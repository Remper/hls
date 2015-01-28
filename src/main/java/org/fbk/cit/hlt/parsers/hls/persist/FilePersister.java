package org.fbk.cit.hlt.parsers.hls.persist;

import org.apache.commons.io.IOUtils;
import org.fbk.cit.hlt.parsers.hls.Segment;

import java.io.*;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Class that saves data to the filesystem
 */
public class FilePersister implements Persister {
    protected File playlists;
    protected File segments;
    protected BufferedWriter infoWriter;
    
    /**
     * Folder to which we save all the data
     * 
     * @param folder target folder
     */
    public FilePersister(File folder) throws FileSystemException {
        if (!folder.exists() && !folder.mkdir()) {
            throw new FileSystemException("Can't create target directory");
        }
        if (!folder.isDirectory()) {
            throw new FileSystemException("Target is not a folder");
        }
        
        //Creating subdirectories
        playlists = new File(getChildPath(folder.toString(), "playlists"));
        segments = new File(getChildPath(folder.toString(), "segments"));
        if ((!playlists.exists() && !playlists.mkdir()) || (!segments.exists() && !segments.mkdir())) {
            throw new FileSystemException("Can't create subdirectories");
        }
        if (!playlists.isDirectory() || !segments.isDirectory()) {
            throw new FileSystemException("Sanity check #1 failed");
        }
    }
    
    @Override
    public void savePlaylist(String type, byte[] data) throws IOException {
        String name = type+"-"+(new SimpleDateFormat("yyyy.MM.dd-HH-mm-ss").format(new Date()))+".m3u8";
        saveFile(playlists, name, data);
    }

    @Override
    public void saveSegment(int sequence, byte[] data) throws IOException {
        String name = sequence+".ts";
        saveFile(segments, name, data);
    }

    @Override
    public void serializeSegmentInfo(Collection<Segment> segments) throws IOException {
        if (infoWriter == null) {
            startWritingInfo();
        }
        for (Segment segment : segments) {
            infoWriter.write(segment.getSequence()+","+segment.getTitle()+","+segment.getDuration()+"\r\n");
        }
        flush();
    }
    
    @Override
    public void flush() {
        if (infoWriter == null) {
            return;
        }
        try {
            infoWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        infoWriter = null;
    }
    
    private void startWritingInfo() throws IOException {
        infoWriter = new BufferedWriter(new FileWriter(new File(getChildPath(segments.toString(), "index.csv")), true));
        infoWriter.write("sequence_id,title,duration\r\n");
    }

    private void saveFile(File folder, String name, byte[] data) throws IOException {
        File output = new File(getChildPath(folder.toString(), name));
        try (FileOutputStream stream = new FileOutputStream(output)) {
            IOUtils.write(data, stream);
        }
    }

    public File getPlaylists() {
        return playlists;
    }

    public void setPlaylists(File playlists) {
        this.playlists = playlists;
    }

    public File getSegments() {
        return segments;
    }

    public void setSegments(File segments) {
        this.segments = segments;
    }

    private String getChildPath(String parent, String child) {
        return parent + "/" + child;
    }
}
