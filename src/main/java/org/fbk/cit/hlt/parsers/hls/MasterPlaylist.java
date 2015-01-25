package org.fbk.cit.hlt.parsers.hls;

import org.fbk.cit.hlt.parsers.hls.exceptions.*;
import org.fbk.cit.hlt.parsers.hls.tags.Tag;
import org.fbk.cit.hlt.parsers.hls.tags.master.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Master playlist entity
 */
public class MasterPlaylist extends Playlist {
    protected ArrayList<MediaPlaylist> elements = new ArrayList<>();

    @Override
    public void applyTag(Tag tag) throws IllegalTagSequence {
        //General playlist tags
        super.applyTag(tag);

        //MasterPlaylist-specific tags
    }

    @Override
    public void addURL(String uri) throws RequiredTagMissing {
        if (!isValid()) {
            throw new RequiredTagMissing();
        }

        //If there was no EXT-X-STREAM-INF just before the URI — throw an exception
        Tag lastTag = tags.get(tags.size()-1);

        if (!(lastTag instanceof StreamInfTag)) {
            throw new RequiredTagMissing();
        }

        //We have to check if there is a corresponding MediaTag
        StreamInfTag streamInf = ((StreamInfTag) lastTag);
        MediaType media = streamInf.getMediaType();
        String groupId = streamInf.getGroupId();

        MediaTag mediaTag = null;
        for (Tag tag : tags) {
            if (!(tag instanceof MediaTag)) {
                continue;
            }

            MediaTag candMediaTag = (MediaTag) tag;
            if (candMediaTag.getMediaType() == media && candMediaTag.getGroupId().equals(groupId)) {
                mediaTag = candMediaTag;
            }
        }
        if (mediaTag == null) {
            throw new RequiredTagMissing();
        }

        MediaPlaylist playlist = new MediaPlaylist(uri, media, groupId);

        playlist.setBandwidth(streamInf.getBandwidth());
        playlist.setAverageBandwidth(streamInf.getAverageBandwidth());
        playlist.setCodecs(streamInf.getCodecs());
        playlist.setLanguage(mediaTag.getLanguage());
        playlist.setAssocLanguage(mediaTag.getAssocLanguage());
        playlist.setDescription(mediaTag.getMediaName());
        elements.add(playlist);
        awaitingURI = false;
    }

    /**
     * Get elements of the playlist.
     * Playlist would appear to be empty if it considers itself invalid
     *
     * @return List of the elements
     */
    public ArrayList<MediaPlaylist> getElements() {
        return isValid() ? elements : new ArrayList<>();
    }

    public boolean isEmpty() {
        return !isValid() || elements.size() == 0;
    }
}
