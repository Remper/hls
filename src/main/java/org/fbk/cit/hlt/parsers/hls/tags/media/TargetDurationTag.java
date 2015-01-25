package org.fbk.cit.hlt.parsers.hls.tags.media;

import org.fbk.cit.hlt.parsers.hls.tags.*;
import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;

/**
 * #EXT-X-TARGETDURATION
 */
@HLSTag(name="EXT-X-TARGETDURATION")
public class TargetDurationTag implements Tag {
    protected float targetDuration;

    public TargetDurationTag(String properties) throws InvalidTagParameters {
        if (!properties.matches("[0-9]+(\\.[0-9]+)?")) {
            throw new InvalidTagParameters();
        }

        targetDuration = Float.valueOf(properties);
    }

    @Override
    public String getName() {
        return "EXT-X-TARGETDURATION";
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

    public float getTargetDuration() {
        return targetDuration;
    }
}
