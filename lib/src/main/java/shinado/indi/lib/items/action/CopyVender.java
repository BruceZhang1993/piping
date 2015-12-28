package shinado.indi.lib.items.action;

import android.widget.Toast;

import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.Searchable;
import shinado.indi.lib.util.FunctionUtil;

/**
 * Created by Administrator on 2015/12/21.
 */
public class CopyVender extends PreActionVender{

    VenderItem mResult;

    public CopyVender(){
        mResult = new VenderItem();
        mResult.setId(VenderItem.BUILD_IN_ID_COPY);
        mResult.setDisplayName(".cp");
        mResult.setValue("");
        mResult.setName(new String[]{".", "cp"});
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    public int function(VenderItem result) {
        TreeSet<VenderItem> successors = result.getSuccessors();
        if (successors != null && successors.size() > 0){
            VenderItem item = (VenderItem)successors.toArray()[0];
            String msg = item.getDisplayName() + ": " + item.getValue();
            FunctionUtil.copyToClipboard(context, msg);
            display("copied to clipboard", Searchable.FLAG_INPUT);
        }else {
            display("nothing was copied", Searchable.FLAG_INPUT);
        }
        return 0;
    }

}
