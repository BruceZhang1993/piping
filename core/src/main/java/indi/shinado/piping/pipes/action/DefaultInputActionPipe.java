package indi.shinado.piping.pipes.action;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public abstract class DefaultInputActionPipe extends ActionPipe {

    protected Pipe mResult;

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

    /**
     * @return the name to be displayed in the screen
     * better to start with $, I mean, it looks cooler
     */
    public abstract String getDisplayName();

    /**
     * @return a name for searching
     */
    public abstract SearchableName getSearchable();

    /**
     * when user input is empty, which contains no special characters, as "." or "-"
     * e.g.
     * ""
     * "ab"
     */
    public abstract void onEmpty(Pipe rs, IInput input);

    /**
     * when params from user input is empty, while pre is not
     * e.g.
     * "a.b"
     */
    public abstract void onParamsEmpty(Pipe rs, IInput input);

    /**
     * when pre from user input is empty, while params is not
     * e.g.
     * "a -b"
     */
    public abstract void onPreEmpty(Pipe rs, IInput input);

    /**
     * when neither pre nor params from user input is empty
     * e.g.
     * "a.b -c"
     */
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
        void input(String string);
    }

}

