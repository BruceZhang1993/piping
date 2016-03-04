package indi.shinado.piping.pipes.action;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.HashMap;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.download.DownloadImpl;
import indi.shinado.piping.download.Downloadable;
import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.entity.SearchableName;

public abstract class DefaultInputActionPipe extends ActionPipe {

    private Pipe mResult;

    public DefaultInputActionPipe(int id) {
        super(id);
        mResult = new Pipe(id);
        mResult.setBasePipe(this);
        mResult.setDisplayName(getDisplayName());
        mResult.setSearchableName(getSearchable());
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        execute(result, new CallbackInput(callback));
    }

    @Override
    protected void execute(Pipe rs) {
        execute(rs, mConsoleInput);
    }

    private void execute(Pipe rs, IInput input){
        Instruction value = rs.getInstruction();
        if (value.isEmpty()) {
            onEmpty(rs, input);
        } else {
            if (value.isParamsEmpty()) {
                onParamsEmpty(rs, input);
            } else {
                if (value.isPreEmpty()) {
                    onPreEmpty(rs, input);
                } else {
                    onNoEmpty(rs, input);
                }
            }
        }
    }

    public abstract String getDisplayName();
    public abstract SearchableName getSearchable();
    public abstract void onEmpty(Pipe rs, IInput input);
    public abstract void onParamsEmpty(Pipe rs, IInput input);
    public abstract void onPreEmpty(Pipe rs, IInput input);
    public abstract void onNoEmpty(Pipe rs, IInput input);

    protected IInput mConsoleInput = new IInput() {
        @Override
        public void input(String string) {
            getConsole().input(string);
            getConsole().releaseInput();
        }
    };

    private class CallbackInput implements IInput{

        public OutputCallback callback;

        public CallbackInput(OutputCallback callback){
            this.callback = callback;
        }

        @Override
        public void input(String string) {
            callback.onOutput(string);
            getConsole().releaseInput();
        }
    }

    public interface IInput{
        public void input(String string);
    }

}

