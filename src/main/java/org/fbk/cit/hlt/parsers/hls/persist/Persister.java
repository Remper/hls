package org.fbk.cit.hlt.parsers.hls.persist;

import org.fbk.cit.hlt.parsers.hls.Segment;

import java.io.IOException;
import java.util.Collection;

/**
 * A common interface for saving stream data to storage
 */
public interface Persister {
    public void savePlaylist(String type, byte[] data) throws IOException;
    public void saveSegment(int sequence, byte[] data) throws IOException;
    public void serializeSegmentInfo(Collection<Segment> segment) throws IOException;
    public void flush();
}
