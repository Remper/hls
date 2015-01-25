package org.fbk.cit.hlt.parsers.hls;

import org.fbk.cit.hlt.parsers.hls.exceptions.IllegalTagSequence;
import org.fbk.cit.hlt.parsers.hls.tags.*;

import java.util.ArrayList;

/**
 * Class that contains the main logic for playlist parsing
 */
public class PlaylistParser {
    private int version;

    /**
     * Create a parser that accepts playlists of version 7
     * as described in HTTP Live Streaming protocol draft
     * @see <a href="http://tools.ietf.org/html/draft-pantos-http-live-streaming-14">draft</a>
     */
    public PlaylistParser() {
        this(7);
    }

    /**
     * Create a parser that accepts playlists of the specified version
     * as described in HTTP Live Streaming protocol draft
     * @see <a href="http://tools.ietf.org/html/draft-pantos-http-live-streaming-14">draft</a>
     *
     * @param version Version of the protocol
     */
    public PlaylistParser(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public MasterPlaylist parseMaster(String playlistStr) throws IllegalTagSequence {
        return (MasterPlaylist) parse(new MasterPlaylist(), playlistStr);
    }

    public MasterPlaylist parseTwitchMaster(String playlistStr) throws IllegalTagSequence {
        return (MasterPlaylist) parse(new org.fbk.cit.hlt.parsers.hls.extensions.twitchtv.MasterPlaylist(), playlistStr);
    }

    /**
     * Create a playlist from string
     *
     * @param playlist playlist to which tags should be applied
     * @param playlistStr string that contains a raw part of the playlist in UTF-8 encoding
     * @return a new playlist parsed from string or null if error
     */
    public Playlist parse(Playlist playlist, String playlistStr) throws IllegalTagSequence {
        ArrayList<String> tagStrings = splitToTagStrings(playlistStr);
        TagFactory factory = new TagFactory(version, TagType.PLAYLIST);
        try {
            for (String string : tagStrings) {
                if (string.startsWith("#EXT")) {
                    playlist.applyTag(factory.getTag(string));
                    continue;
                }
                if (!string.startsWith("#")) {
                    playlist.addURL(string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return playlist;
    }

    /**
     * Split playlist into single lines, merging slashed strings and ignoring empty lines
     *
     * @param playlist raw playlist
     * @return array of tag lines
     */
    public ArrayList<String> splitToTagStrings(String playlist) {
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean cleanWhiteSpace = false;
        for (char ch : playlist.toCharArray()) {
            switch (ch) {
                case '\r':
                    break;
                case ' ':
                    if (!cleanWhiteSpace) {
                        builder.append(ch);
                    }
                    break;
                case '\n':
                    if (cleanWhiteSpace || builder.length() == 0) {
                        break;
                    }

                    lines.add(builder.toString());
                    builder = new StringBuilder();
                    break;
                case '\\':
                    cleanWhiteSpace = true;
                    break;
                default:
                    cleanWhiteSpace = false;
                    builder.append(ch);
            }
        }

        return lines;
    }
}
