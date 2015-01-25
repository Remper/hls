package org.fbk.cit.hlt.parsers.hls.tags.extensions.twitchtv;

import org.fbk.cit.hlt.parsers.hls.exceptions.InvalidTagParameters;
import org.fbk.cit.hlt.parsers.hls.tags.*;

/**
 * #EXT-X-TWITCH-INFO
 */
@HLSTag(name="EXT-X-TWITCH-INFO")
public class TwitchInfoTag extends TagWithAttributeList implements Tag {
    protected String node;
    protected String manifestNode;
    protected String userIP;
    protected String cluster;
    protected String manifestCluster;
    protected float serverTime;

    public TwitchInfoTag(String propertyString) throws InvalidTagParameters {
        super(propertyString);
    }

    @Override
    protected void resolveParameter(String name, String string) throws InvalidTagParameters {
        switch (name) {
            case "NODE":
                node = string;
                return;
            case "MANIFEST-NODE":
                manifestNode = string;
                return;
            case "USER-IP":
                userIP = string;
                return;
            case "SERVER-TIME":
                serverTime = Float.valueOf(string);
                return;
            case "CLUSTER":
                cluster = string;
                return;
            case "MANIFEST-CLUSTER":
                manifestCluster = string;
                return;
        }

        throw new InvalidTagParameters("Invalid parameter name for a string parameter: \"" + name + "\"");
    }

    @Override
    public String getName() {
        return "EXT-X-TWITCH-INFO";
    }

    @Override
    public TagType getType() {
        return TagType.MASTER_PLAYLIST;
    }

    @Override
    public int minVersion() {
        return 0;
    }

    @Override
    public boolean isOneTime() {
        return false;
    }

    @Override
    public boolean shouldBeFollowedByURI() {
        return false;
    }

    @Override
    public boolean shouldBeUnique() {
        return true;
    }

    public String getNode() {
        return node;
    }

    public String getManifestNode() {
        return manifestNode;
    }

    public String getUserIP() {
        return userIP;
    }

    public String getCluster() {
        return cluster;
    }

    public String getManifestCluster() {
        return manifestCluster;
    }

    public float getServerTime() {
        return serverTime;
    }
}
