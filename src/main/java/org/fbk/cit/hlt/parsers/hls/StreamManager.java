package org.fbk.cit.hlt.parsers.hls;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.fbk.cit.hlt.parsers.hls.util.Pair;
import org.fbk.cit.hlt.parsers.hls.download.*;
import org.fbk.cit.hlt.parsers.hls.persist.*;
import org.fbk.cit.hlt.parsers.hls.exceptions.IllegalTagSequence;

/**
 * Main class for downloading, handling 
 * and outputting a single stream event
 */
public class StreamManager implements DownloaderListener {
    protected URI uri;
    protected String label;
    protected Downloader downloader;
    protected Persister persister;
    protected Logger logger = LoggerFactory.getLogger(StreamManager.class);
    protected PlaylistParser parser = new PlaylistParser();
    protected long nextJobId = 1;
    protected int dropCount = 5;
    protected MasterPlaylist playlist;
    protected MediaPlaylist media;
    
    protected HashMap<Long, Pair<JobType, Downloadable>> jobs = new HashMap<>();
    protected HashMap<Integer, Segment> segments = new HashMap<>();
    protected HashSet<Integer> downloadedSegments = new HashSet<>();

    protected enum JobType {
        MASTER, MEDIA, SEGMENT
    }

    public static final int HIGH_PRIORITY = 241;
    public static final int LOW_PRIORITY = 0;
    public static final int MAX_DROP_COUNT = 5;
    
    /**
     * Create a stream manager
     *
     * @param uri a URI to a Master Playlist
     * @throws URISyntaxException
     */
    public StreamManager(Persister persister, String uri) throws URISyntaxException {
        this.persister = persister;
        this.uri = new URI(uri);
        this.label = uri;
        this.setDownloader(new SimpleDownloader());
    }
    
    public void reset() {
        playlist = null;
        media = null;
        dropCount = MAX_DROP_COUNT;
        jobs.clear();
        downloadedSegments.clear();
    }
    
    public void start(MediaType type) {
        reset();
        info("Starting handling stream");
        
        //Submitting a job for a Master Playlist and waiting until Master Playlist is populated
        submitJob(JobType.MASTER, null, uri);
        downloader.waitUntilEvent();
        
        //Halting on error
        if (playlist == null) {
            warn("Master Playlist haven't been downloaded. Halting");
            return;
        }
        info("Downloaded the Master Playlist");
        
        //Trying to select the best quality media of the selected type
        MediaPlaylist selectedMedia = null;
        for (MediaPlaylist candMedia : playlist.getElements()) {
            if (candMedia.getMediaType() != type) {
                continue;
            }
            if (selectedMedia == null || candMedia.bandwidth > selectedMedia.bandwidth) {
                selectedMedia = candMedia;
            }
        }
        if (selectedMedia == null) {
            info("Can't find media of the selected type. Halting");
            return;
        }

        //Check if we can actually parse the media URL
        URI currentMediaUri = null;
        try {
            currentMediaUri = new URI(selectedMedia.getBaseURI());
        } catch (URISyntaxException e) {
            warn("Can't parse media URI. Halting");
            return;
        }
        
        //Downloading the corresponding Media Playlist
        while (dropCount > 0) {
            if (Thread.currentThread().isInterrupted()) {
                info("Received an interruption. Halting");
                break;
            }
            
            long jobId = getNextJobId();
            MediaPlaylist currentMedia = null;
            try {
                currentMedia = selectedMedia.clone();
            } catch (CloneNotSupportedException e) {
                warn("[" + label + "] Media Playlist couldn't be cloned. Halting");
                return;
            }
            jobs.put(jobId, new Pair<>(JobType.MEDIA, currentMedia));
            downloader.download(jobId, currentMediaUri, HIGH_PRIORITY);
            try {
                while (jobId != downloader.waitUntilEvent()) {
                    //Wait until we catch our Media event
                }
            } catch (Exception e) {
                //If something happened with our queue, we should halt
                e.printStackTrace();
                logger.warn("["+label+"] Halting due to Downloader failure: "+dropCount);
                return;
            }

            //Trying again on error
            if (media == null) {
                logger.warn("["+label+"] Media Playlist haven't been downloaded. Retries left: "+dropCount);
                continue;
            }
            
            ArrayList<Segment> segments = media.getSegments();
            if (segments.size() == 0) {
                logger.warn("[" + label + "] Downloaded empty Media Playlist. Retries left: " + dropCount);
                dropCount--;
            } else {
                logger.info("["+label+"] Downloaded Media Playlist. Segments from "+media.getStartingSequence()+" to "+media.getCurrentSequence());
            }
            
            
            
            //Adding all the segments to job queue
            float sleepDuration = 0;
            for (Segment segment : segments) {
                sleepDuration += segment.getDuration();
                //Checking if we already processed this segment
                if (this.segments.containsKey(segment.getSequence())) {
                    continue;
                }
                URI currentUri = null;
                try {
                    currentUri = new URI(segment.getUri());
                    //Resolve URL for segment, if it is relative
                    if (!currentUri.isAbsolute()) {
                        currentUri = currentMediaUri.resolve(currentUri);
                        segment.setUri(currentUri.toString());
                    }
                } catch (URISyntaxException e) {
                    warn("Can't parse URI for segment #"+segment.getSequence());
                    continue;
                }
                this.segments.put(segment.getSequence(), segment);
                submitJob(JobType.SEGMENT, segment, currentUri);
            }

            //Clearing media, because now it is obsolete
            media = null;
            try {
                //Sleeping for half of the target duration
                Thread.sleep(Math.round(sleepDuration*500));
            } catch (InterruptedException e) {
                //Sleep interruption is fine for us
            }
        }

        info("Downloading halted. Downloaded "+downloadedSegments.size()+" segments");
        try {
            persister.serializeSegmentInfo(segments.values());
        } catch (IOException e) {
            warn("Failed while saving segment index");
        }
    }

    private void submitJob(JobType type, Downloadable dwn, URI uri) {
        submitJob(type, dwn, uri, LOW_PRIORITY);
    }
    
    private void submitJob(JobType type, Downloadable dwn, URI uri, int priority) {
        long jobId = getNextJobId();
        jobs.put(jobId, new Pair<>(type, dwn));
        downloader.download(jobId, uri, priority);
    }

    private long getNextJobId() {
        return nextJobId++;
    }

    @Override
    public void onFinish(long jobId, byte[] result) {
        logger.debug("[" + label + "] job #"+jobId+" OK");
        Pair<JobType, Downloadable> job = jobs.get(jobId);
        switch (job.getKey()) {
            case MASTER:
                try {
                    try {
                        persister.savePlaylist("master", result);
                    } catch (IOException e) {
                        warn("Master Playlist save failed: " + e.getClass().getSimpleName()+" "+e.getMessage());
                    }
                    playlist = parser.parseTwitchMaster(new String(result));
                } catch (IllegalTagSequence illegalTagSequence) {
                    warn("Parsing failed for Master Playlist");
                    dropCount--;
                    return;
                }
                break;
            case MEDIA:
                try {
                    try {
                        persister.savePlaylist("media", result);
                    } catch (IOException e) {
                        warn("Media Playlist save failed: " + e.getClass().getSimpleName()+" "+e.getMessage());
                    }
                    media = (MediaPlaylist) parser.parse((Playlist) job.getValue(), new String(result));
                } catch (IllegalTagSequence illegalTagSequence) {
                    warn("Parsing failed for Media Playlist");
                    dropCount--;
                    return;
                }
                break;
            case SEGMENT:
                Segment segment = (Segment) job.getValue();
                downloadedSegments.add(segment.getSequence());
                info("Downloaded segment #" + segment.getSequence());
                try {
                    persister.saveSegment(segment.getSequence(), result);
                } catch (IOException e) {
                    warn("Segment save failed: " + e.getClass().getSimpleName()+" "+e.getMessage());
                }
                break;
        }
        
        //Restore the amount of allowed failures
        if (dropCount < MAX_DROP_COUNT) {
            dropCount++;
        }
    }

    @Override
    public void onError(long jobId, Exception e) {
        Pair<JobType, Downloadable> job = jobs.get(jobId);
        warn("Download job of type \"" + job.getKey().name() + "\" has failed: " + e.getClass().getSimpleName()+" "+e.getMessage());
        dropCount--;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public URI getUri() {
        return uri;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public void setDownloader(Downloader downloader) {
        if (this.downloader != null) {
            this.downloader.unsubscribe(this);
        }
        this.downloader = downloader;
        this.downloader.subscribe(this);
    }
    
    private void info(String text) {
        logger.info("[" + label + "] " + text);
    }
    
    private void warn(String text) {
        logger.info("[" + label + "] " + text);
    }
    
    public static void main(String args[]) {
        //Options specification
        Options options = new Options();
        options.addOption("u", "url", true, "URL of the Master Playlist to download");
        options.addOption("l", "label", true, "Convenient label of the stream");
        options.addOption("o", "out", true, "Output folder");
        
        CommandLineParser parser = new PosixParser();
        CommandLine line;
        StreamManager manager;
        Persister persister;
        try {
            line = parser.parse(options, args);
            if (!line.hasOption("url")) {
                throw new ParseException("Missing parameter url");
            }
            if (!line.hasOption("out")) {
                throw new ParseException("Missing parameter out");
            }

            persister = new FilePersister(new File(line.getOptionValue("out")));
            try {
                manager = new StreamManager(persister, line.getOptionValue("url"));
            } catch (URISyntaxException e) {
                throw new ParseException("Malformed url");
            }
        } catch (Exception e) {
            String footer = "\nError: "+e.getMessage();
            new HelpFormatter().printHelp(400, "java -jar hls-0.1.jar", "\n", options, footer, true);
            return;
        }

        if (line.hasOption("label")) {
            manager.setLabel(line.getOptionValue("label"));
        }
        manager.start(MediaType.VIDEO);
    }
}
