package org.fbk.cit.hlt.parsers.hls.persist;

import java.io.IOException;

/**
 * A common interface for saving stream data to storage
 */
public interface Persister {
    public void savePlaylist(String type, byte[] data) throws IOException;
    public void saveSegment(int sequence, byte[] data) throws IOException;
}
