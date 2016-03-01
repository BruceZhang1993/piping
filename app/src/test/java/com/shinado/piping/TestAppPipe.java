package com.shinado.piping;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

@RunWith(MockitoJUnitRunner.class)
public class TestAppPipe extends SearchablePipe {

    @Mock
    private Context context;

    public TestAppPipe() {
        super(10);
    }

    @Before
    public void setup() {
        load(new EnglishTranslator(context), null, 2);
    }

    @Override
    public void load(AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        add("Kakao Talk", translator, 1);
        add("KakaoTalk", translator, 2);
        add("ok google", translator, 3);
        add("oka google", translator, 4);
        add("what kat", translator, 5);
        add("XBrowser", translator, 6);
        add("candy", translator, 7);

        new Thread() {
            public void run() {
//                try {
//                    sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (listener != null) {
                    listener.onItemsLoaded(TestAppPipe.this.getId(), total);
                }

            }
        }.start();
    }

    private void add(String displayName, AbsTranslator translator, int id) {
        SearchableName name = translator.getName(displayName);
        Pipe pipe = new Pipe(id, displayName, name, displayName + ".exe");
        pipe.setBasePipe(this);
        putItemInMap(pipe);
    }

    @Override
    public void acceptInput(Pipe result, String input) {
        getConsole().input(result.getDisplayName() + " accepting input:" + input);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("getting output from:" + result.getDisplayName());
    }

    @Override
    protected void execute(Pipe rs) {

    }

    @Test
    public void testSearch() {
        TreeSet<Pipe> results = search(new Instruction(""));
        assertEquals(new int[]{}, results);

        results = search(new Instruction("k"));
        push(results);
        assertEquals(new int[]{2, 1, 4, 3, 5}, results);

        results = search(new Instruction("ka"));
        push(results);
        assertEquals(new int[]{2, 1, 4, 5}, results);

        results = search(new Instruction("kak"));
        push(results);
        assertEquals(new int[]{2, 1}, results);

        results = search(new Instruction("kaki"));
        push(results);
        assertEquals(new int[]{}, results);

        //"kak"
        pop();
        assertEquals(new int[]{2, 1}, peek());

        //"ka"
        pop();
        assertEquals(new int[]{2, 1, 4, 5}, peek());

        //"k"
        pop();
        assertEquals(new int[]{2, 1, 4, 3, 5}, peek());

        results = search(new Instruction("k."));
        push(results);
        assertEquals(new int[]{}, results);

        results = search(new Instruction("k.k"));
        push(results);
        assertEquals(new int[]{2, 1, 4, 3, 5}, results);

    }

    private void assertEquals(int[] expected, TreeSet<Pipe> actual) {
        Assert.assertEquals(expected.length, actual.size());
        int i = 0;
        for (Pipe item : actual) {
            Assert.assertEquals(expected[i++], item.getId());
        }
    }
}
