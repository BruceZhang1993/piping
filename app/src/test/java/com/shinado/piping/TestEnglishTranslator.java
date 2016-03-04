package com.shinado.piping;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

public class TestEnglishTranslator {

    private AbsTranslator translator;

    @Mock
    private Context context;

    @Before
    public void setup(){
        translator = new EnglishTranslator(context);
    }

    @Test
    public void testGetName(){
        SearchableName names = translator.getName("Kakao Talk");
        String[] expected = new String[]{"ka", "kao", "ta", "l", "k"};
        assertEquals(expected, names);

        names = translator.getName("iKakao");
        expected = new String[]{"i", "ka", "kao"};
        assertEquals(expected, names);

        names = translator.getName("");
        expected = new String[]{""};
        assertEquals(expected, names);

        names = translator.getName("i4kao");
        expected = new String[]{"i", "4", "kao"};
        assertEquals(expected, names);

        names = translator.getName("KakaoTalk");
        expected = new String[]{"ka", "kao", "ta", "l", "k"};
        assertEquals(expected, names);
    }

    private void assertEquals(String[] expected, SearchableName name){
        String[] result = name.getNames();
        Assert.assertEquals(expected.length, result.length);
        for (int i=0; i<result.length; i++){
            Assert.assertEquals(expected[i], result[i]);
        }
    }


}
