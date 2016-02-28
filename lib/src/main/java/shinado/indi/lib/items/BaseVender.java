package shinado.indi.lib.items;

import android.content.Context;

import java.util.TreeSet;

import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.launcher.Searchable;
import shinado.indi.lib.items.VenderItem.Value;

/**
 * the basic
 */
public abstract class BaseVender {

    protected int id;
    protected Context context;
    protected TreeSet<VenderItem> frequentItems = new TreeSet<>();
    protected SearchHelper mSearchHelper;
    protected FrequentMap mFrequentMap;

    public BaseVender(int id) {
        this.id = id;
    }

    public void init(Context context, SearchHelper s) {
        this.context = context;
        mFrequentMap = new FrequentMap();
        this.mSearchHelper = s;
    }

    public void removeFrequency(VenderItem vo) {
        mFrequentMap.remove(vo.getValue().body);
        frequentItems.remove(vo);
    }

    public void addFrequency(VenderItem item) {
        item.addFrequency();
        boolean bExist = mFrequentMap.addFrequency(item.getValue().body);
        if (!bExist) {
            frequentItems.add(item);
        }
    }

    /**
     * fire when input text is changed
     * used by a TextWatcher
     *
     * @param prev   the previous result before entering ".", for action vender only
     * @param key    the text input
     * @param length count-before
     *               which means when
     *               length > 0 input
     *               length < 0 delete
     */
    public abstract TreeSet<VenderItem> search(TreeSet<VenderItem> prev, String key, int length);

    public abstract VenderItem getItem(String value);

    /**
     * fulfill result with frequency
     */
    protected void fulfillResult(VenderItem vo) {
        Integer freq = mFrequentMap.get(vo.getValue().body);
        if (freq != null) {
            vo.setFreq(freq);
            frequentItems.add(vo);
        }
    }

    /**
     * "tran.ins -ls" -> ["tran", "ins", ["ls"]]
     * "maya.txt.play" -> ["maya.txt", "play", null]
     *
     * @param key
     * @return
     */
    protected Value getValue(String key) {
        Value command = new Value();
        int indexOfDot = key.lastIndexOf(".");
        if (indexOfDot < 0) {
            command.body = key;
        } else {
            if (indexOfDot == 0) {
                command.pre = null;
            } else {
                String left = key.substring(0, indexOfDot);
                command.pre = left;
            }
            String right = key.substring(indexOfDot + 1, key.length());
            String[] splitRight = right.split("-");
            int splitLength = splitRight.length;
            if (splitLength > 0) {
                command.body = splitRight[0].trim();
                if (splitLength > 1) {
                    String[] params = new String[splitLength - 1];
                    for (int i = 1; i < splitRight.length; i++) {
                        params[i - 1] = splitRight[i].trim();
                    }
                    command.params = params;
                } else {
                    command.params = null;
                }
            } else {
                command.body = null;
                command.params = null;
            }
        }
        return command;
    }

    /**
     * accept input from the successors of result
     */
    public abstract void acceptInput(VenderItem result, String input);

    /**
     * get output for the next VenderItem
     */
    public abstract void getOutput(VenderItem result, OutputCallback callback);

    public void startExecution(VenderItem item) {
        execute(item, null, false);
    }

    /**
     * execute an instruction
     * if rs has previous items, accept input from the previous ones.
     *
     * @param hasNext if true, execute this item and get output for the next one.
     */
    private void execute(final VenderItem rs, OutputCallback callback, boolean hasNext) {
        Value value = rs.getValue();
        //pre being empty is the only
        if (!value.isPreEmpty()) {
            TreeSet<VenderItem> prevs = rs.getSuccessors();
            if (prevs != null && !prevs.isEmpty()) {
                VenderItem prev = prevs.first();
                BaseVender preVender = mSearchHelper.getVender(prev.getId());
                preVender.execute(prev, new OutputCallback() {
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

    protected abstract void execute(VenderItem rs);

    public static interface OutputCallback {
        public void onOutput(String output);
    }


    /**
     * display something in the view
     *
     * @param msg the message to display
     */
    protected void display(String msg, int flag) {
        mSearchHelper.getSearchable().onDisplay(msg + "\n", flag);
    }

    protected void input(String msg) {
        display(msg + "\n", Searchable.FLAG_INPUT);
    }

    protected void replace(String msg) {
        display(msg + "\n", Searchable.FLAG_REPLACE);
    }

    protected void blockInput() {
        mSearchHelper.blockInput();
    }

    protected void releaseInput() {
        mSearchHelper.releaseInput();
    }

    public int getId() {
        return id;
    }
}
