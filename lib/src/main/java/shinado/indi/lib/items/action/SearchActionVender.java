package shinado.indi.lib.items.action;

import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;

/**
 * Created by Administrator on 2015/12/28.
 */
public class SearchActionVender extends PreActionVender {

    VenderItem mResult;

    public SearchActionVender(int id) {
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName("$ls");
        mResult.setName(new String[]{".", "ls"});
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
        TreeSet<VenderItem> successors = result.getSuccessors();
        StringBuilder sb = new StringBuilder();
        for (VenderItem item : successors) {
            sb.append(item.getDisplayName());
            sb.append("\n");
        }
        input(sb.toString());
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {
        callback.onOutput("error, ls does not have output");
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    public void execute(VenderItem result) {

    }
}
