package org.fbk.cit.hlt.parsers.hls.download;

/**
 * Listener for Downloader events
 */
public interface DownloaderListener {
    /**
     * Invoked upon successful completion of the download task
     *  
     * DON'T retain the resulting array. 
     * Convert it to something else before the completion of onFinish
     * 
     * @param jobId id of the job
     * @param result resulting byte array
     */
    public void onFinish(long jobId, byte[] result);

    /**
     * Invokes upon error while downloading
     *
     * @param jobId id of the job
     * @param e exception which caused failure
     */
    public void onError(long jobId, Exception e);
}
