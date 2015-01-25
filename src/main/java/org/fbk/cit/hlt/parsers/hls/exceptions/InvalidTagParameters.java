package org.fbk.cit.hlt.parsers.hls.exceptions;

/**
 * An exception that occurs if the list of attributes that are given to tag don't match to it's specification
 */
public class InvalidTagParameters extends Exception {
    public InvalidTagParameters() {
        super();
    }

    public InvalidTagParameters(String message) {
        super(message);
    }

    public static InvalidTagParameters required(String parameter) {
        return new InvalidTagParameters("Required "+parameter+" parameter is missing");
    }
}
