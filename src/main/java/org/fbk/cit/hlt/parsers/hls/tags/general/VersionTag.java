package org.fbk.cit.hlt.parsers.hls.tags.general;

import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;
import org.fbk.cit.hlt.parsers.hls.tags.HLSTag;
import org.fbk.cit.hlt.parsers.hls.tags.Tag;
import org.fbk.cit.hlt.parsers.hls.tags.TagType;

/**
 * #EXT-X-VERSION
 */
@HLSTag(name="EXT-X-VERSION")
public class VersionTag implements Tag {
    protected int version;
    
    public VersionTag(String properties) throws InvalidTagParameters {
        if (!properties.matches("[0-9]+")) {
            throw new InvalidTagParameters();
        }

        version = Integer.valueOf(properties);
    }
    
    @Override
    public String getName() {
        return "EXT-X-VERSION";
    }

    @Override
    public TagType getType() {
        return TagType.PLAYLIST;
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

    public int getVersion() {
        return version;
    }
}
