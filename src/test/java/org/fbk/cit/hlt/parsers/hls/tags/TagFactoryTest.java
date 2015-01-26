package org.fbk.cit.hlt.parsers.hls.tags;

import org.fbk.cit.hlt.parsers.hls.MediaType;
import org.fbk.cit.hlt.parsers.hls.tags.general.OpeningTag;
import org.fbk.cit.hlt.parsers.hls.tags.master.MediaTag;
import org.junit.Test;

import javax.print.attribute.standard.Media;
import java.util.HashMap;

import static org.junit.Assert.*;

public class TagFactoryTest {

    @Test
    public void testCheckAmountOfTags() throws Exception {
        HashMap<String, Class<?>> tagClasses =  TagFactory.getTags();
        assertTrue("The amount of found tags should be greater than zero", tagClasses.size() > 0);
    }
    
    @Test
    public void testOpeningTagInstantiation() throws Exception {
        Tag tag = new TagFactory().getTag("#EXTM3U");
        assertTrue(tag instanceof OpeningTag);
    }
    
    @Test
    public void testMediaTagInstantiation() throws Exception {
        Tag tag = new TagFactory().getTag("#EXT-X-MEDIA:TYPE=VIDEO,GROUP-ID=\"medium\",NAME=\"Medium\",AUTOSELECT=YES,DEFAULT=YES");
        assertTrue(tag instanceof MediaTag);
        
        MediaTag mediaTag = (MediaTag) tag;
        assertEquals("medium", mediaTag.getGroupId());
        assertEquals(MediaType.VIDEO, mediaTag.getMediaType());
        assertEquals("Medium", mediaTag.getMediaName());
        assertTrue(mediaTag.isAutoselect());
        assertTrue(mediaTag.isDefault());
        assertTrue(!mediaTag.isForced());
    }
}