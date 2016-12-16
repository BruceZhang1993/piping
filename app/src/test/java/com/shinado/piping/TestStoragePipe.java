package com.shinado.piping;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchableActionPipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;


public class TestStoragePipe extends SearchableActionPipe{

    private Pipe addPipe, pushPipe;

    public TestStoragePipe(int id) {
        super(id);

        addPipe = new Pipe(id, "$add", new SearchableName("add"), "$#add");
        pushPipe = new Pipe(id, "$push", new SearchableName("push"), "$#push");
    }

    @Override
    public String getKeyword() {
        return "db";
    }

    @Override
    public void start() {
        super.start();

        putItemInMap(new Pipe(getId(), "download"));
        putItemInMap(new Pipe(getId(), "files"));
        putItemInMap(addPipe);
        putItemInMap(pushPipe);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        System.out.println(input + " -> " + result.getExecutable());
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {

    }

    @Override
    protected void execute(Pipe rs) {
        System.out.println(rs.getExecutable());
    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {
    }

}
