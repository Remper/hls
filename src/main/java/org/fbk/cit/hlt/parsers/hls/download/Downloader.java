package org.fbk.cit.hlt.parsers.hls.download;

import java.net.URI;

/**
 * Main interface for different asynchronous downloaders that can
 * execute tasks provided by StreamManager 
 */
public interface Downloader {
    public void download(long jobId, URI uri);
    public void download(long jobId, URI uri, int priority);
    public void subscribe(DownloaderListener listener);
    public void unsubscribe(DownloaderListener listener);
    public long waitUntilEvent();
}
