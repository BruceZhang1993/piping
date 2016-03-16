package indi.shinado.piping.pipes.action;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import csu.org.dependency.volley.DefaultApplication;
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
        execute(result, callback);
    }

    @Override
    protected void execute(Pipe rs) {
        execute(rs, getConsoleCallback());
    }

    @Override
    public void intercept() {
        DefaultApplication.getInstance().getRequestQueue().cancelAll(DefaultApplication.TAG);
    }

    private void execute(Pipe rs, OutputCallback callback){
        Instruction value = rs.getInstruction();
        if (value.isParamsEmpty()){
            onParamsEmpty(rs, callback);
        }else {
            onParamsNotEmpty(rs, callback);
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
     * when pre and params are both empty
     * e.g.
     * "b"
     */
    public abstract void onParamsEmpty(Pipe rs, OutputCallback callback);

    /**
     * when pre from user input is empty, while params is not
     * e.g.
     * "b -c"
     */
    public abstract void onParamsNotEmpty(Pipe rs, OutputCallback callback);

}

