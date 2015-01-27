package org.fbk.cit.hlt.parsers.hls;

import org.fbk.cit.hlt.parsers.hls.download.Downloadable;
import org.fbk.cit.hlt.parsers.hls.tags.Encryption;

/**
 * Media Sequence or any other media file
 */
public class Segment implements Downloadable {
    private float duration;
    private String title;
    private String uri;
    private Encryption encryption;
    private int sequence;
    private int discontinuity;

    public Segment(float duration, String title, String uri) {
        this(duration, title, uri, 0);
    }

    public Segment(float duration, String title, String uri, int sequence) {
        this.duration = duration;
        this.title = title;
        this.uri = uri;
        this.encryption = null;
        this.sequence = sequence;
    }

    public float getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public int getSequence() {
        return sequence;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public int getDiscontinuity() {
        return discontinuity;
    }

    public void setDiscontinuity(int discontinuity) {
        this.discontinuity = discontinuity;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
