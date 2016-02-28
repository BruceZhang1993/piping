package shinado.indi.lib.items.action;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.util.FunctionUtil;

public class CopyVender extends PreActionVender{

    VenderItem mResult;

    public CopyVender(int id){
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName(".cp");
        mResult.setName(new String[]{".", "cp"});
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
        FunctionUtil.copyToClipboard(context, input);
        input("copied to clipboard");
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {
        callback.onOutput("error, cannot get output from cp");
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    public void execute(VenderItem result) {
        input(".cp must take an application or contact");
    }

}
