package indi.shinado.piping.pipes.impl.action;

import android.content.Context;

import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class ClearPipe extends ActionPipe {

    private Pipe mResult;

    public ClearPipe(int id) {
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$clear");
        mResult.setSearchableName(new SearchableName(new String[]{"clear"}));
        mResult.setBasePipe(this);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        callback.onOutput(input);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("");
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void execute(Pipe result) {
        getConsole().clear();
    }

}
