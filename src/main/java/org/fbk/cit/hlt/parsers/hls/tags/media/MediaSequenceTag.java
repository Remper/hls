package org.fbk.cit.hlt.parsers.hls.tags.media;

import org.fbk.cit.hlt.parsers.hls.tags.*;
import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;

/**
 * #EXT-X-MEDIA-SEQUENCE
 */
@HLSTag(name="EXT-X-MEDIA-SEQUENCE")
public class MediaSequenceTag implements Tag {
    protected int sequence;

    public MediaSequenceTag(String properties) throws InvalidTagParameters {
        if (!properties.matches("[0-9]+")) {
            throw new InvalidTagParameters();
        }

        sequence = Integer.valueOf(properties);
    }

    @Override
    public String getName() {
        return "EXT-X-MEDIA-SEQUENCE";
    }

    @Override
    public TagType getType() {
        return TagType.MEDIA_PLAYLIST;
    }

    @Override
    public int minVersion() {
        return 0;
    }

    @Override
    public boolean isOneTime() {
        return false;
    }

    @Override
    public boolean shouldBeFollowedByURI() {
        return false;
    }

    @Override
    public boolean shouldBeUnique() {
        return true;
    }

    public int getSequence() {
        return sequence;
    }
}
