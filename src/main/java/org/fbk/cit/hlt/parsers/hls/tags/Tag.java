package org.fbk.cit.hlt.parsers.hls.tags;

/**
 * Common interface for a Tag
 */
public interface Tag {
    public String getName();
    public TagType getType();
    public int minVersion();
    public boolean isOneTime();
    public boolean shouldBeFollowedByURI();
    public boolean shouldBeUnique();
}