package org.fbk.cit.hlt.parsers.hls.extensions.twitchtv;

import org.fbk.cit.hlt.parsers.hls.tags.extensions.twitchtv.TwitchInfoTag;

/**
 * Helper class containing info from #EXT-X-TWITCH-INFO to decouple from Tag abstraction
 */
public class TwitchInfo {
    protected String node;
    protected String manifestNode;
    protected String userIP;
    protected String cluster;
    protected String manifestCluster;
    protected float serverTime;

    public TwitchInfo(String node, String manifestNode, String userIP, String cluster, String manifestCluster, float serverTime) {
        this.node = node;
        this.manifestNode = manifestNode;
        this.userIP = userIP;
        this.cluster = cluster;
        this.manifestCluster = manifestCluster;
        this.serverTime = serverTime;
    }

    public static TwitchInfo fromTag(TwitchInfoTag tag) {
        return new TwitchInfo(tag.getNode(), tag.getManifestNode(), tag.getUserIP(), tag.getCluster(), tag.getManifestCluster(), tag.getServerTime());
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
