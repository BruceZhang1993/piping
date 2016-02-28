package shinado.indi.lib.items.action;


import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.util.AppManager;

public class MightyVender extends PreActionVender {

    VenderItem mResult;
    private final String OPT_U = "u";
    private final String OPT_I = "i";
    private final String HELP = "Usage of mt:" +
            "[key].mt [option]" +
            "where option includes:" +
            VenderItem.INDICATOR +  OPT_U + " uninstall app" +
            VenderItem.INDICATOR +  OPT_I + " app information";

    public MightyVender(int id) {
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName(".mt");
        mResult.setName(new String[]{".", "mt"});
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
        VenderItem.Value value = result.getValue();

        //wc.mt -u
        TreeSet<VenderItem> successors = result.getSuccessors();
        VenderItem suc = successors.first();
        String[] params = value.params;
        if (params.length > 1) {
            input(".mt only takes one param, ignoring the rest");
        }
        switch (params[0]) {
            case OPT_U:
                if (suc.getId() == VenderItem.BUILD_IN_ID_APP) {
                    AppManager.uninstall(context, suc.getValue().body);
                } else {
                    input(suc.getDisplayName() + " is not an application");
                }
                break;
            case OPT_I:
                if (suc.getId() == VenderItem.BUILD_IN_ID_APP) {
                    AppManager.info(context, suc.getValue().body);
                } else {
                    input(suc.getDisplayName() + " is not an application");
                }
                break;
            default:
                input(HELP);
        }
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {
        callback.onOutput("error, mt does not output");
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    public void execute(VenderItem result) {
        VenderItem.Value value = result.getValue();

        if (value.isEmpty()) {
            //.mt
            input(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //wc.mt
                input(HELP);
            } else {
                if (value.isPreEmpty()) {
                    //.mt -u
                    input(HELP);
                } else {

                }

            }
        }
    }

}
