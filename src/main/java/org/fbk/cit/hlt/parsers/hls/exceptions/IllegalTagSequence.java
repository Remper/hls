package org.fbk.cit.hlt.parsers.hls.exceptions;

/**
 * Meaning that you are trying to add a tag that shouldn't be in this context
 */
public class IllegalTagSequence extends Exception {
    public IllegalTagSequence() {
        super();
    }

    public IllegalTagSequence(String message) {
        super(message);
    }
}
