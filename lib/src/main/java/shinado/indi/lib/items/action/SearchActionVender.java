package shinado.indi.lib.items.action;

import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.Searchable;

/**
 * Created by Administrator on 2015/12/28.
 */
public class SearchActionVender extends PreActionVender{

    VenderItem mResult;

    public SearchActionVender(){
        mResult = new VenderItem();
        mResult.setId(VenderItem.BUILD_IN_ID_SEARCH);
        mResult.setDisplayName(".ls");
        mResult.setValue("");
        mResult.setName(new String[]{".", "ls"});
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    public int function(VenderItem result) {
        TreeSet<VenderItem> successors = result.getSuccessors();
        StringBuilder sb = new StringBuilder();
        for (VenderItem item : successors){
            sb.append(item.getDisplayName());
            sb.append("\n");
        }
        display(sb.toString(), Searchable.FLAG_INPUT);
        return 0;
    }
}
