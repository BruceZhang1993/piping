package com.shinado.piping;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import indi.shinado.piping.pipes.SearchableName;

public class TestSearchableName {

    private SearchableName searchableName;

    @Before
    public void setup(){
        searchableName = new SearchableName("");
    }

    @Test
    public void testGetName(){
        String[] names = searchableName.getNames("Kakao Talk");
        String[] expected = new String[]{"ka", "kao", "ta", "l", "k"};
        assertEquals(expected, names);

        names = searchableName.getNames("iKakao");
        expected = new String[]{"i", "ka", "kao"};
        assertEquals(expected, names);

        names = searchableName.getNames("");
        expected = new String[]{""};
        assertEquals(expected, names);

        names = searchableName.getNames("i4kao");
        expected = new String[]{"i", "4", "kao"};
        assertEquals(expected, names);

        names = searchableName.getNames("KakaoTalk");
        expected = new String[]{"ka", "kao", "ta", "l", "k"};
        assertEquals(expected, names);
    }

    private void assertEquals(String[] expected, String[] result){
        Assert.assertEquals(expected.length, result.length);
        for (int i=0; i<result.length; i++){
            Assert.assertEquals(expected[i], result[i]);
        }
    }

    @Test
    public void testContains(){
        SearchableName searchableName = new SearchableName("iFacebook 4what?");

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
