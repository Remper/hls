package org.fbk.cit.hlt.parsers.hls;

import org.fbk.cit.hlt.parsers.hls.tags.Tag;
import org.fbk.cit.hlt.parsers.hls.tags.TagWithAttributeList;
import org.fbk.cit.hlt.parsers.hls.tags.media.*;
import org.fbk.cit.hlt.parsers.hls.exceptions.*;

import java.util.ArrayList;

/**
 * Media playlist entity
 */
public class MediaPlaylist extends Playlist implements Cloneable {
    protected int currentSequence;
    protected int currentDiscontinuity;
    protected ArrayList<Segment> segments;
    protected String baseURI;
    protected MediaPlaylistType type;
    protected MediaType mediaType;
    protected long bandwidth = 0;
    protected long averageBandwidth = 0;
    protected String codecs;
    protected TagWithAttributeList.Resolution resolution;
    protected String groupId;
    protected String language;
    protected String assocLanguage;
    protected String description;
    protected float targetDuration;
    protected boolean isSealed;
    protected int startingSequence;

    public MediaPlaylist(String baseURI, MediaType mediaType, String groupId) {
        super();
        this.baseURI = baseURI;
        segments = new ArrayList<>();
        this.mediaType = mediaType;
        this.groupId = groupId;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    @Override
    public void applyTag(Tag tag) throws IllegalTagSequence {
        //General playlist tags
        super.applyTag(tag);

        //MediaPlaylist-specific tags
        if (tag instanceof PlaylistTypeTag) {
            type = ((PlaylistTypeTag) tag).getPlaylistType();
            return;
        }

        if (tag instanceof TargetDurationTag) {
            targetDuration = ((TargetDurationTag) tag).getTargetDuration();
            return;
        }

        if (tag instanceof MediaSequenceTag) {
            int newSequence = ((MediaSequenceTag) tag).getSequence();
            if (newSequence < currentSequence) {
                throw new IllegalTagSequence("New Media Sequence shouldn't be less than current");
            }
            currentSequence = newSequence;
            startingSequence = currentSequence;
            return;
        }

        if (tag instanceof DiscontinuitySequenceTag) {
            int newSequence = ((DiscontinuitySequenceTag) tag).getSequence();
            if (newSequence < currentDiscontinuity) {
                throw new IllegalTagSequence("New Discontinuity Sequence shouldn't be less than current");
            }
            currentDiscontinuity = newSequence;
            return;
        }

        if (tag instanceof EndListTag) {
            isSealed = true;
        }
    }

    /**
     * Add segment by uri, applying all the tags on the way
     */
    public void addURL(String uri) throws RequiredTagMissing {
        if (!isValid() || isSealed) {
            throw new RequiredTagMissing();
        }

        //If there was no EXTINF just before URI — throw an exception
        Tag lastTag = tags.get(tags.size()-1);

        if (!(lastTag instanceof ExtInfTag)) {
            throw new RequiredTagMissing();
        }

        ExtInfTag infotag = (ExtInfTag) lastTag;
        Segment segment = new Segment(infotag.getDuration(), infotag.getTitle(), uri, currentSequence++);

        segment.setDiscontinuity(currentDiscontinuity++);
        segments.add(segment);
        awaitingURI = false;
    }

    public float getTargetDuration() {
        return targetDuration;
    }

    public MediaPlaylistType getType() {
        return type;
    }

    public String getCodecs() {
        return codecs;
    }

    public void setCodecs(String codecs) {
        this.codecs = codecs;
    }

    public String getGroupId() {
        return groupId;
    }

    public TagWithAttributeList.Resolution getResolution() {
        return resolution;
    }

    public void setResolution(TagWithAttributeList.Resolution resolution) {
        this.resolution = resolution;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public long getAverageBandwidth() {
        return averageBandwidth;
    }

    public void setAverageBandwidth(long averageBandwidth) {
        this.averageBandwidth = averageBandwidth;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAssocLanguage() {
        return assocLanguage;
    }

    public void setAssocLanguage(String assocLanguage) {
        this.assocLanguage = assocLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStartingSequence() {
        return startingSequence;
    }

    public int getCurrentSequence() {
        return currentSequence;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public boolean isSealed() {
        return isSealed;
    }

    @Override
    public MediaPlaylist clone() throws CloneNotSupportedException {
        MediaPlaylist cloned = (MediaPlaylist) super.clone();
        cloned.segments = new ArrayList<>();
        if (resolution != null) {
            cloned.resolution = resolution.clone();
        }

        return cloned;
    }
}
