package org.fbk.cit.hlt.parsers.hls.exceptions;

/**
 * If tag string doesn't make sense
 */
public class InvalidTagString extends Exception {
    public InvalidTagString() {
        super();
    }
    
    public InvalidTagString(String message) {
        super(message);
    }
}
