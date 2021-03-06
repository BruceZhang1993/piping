package com.shinado.piping;

import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class InstallPipe extends ActionPipe{

    Pipe mResult;

    public InstallPipe(){
        super(11);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$install");
        mResult.setSearchableName(new SearchableName(new String[]{"ins", "tall"}));
        mResult.setBasePipe(this);
    }

    @Override
    protected Pipe getResult() {
        return mResult;
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

}
