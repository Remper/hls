package org.fbk.cit.hlt.parsers.hls.tags.master;

import org.fbk.cit.hlt.parsers.hls.MediaType;
import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;
import org.fbk.cit.hlt.parsers.hls.tags.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * #EXT-X-STREAM-INF
 */
@HLSTag(name="EXT-X-STREAM-INF")
public class StreamInfTag extends TagWithAttributeList implements Tag {
    protected static final Logger logger = LoggerFactory.getLogger(StreamInfTag.class);
    protected long bandwidth;
    protected long averageBandwidth;
    protected String codecs;
    protected Resolution resolution;
    protected MediaType mediaType;
    protected String groupId;

    public StreamInfTag(String propertyString) throws InvalidTagParameters {
        super(propertyString);
        if (bandwidth == 0) {
            throw InvalidTagParameters.required("BANDWIDTH");
        }
        if (averageBandwidth == 0) {
            averageBandwidth = bandwidth;
        }
        if (mediaType == null) {
            throw InvalidTagParameters.required("VIDEO or AUDIO or SUBTITLES or CLOSED-CAPTIONS");
        }
        if (codecs == null) {
            codecs = "";
        }
    }

    protected void resolveParameter(String name, long number) throws InvalidTagParameters {
        switch (name) {
            case "BANDWIDTH":
                bandwidth = number;
                return;
            case "AVERAGE-BANDWIDTH":
                averageBandwidth = number;
                return;
            case "PROGRAM-ID":
                logger.warn("Integer parameter PROGRAM-ID shouldn't be used for tag "+this.getName());
                return;
        }

        throw new InvalidTagParameters("Invalid parameter name for an integer parameter: \"" + name + "\"");
    }
    protected void resolveParameter(String name, float number) throws InvalidTagParameters {
        throw new InvalidTagParameters("Invalid parameter name for a float parameter: \"" + name + "\"");
    }
    protected void resolveParameter(String name, String string) throws InvalidTagParameters {
        switch (name) {
            case "CODECS":
                codecs = string;
                return;
            default:
                throw new InvalidTagParameters("Invalid parameter name for a string parameter: \"" + name + "\"");
            case "AUDIO":
                mediaType = MediaType.AUDIO;
                break;
            case "VIDEO":
                mediaType = MediaType.VIDEO;
                break;
            case "SUBTITLES":
                mediaType = MediaType.SUBTITLES;
                break;
            case "CLOSED-CAPTIONS":
                mediaType = MediaType.CLOSED_CAPTIONS;
                break;
        }
        groupId = string;
    }
    protected void resolveParameter(String name, Resolution resolution) throws InvalidTagParameters {
        if (!name.equals("RESOLUTION")) {
            throw new InvalidTagParameters();
        }
        this.resolution = resolution;
    }

    @Override
    public String getName() {
        return "EXT-X-STREAM-INF";
    }

    @Override
    public TagType getType() {
        return TagType.MASTER_PLAYLIST;
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
        return true;
    }

    @Override
    public boolean shouldBeUnique() {
        return false;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public long getAverageBandwidth() {
        return averageBandwidth;
    }

    public String getCodecs() {
        return codecs;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getGroupId() {
        return groupId;
    }
}