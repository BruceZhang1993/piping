package com.shinado.search;

import java.util.TreeSet;

import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class SearchItemPipe extends ActionPipe {

    private Pipe mResult;

    public SearchItemPipe(int id){
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$ls");
        mResult.setSearchableName(new SearchableName(new String[]{"ls"}));
        mResult.setBasePipe(this);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes prev) {
        TreeSet<Pipe> previous = prev.getAll();
        StringBuilder sb = new StringBuilder();
        for (Pipe item : previous) {
            sb.append(item.getDisplayName());
            sb.append("\n");
        }
        getConsole().input(sb.toString());
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("error, cannot get output from $ls");
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void execute(Pipe result) {
        getConsole().input("$ls must take an application or contact");
    }

}
