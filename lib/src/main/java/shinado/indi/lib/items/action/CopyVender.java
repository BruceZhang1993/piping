package shinado.indi.lib.items.action;

import android.widget.Toast;

import shinado.indi.lib.items.VenderItem;

/**
 * Created by Administrator on 2015/12/21.
 */
public class CopyVender extends ActionVender{

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
        VenderItem successor = result.getSuccessor();
        if (successor != null){
            Toast.makeText(context, successor.getDisplayName(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "successor is null", Toast.LENGTH_LONG).show();
        }
        return 0;
    }

}
