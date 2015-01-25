package org.fbk.cit.hlt.parsers.hls.tags.media;

import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;
import org.fbk.cit.hlt.parsers.hls.tags.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * #EXTINF:<duration>,<title>
 *
 * Duration is decimal
 * Title is an unquoted string
 */
@HLSTag(name="EXTINF")
public class ExtInfTag implements Tag {
    protected String title;
    protected float duration;

    public static final String DURATION = "[0-9]+(?:\\.[0-9]+)?";
    public static final String TITLE = "[a-zA-Z]*";

    public ExtInfTag(String properties) throws InvalidTagParameters {
        Matcher m = Pattern.compile("("+DURATION+"),("+TITLE+")").matcher(properties);
        if (!m.find()) {
            //Throw error if tag provided with wrong parameters
            throw new InvalidTagParameters();
        }

        duration = Float.valueOf(m.group(1));
        title = m.group(2);
    }

    @Override
    public String getName() {
        return "EXTINF";
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
        return true;
    }

    @Override
    public boolean shouldBeFollowedByURI() {
        return true;
    }

    @Override
    public boolean shouldBeUnique() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    public float getDuration() {
        return duration;
    }
}
