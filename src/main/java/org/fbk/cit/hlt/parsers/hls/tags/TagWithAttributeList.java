package org.fbk.cit.hlt.parsers.hls.tags;

import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class for tags that have an AttributeList as a parameter
 * Parses an input list of parameters and outputs typed pairs to child class
 */
public abstract class TagWithAttributeList {
    public static final String ATTRIBUTE_NAME = "([a-zA-Z0-9-]+)";
    public static final String VALUE_INTEGER = "([0-9]+)";
    public static final String VALUE_HEX = "(?:0x([0-9A-Fa-f]+))";
    public static final String VALUE_FLOAT = "(-?[0-9]+(?:\\.[0-9]+)?)";
    public static final String VALUE_STRING = "(?:\"([^\"\\n\\r]*)\")";
    public static final String VALUE_ENUM = "([A-Za-z]+)";
    public static final String VALUE_RESOLUTION = "([0-9]+x[0-9]+)";
    public static final String ATTRIBUTE_VALUE = "(" + VALUE_ENUM
            + "|" + VALUE_INTEGER + "|" + VALUE_HEX
            + "|" + VALUE_FLOAT + "|" + VALUE_STRING + "|" + VALUE_RESOLUTION + ")";
    public static final String ATTRIBUTE_PAIR = "(" + ATTRIBUTE_NAME + "=" + ATTRIBUTE_VALUE + ")";

    public TagWithAttributeList(String propertyString) throws InvalidTagParameters {
        Matcher m = Pattern.compile(ATTRIBUTE_PAIR + "(?:,|$)").matcher(propertyString);
        while (m.find()) {
            String name = m.group(2);
            if (m.group(4) != null) {
                resolveParameter(name, m.group(4));
                continue;
            }
            if (m.group(5) != null) {
                resolveParameter(name, Long.valueOf(m.group(5)));
                continue;
            }
            if (m.group(6) != null) {
                resolveParameter(name, Long.parseLong(m.group(6), 16));
                continue;
            }
            if (m.group(7) != null) {
                resolveParameter(name, Float.valueOf(m.group(7)));
                continue;
            }
            if (m.group(7) != null) {
                resolveParameter(name, Float.valueOf(m.group(7)));
                continue;
            }
            if (m.group(8) != null) {
                resolveParameter(name, m.group(8));
                continue;
            }
            String[] resolution = m.group(9).split("x");
            resolveParameter(name, new Resolution(Integer.valueOf(resolution[0]), Integer.valueOf(resolution[1])));
        }

        if (!m.hitEnd()) {
            throw new InvalidTagParameters();
        }
    }

    //At least one of the following functions should be overridden, otherwise this class doesn't make any sense
    protected void resolveParameter(String name, long number) throws InvalidTagParameters {
        throw new InvalidTagParameters("Invalid parameter name for an integer parameter: \"" + name + "\"");
    }
    protected void resolveParameter(String name, float number) throws InvalidTagParameters {
        throw new InvalidTagParameters("Invalid parameter name for a float parameter: \"" + name + "\"");
    }
    protected void resolveParameter(String name, String string) throws InvalidTagParameters {
        throw new InvalidTagParameters("Invalid parameter name for a string parameter: \"" + name + "\"");
    }
    protected void resolveParameter(String name, Resolution resolution) throws InvalidTagParameters {
        throw new InvalidTagParameters("Invalid parameter name for a resolution parameter: \"" + name + "\"");
    }

    public static class Resolution {
        int width;
        int height;
        public Resolution(int width, int height) {this.width = width; this.height = height;}
    }
}
