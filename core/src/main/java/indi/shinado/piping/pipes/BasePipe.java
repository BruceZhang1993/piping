package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.TreeSet;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public abstract class BasePipe {

    protected int id;

    protected Context context;

    protected Console console;

    protected IPipeManager pipeManager;

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
        Instruction instruction = rs.getInstruction();
        if (!instruction.isPreEmpty()) {
            Pipe.PreviousPipes previous = rs.getPrevious();
            if (!previous.isEmpty()) {
                Pipe prev = previous.get();
                //create a new copy of previous
                //since previous will be set null
                //better not to get previous from rs
                final Pipe.PreviousPipes newPrevious = new Pipe.PreviousPipes(previous);
                if (rs.ignoreInput()){
                    acceptInput(rs, "", newPrevious);
                }else{
                    prev.getBasePipe().execute(prev, new OutputCallback() {
                        @Override
                        public void onOutput(String input) {
                            acceptInput(rs, input, newPrevious);
                        }
                    }, true);
                }
                return;
            }
        }
        if (!hasNext) {
            execute(rs);
        } else {
            getOutput(rs, callback);
        }
    }

    protected void fulfill(Pipe item, String input){
        int keyIndex = getKeyIndex(item, input);
        item.setKeyIndex(keyIndex);
        item.setInstruction(new Instruction(input));
        //only set previous for the first item
        //pass it on to next when shifting
//            item.setPrevious(prev);
    }

    /**
     * return the key index to be searched
     * e.g. input "k"
     * ["kakao", "talk"] -> 0
     * ["we", "kite"] -> 1
     * ["we", "chat"] -> 2
     * so that "kakao talk" will come first
     */
    protected int getKeyIndex(Pipe item, String key) {
        int i = 0;
        //set key index
        for (String str : item.getSearchableName().getNames()) {
            if (str.startsWith(key)) {
                break;
            }
            i++;
        }

        return i;
    }
    public IPipeManager getPipeManager() {
        return pipeManager;
    }

    public void setPipeManager(IPipeManager pipeManager) {
        this.pipeManager = pipeManager;
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
     * @param input user input
     * @param length the length of the input change, e.g.
     *               "" -> "a" : 1
     *               "a" -> "aoa" : 2
     *               "aoa" -> "ao" : -1
     * @param callback to receive results
     */
    public abstract void search(String input, int length, SearchResultCallback callback);

    /**
     * accept input from the successors of result
     */
    public abstract void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous);

    /**
     * get output for the next Pipe
     */
    public abstract void getOutput(Pipe result, OutputCallback callback);

    protected abstract void execute(Pipe rs);

    public abstract void load(AbsTranslator translator, OnItemsLoadedListener listener, int total);

    public interface OnItemsLoadedListener {
        void onItemsLoaded(int id, int total);
    }

    public interface OutputCallback {
        void onOutput(String output);
    }

    public interface SearchResultCallback{
        void onSearchResult(TreeSet<Pipe> results, String input);
    }
}
