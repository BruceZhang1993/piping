package com.shinado.piping;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchableActionPipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class TestSearchableActionPipe extends SearchableActionPipe{

    public TestSearchableActionPipe(int id) {
        super(id);
        putItemInMap(new Pipe(getId(), "downloads"));
        putItemInMap(new Pipe(getId(), "workspace"));
        putItemInMap(new Pipe(getId(), "documents"));
        putItemInMap(new Pipe(getId(), "desktop"));
    }

    @Override
    public String getKeyword() {
        return "key";
    }

    @Override
    public void destroy() {

    }

    @Override
    public Pipe getByValue(String value) {
        return null;
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {

    }

    @Override
    protected void execute(Pipe rs) {
        System.out.println("exec: " + rs.getExecutable());
    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {

    }
}
