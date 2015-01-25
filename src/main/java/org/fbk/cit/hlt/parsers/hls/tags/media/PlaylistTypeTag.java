package org.fbk.cit.hlt.parsers.hls.tags.media;

import org.fbk.cit.hlt.parsers.hls.MediaPlaylistType;
import org.fbk.cit.hlt.parsers.hls.tags.*;
/**
 * #EXT-X-PLAYLIST-TYPE
 */
@HLSTag(name = "EXT-X-PLAYLIST-TYPE")
public class PlaylistTypeTag implements Tag {
    protected MediaPlaylistType playlistType;

    public PlaylistTypeTag(String properties) {
        playlistType = properties.equalsIgnoreCase("EVENT") ? MediaPlaylistType.EVENT : MediaPlaylistType.VOD;
    }

    @Override
    public String getName() {
        return "EXT-X-PLAYLIST-TYPE";
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
        return false;
    }

    public MediaPlaylistType getPlaylistType() {
        return playlistType;
    }
}
