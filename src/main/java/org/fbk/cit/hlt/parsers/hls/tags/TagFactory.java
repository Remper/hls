package org.fbk.cit.hlt.parsers.hls.tags;

import org.fbk.cit.hlt.parsers.hls.exceptions.*;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

/**
 * Class that returns Tags
 */
public class TagFactory {
    private static final Logger logger = LoggerFactory.getLogger(TagFactory.class);
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
        //Splitting tag string into the tag name 
        int tagDelimiter = tagString.indexOf(':');
        String tagName = "";
        String tagProperties = "";
        if (tagDelimiter == -1) {
            tagName = tagString.substring(1);
        } else {
            tagName = tagString.substring(1, tagDelimiter-1);
            tagProperties = tagString.substring(tagDelimiter+1);
        }
        
        //Trying to instantiate the right tag
        Tag tag;
        Class<?> tagClass = getTags().get(tagName);
        if (tagClass == null) {
            throw new InvalidTagString("Tag with this name not found");
        }
        try {
            tag = (Tag) tagClass.getConstructor(String.class).newInstance(tagProperties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTagString("Cannot instantiate a tag with this name.");
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

        Set<Class<?>> types = ref.getTypesAnnotatedWith(HLSTag.class);
        logger.debug("Building tags. Found " + types.size() + " Tag candidates");
        for (Class<?> tagClass : types) {
            //If a class doesn't implement correct interface — just ignore it
            if (!Tag.class.isAssignableFrom(tagClass)) {
                continue;
            }

            tags.put(tagClass.getAnnotation(HLSTag.class).name(), tagClass);
        }
        logger.info("Building tags. Found " + tags.size() + " tags");
        return tags;
    }

    public TagType getCurrentType() {
        return currentType;
    }

    public void setCurrentType(TagType currentType) {
        this.currentType = currentType;
    }

    public static Logger getLogger() {
        return logger;
    }
}
