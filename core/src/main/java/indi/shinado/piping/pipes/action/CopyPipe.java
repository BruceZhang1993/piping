package indi.shinado.piping.pipes.action;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.util.android.FunctionUtil;

public class CopyPipe extends ActionPipe{

    Pipe mResult;

    public CopyPipe(int id){
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$copy");
        mResult.setSearchableName(new SearchableName(new String[]{"co", "py"}));
        mResult.setBasePipe(this);
    }

    @Override
    public void acceptInput(Pipe result, String input) {
        FunctionUtil.copyToClipboard(context, input);
        getConsole().input("copied to clipboard");
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("error, cannot get output from cp");
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void execute(Pipe result) {
        getConsole().input(".cp must take an application or contact");
    }

}
