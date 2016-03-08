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
import indi.shinado.piping.pipes.entity.Keys;
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
        add("$install", new String[]{"ins", "tall"}, 8);
        add("uninstall", new String[]{"un", "ins", "tall"}, 9);

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

    private void add(String displayName, String[] name, int id) {
        Pipe pipe = new Pipe(id, displayName, new SearchableName(name), displayName + ".exe");
        pipe.setBasePipe(this);
        putItemInMap(pipe);
    }

    private void add(String displayName, AbsTranslator translator, int id) {
        SearchableName name = translator.getName(displayName);
        Pipe pipe = new Pipe(id, displayName, name, displayName + ".exe");
        pipe.setBasePipe(this);
        putItemInMap(pipe);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
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
        TreeSet<Pipe> results = search("");
        assertEquals(new int[]{}, results);

        results = search("kakao");
        assertEquals(new int[]{2, 1}, results);

        results = search("k");
        assertEquals(new int[]{2, 1, 4, 3, 5}, results);

        results = search("ka");
        assertEquals(new int[]{2, 1, 4, 5}, results);

        results = search("kak");
        assertEquals(new int[]{2, 1}, results);

        results = search("kaki");
        assertEquals(new int[]{}, results);

        results = search("kak");
        assertEquals(new int[]{2, 1}, results);

        results = search("ka");
        assertEquals(new int[]{2, 1, 4, 5}, results);

        results = search("k");
        assertEquals(new int[]{2, 1, 4, 3, 5}, results);

        results = search("k" + Keys.PIPE);
        assertEquals(new int[]{}, results);

        results = search("kak");
        assertEquals(new int[]{2, 1}, results);

//        results = search("k" + Keys.PIPE + "k");
//        assertEquals(new int[]{2, 1, 4, 3, 5}, results);

    }

    @Test
    public void testSearch2() {
        TreeSet<Pipe> results = search("");
        assertEquals(new int[]{}, results);

        results = search("i");
        assertEquals(new int[]{8, 9}, results);

        results = search("in");
        assertEquals(new int[]{8, 9}, results);

    }

    private void assertEquals(int[] expected, TreeSet<Pipe> actual) {
        Assert.assertEquals(expected.length, actual.size());
        int i = 0;
        for (Pipe item : actual) {
            Assert.assertEquals(expected[i++], item.getId());
        }
    }
}
