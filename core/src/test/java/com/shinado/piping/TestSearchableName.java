package com.shinado.piping;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

public class TestSearchableName {

    @Test
    public void testContains(){
        SearchableName searchableName = new SearchableName(new String[]{"i", "fa", "ce", "boo", "k", "4", "w", "h", "at", "?"});

        Assert.assertEquals(false, searchableName.contains(""));
        Assert.assertEquals(true, searchableName.contains("i"));
        Assert.assertEquals(true, searchableName.contains("if"));
        Assert.assertEquals(true, searchableName.contains("fb"));
        Assert.assertEquals(false, searchableName.contains("fe"));
        Assert.assertEquals(false, searchableName.contains("fo"));
        Assert.assertEquals(true, searchableName.contains("facebook"));
        Assert.assertEquals(true, searchableName.contains("facebookw"));
        Assert.assertEquals(true, searchableName.contains("facebook w"));
    }
}
