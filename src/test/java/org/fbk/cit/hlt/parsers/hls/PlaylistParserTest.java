package org.fbk.cit.hlt.parsers.hls;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;

public class PlaylistParserTest {
    public PlaylistParser defaultParser;

    @Before
    public void setUp() throws Exception {
        defaultParser = new PlaylistParser();
    }

    @Test
    public void testSplitToTagStrings() throws Exception {
        String masterPl = IOUtils.toString(this.getClass().getResourceAsStream("/master_playlist1.m3u"), "UTF-8");
        String cleanMasterPl = IOUtils.toString(this.getClass().getResourceAsStream("/clean_master_playlist1.m3u"), "UTF-8");
        ArrayList<String> cleanLines = defaultParser.splitToTagStrings(cleanMasterPl);
        ArrayList<String> lines = defaultParser.splitToTagStrings(masterPl);

        Assert.assertArrayEquals("Arrays should be equal", cleanLines.toArray(), lines.toArray());
    }
}