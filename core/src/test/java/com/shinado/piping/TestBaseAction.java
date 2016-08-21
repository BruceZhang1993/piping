package com.shinado.piping;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class TestBaseAction {

    private BasePipe basePipe = new BasePipe(1) {

        @Override
        public void search(String input, int length, SearchResultCallback callback) {

        }

        @Override
        public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

        }

        @Override
        public void getOutput(Pipe result, OutputCallback callback) {

        }

        @Override
        protected void execute(Pipe rs) {

        }

        @Override
        public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {

        }
    };

    @Test
    public void testGetKeyIndex(){
        String key = "lany";
        assertKeyIndex(1, new String[]{"lan", "ya"}, key);
        assertKeyIndex(2, new String[]{"lang", "yi"}, key);

        key = "mes";
        assertKeyIndex(1, new String[]{"me", "s", "sa", "ge"}, key);
        assertKeyIndex(3, new String[]{"t", "he", "me", "s"}, key);

        key = "me";
        assertKeyIndex(0, new String[]{"me", "s", "sa", "ge"}, key);

        key = "m";
        assertKeyIndex(0, new String[]{"m", "s", "sa", "ge"}, key);
    }

    private void assertKeyIndex(int keyIndex, String[] name, String key){
        Assert.assertEquals(keyIndex,
                basePipe.getKeyIndex(
                        new Pipe(2, "", new SearchableName(name)
                        ), key)
        );
    }
}
