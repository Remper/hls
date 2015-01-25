package org.fbk.cit.hlt.parsers.hls.tags;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class TagFactoryTest {

    @Test
    public void testCheckAmountOfTags() throws Exception {
        HashMap<String, Class<?>> tagClasses =  TagFactory.getTags();
        assertTrue("The amount of found tags should be greater than zero", tagClasses.size() > 0);
    }
}