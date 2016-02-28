package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.Value;

public abstract class BasePipe {

    protected int id;
    protected Context context;

    protected Console console;

    public BasePipe(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void startExecution(Pipe item) {
        execute(item, null, false);
    }

    /**
     * execute an instruction
     * if rs has previous items, accept input from the previous ones.
     *
     * @param hasNext if true, execute this item and get output for the next one.
     */
    private void execute(final Pipe rs, OutputCallback callback, boolean hasNext) {
        Value value = rs.getValue();
        if (!value.isPreEmpty()) {
            TreeSet<Pipe> prevs = rs.getPrevious();
            if (prevs != null && !prevs.isEmpty()) {
                Pipe prev = prevs.first();
                prev.getBasePipe().execute(prev, new OutputCallback() {
                    @Override
                    public void onOutput(String input) {
                        acceptInput(rs, input);
                    }
                }, true);
                return;
            }
        }
        if (!hasNext) {
            execute(rs);
        } else {
            getOutput(rs, callback);
        }
    }

    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }

    public void setContext(Context context){
        this.context = context;
    }

    /**
     * @param prev once users input ".", the search results prior the input will be put into this set
     * @param input user input
     * @param length the length of the input change, e.g.
     *               "" -> "a" : 1
     *               "a" -> "aoa" : 2
     *               "aoa" -> "ao" : -1
     * @param callback to receive results
     */
    public abstract void search(TreeSet<Pipe> prev, String input, int length, SearchResultCallback callback);

    /**
     * accept input from the successors of result
     */
    public abstract void acceptInput(Pipe result, String input);

    /**
     * get output for the next Pipe
     */
    public abstract void getOutput(Pipe result, OutputCallback callback);

    protected abstract void execute(Pipe rs);

    public static interface OutputCallback {
        public void onOutput(String output);
    }

    public interface SearchResultCallback{
        public void onSearchResult(TreeSet<Pipe> results);
    }
}
