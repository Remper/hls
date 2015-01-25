package org.fbk.cit.hlt.parsers.hls.tags;

/**
 * Encryption info usually specified by #EXT-X-KEY
 */
public class Encryption {
    public String method;
    public String uri;
    public String iv; //Initialization vector
    public String keyformat;
    public String keyformatversions;
}
