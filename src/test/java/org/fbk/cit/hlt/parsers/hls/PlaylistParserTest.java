package org.fbk.cit.hlt.parsers.hls;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;

public class PlaylistParserTest {
    public PlaylistParser defaultParser;
    
    private String getStringResource(String resource) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(resource), "UTF-8");
    }

    @Before
    public void setUp() throws Exception {
        defaultParser = new PlaylistParser();
    }

    @Test
    public void testSplitToTagStrings() throws Exception {
        String masterPl = getStringResource("/master_playlist1.m3u");
        String cleanMasterPl = getStringResource("/clean_master_playlist1.m3u");
        ArrayList<String> cleanLines = defaultParser.splitToTagStrings(cleanMasterPl);
        ArrayList<String> lines = defaultParser.splitToTagStrings(masterPl);

        Assert.assertArrayEquals("Arrays should be equal", cleanLines.toArray(), lines.toArray());
    }
    
    @Test
    public void testMasterPlaylistParsing() throws Exception {
        String cleanMasterPl = getStringResource("/clean_master_playlist1.m3u");
        MasterPlaylist master = defaultParser.parseTwitchMaster(cleanMasterPl);
        Assert.assertEquals(master.getElements().size(), 6);
    }
}