package org.fbk.cit.hlt.parsers.hls.tags;

import org.fbk.cit.hlt.parsers.hls.exceptions.*;
import org.reflections.Reflections;

import java.util.HashMap;

/**
 * Class that returns Tags
 */
public class TagFactory {
    private static HashMap<String, Class<?>>tags;
    private int version;
    private TagType currentType;

    public TagFactory() {
        this(7, TagType.PLAYLIST);
    }

    public TagFactory(int version, TagType currentType) {
        this.version = version;
        this.currentType = currentType;
    }

    public Tag getTag(String tagString) throws IllegalTagSequence, InvalidTagVersion, InvalidTagString {
        int tagDelimiter = tagString.indexOf(':');
        String tagName = tagString.substring(1, tagDelimiter-1);
        Tag tag;
        try {
            tag = (Tag) getTags().get(tagName).getConstructor(String.class).newInstance(tagString.substring(tagDelimiter+1));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTagString();
        }

        if (version < tag.minVersion()) {
            throw new InvalidTagVersion();
        }

        if (tag.getType() != TagType.PLAYLIST) {
            if (tag.getType() != currentType && currentType != TagType.PLAYLIST) {
                throw new IllegalTagSequence();
            }
            currentType = tag.getType();
        }

        return tag;
    }

    public static HashMap<String, Class<?>> getTags() {
        return tags == null ? buildTags() : tags;
    }

    private static HashMap<String, Class<?>> buildTags() {
        Reflections ref = new Reflections(Tag.class.getPackage().getName());
        tags = new HashMap<>();

        for (Class<?> tagClass : ref.getTypesAnnotatedWith(HLSTag.class)) {
            //If a class doesn't implement correct interface — just ignore it
            if (!tagClass.isAssignableFrom(Tag.class)) {
                continue;
            }

            tags.put(tagClass.getAnnotation(HLSTag.class).name(), tagClass);
        }
        return tags;
    }

    public TagType getCurrentType() {
        return currentType;
    }

    public void setCurrentType(TagType currentType) {
        this.currentType = currentType;
    }
}
