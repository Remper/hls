package org.fbk.cit.hlt.parsers.hls.download;

import javafx.util.Pair;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Simple single-threaded downloader to test everything
 */
public class SimpleDownloader implements Downloader {
    private HashSet<DownloaderListener> listeners = new HashSet<>();
    private LinkedList<Long> completedJobs = new LinkedList<>();
    
    public static final String USER_AGENT = "Mechanical Mockingbird";
    
    @Override
    public void download(long jobId, URI uri) {
        download(jobId, uri, 0);
    }

    @Override
    public void download(long jobId, URI uri, int priority) {
        URLConnection conn;
        try {
            conn = uri.toURL().openConnection();
        } catch (IOException e) {
            File file = new File(uri);
            if (file.exists() && file.canRead()) {
                try {
                    resolve(jobId, IOUtils.toByteArray(new FileReader(file)));
                    return;
                } catch (Exception e1) {
                    error(jobId, e1);
                    return;
                }
            }
            error(jobId, e);
            return;
        }
        
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        try {
            conn.connect();
            resolve(jobId, IOUtils.toByteArray(conn.getInputStream()));
        } catch (IOException e) {
            error(jobId, e);
        }
    }
    
    private void resolve(long jobId, byte[] data) {
        for (DownloaderListener listener : listeners) {
            listener.onFinish(jobId, data);
        }
        completedJobs.push(jobId);
    }
    
    private void error(long jobId, Exception e) {
        for (DownloaderListener listener : listeners) {
            listener.onError(jobId, e);
        }
        completedJobs.push(jobId);
    } 

    @Override
    public void subscribe(DownloaderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(DownloaderListener listener) {
        listeners.remove(listener);
    }

    @Override
    public long waitUntilEvent() {
        return completedJobs.pop();
    }
}
