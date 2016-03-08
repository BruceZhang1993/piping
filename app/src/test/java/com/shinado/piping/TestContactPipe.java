package com.shinado.piping;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

@RunWith(MockitoJUnitRunner.class)
public class TestContactPipe extends SearchablePipe {

    @Mock
    private Context context;

    public TestContactPipe() {
        super(13);
    }

    @Before
    public void setup() {
        load(new EnglishTranslator(context), null, 2);
    }

    @Override
    public void load(AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        add("Kakao Talk con", translator, 1);
        add("KakaoTalk con", translator, 2);
        add("ok google con", translator, 3);
        add("oka google con", translator, 4);
        add("what kat con", translator, 5);
        add("XBrowser con", translator, 6);
        add("candy con", translator, 7);

        new Thread() {
            public void run() {
//                try {
//                    sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (listener != null) {
                    listener.onItemsLoaded(TestContactPipe.this.getId(), total);
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
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        callback.onOutput(result.getDisplayName() + " accepting input:" + input);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("getting output from:" + result.getDisplayName());
    }

    @Override
    protected void execute(Pipe rs) {

    }

    @Test
    public void doNothing(){

    }
}
