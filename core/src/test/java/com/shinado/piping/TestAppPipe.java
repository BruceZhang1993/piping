package com.shinado.piping;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class TestAppPipe extends SearchablePipe{

    public TestAppPipe(int id) {
        super(id);
    }

    @Override
    public void load(AbsTranslator translator, final OnItemsLoadedListener listener) {
        add("Kakao Talk", translator);
        add("KakaoTalk", translator);
        add("ok google", translator);
        add("oka google", translator);
        add("what kat", translator);
        add("XBrowser", translator);

        new Thread(){
            public void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listener.onItemsLoaded(TestAppPipe.this.getId());
            }
        }.start();
    }

    private void add(String displayName, AbsTranslator translator){
        SearchableName name = translator.getName(displayName);
        putItemInMap(new Pipe(100, displayName, name));
    }

    @Override
    public void acceptInput(Pipe result, String input) {

    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {

    }

    @Override
    protected void execute(Pipe rs) {

    }
}
