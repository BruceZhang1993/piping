package com.shinado.piping;

import org.junit.Assert;
import org.junit.Test;

import indi.shinado.piping.pipes.entity.SearchableName;

public class TestSearchableName {

    @Test
    public void testContains(){
        SearchableName searchableName = new SearchableName(new String[]{"i", "fa", "ce", "boo", "k", "4", "w", "h", "at", "?"});

        Assert.assertEquals(false, searchableName.contains(""));
        Assert.assertEquals(true, searchableName.contains("i"));
        Assert.assertEquals(true, searchableName.contains("if"));
        Assert.assertEquals(false, searchableName.contains("fb"));
        Assert.assertEquals(false, searchableName.contains("fe"));
        Assert.assertEquals(false, searchableName.contains("fo"));
        Assert.assertEquals(true, searchableName.contains("facebook"));
        Assert.assertEquals(false, searchableName.contains("facebookw"));
        Assert.assertEquals(false, searchableName.contains("facebook w"));
    }
}
