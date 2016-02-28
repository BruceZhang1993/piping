package shinado.indi.lib.items.action;

import android.content.Context;
import android.os.Handler;

import java.util.List;
import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.util.ProcessManager;

public class TaskVender extends PreActionVender {

    private VenderItem mResult;
    private ProcessManager pm;
    private static final String OPT_LS = "ls";
    private static final String OPT_IDLE = "i";

    private static final String HELP = "Usage of install:\n" +
            "[key].x [option]\n" +
            "where option includes:\n" +
            VenderItem.INDICATOR + OPT_LS + " list running process\n" +
            VenderItem.INDICATOR + OPT_IDLE + " show idle memory\n";

    public TaskVender(int id) {
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName(".x");
        mResult.setName(new String[]{".", "x"});
    }

    @Override
    public void init(Context context, SearchHelper s) {
        super.init(context, s);
        pm = new ProcessManager(context);
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
        TreeSet<VenderItem> successors = result.getSuccessors();
        VenderItem pre = successors.first();
        if (pre.getId() == VenderItem.BUILD_IN_ID_APP) {
            input("Killing: " + pre.getDisplayName());
            pm.killProcess(pre.getValue().body);
        } else {
            input("Target is not process");
        }
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {
        VenderItem.Value value = result.getValue();
        if (!value.isParamsEmpty()){
            String[] params = value.params;
            switch (params[0]) {
                case OPT_LS:
                    StringBuilder sb = new StringBuilder();
                    sb.append("List of running process:");
                    sb.append("\n");
                    List<String> list = pm.getRunningProcess();
                    for (String running : list) {
                        sb.append(running);
                        sb.append("\n");
                    }
                    callback.onOutput(sb.toString());
                    break;
                case OPT_IDLE:
                    callback.onOutput("idle:" + pm.getMemoSize());
                    break;
                default:
                    input(HELP);
            }
        }
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    protected VenderItem filter(VenderItem result) {
        return result;
    }

    @Override
    public void execute(VenderItem result) {
        VenderItem.Value value = result.getValue();

        if (value.isEmpty()) {
            //.x
            blockInput();
            input("Killing all");
            pm.killAll();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    input("idle:" + pm.getMemoSize());
                }
            }, 1000);
            releaseInput();
        } else {
            if (value.isParamsEmpty()) {
                //wc.x
                input("Process not found");
            } else {
                String[] params = value.params;
                if (params.length > 1) {
                    input(".x only takes one param, ignoring the rest");
                }
                if (value.isPreEmpty()) {
                    //.x -ls
                    switch (params[0]) {
                        case OPT_LS:
                            StringBuilder sb = new StringBuilder();
                            sb.append("List of running process:");
                            sb.append("\n");
                            List<String> list = pm.getRunningProcess();
                            for (String running : list) {
                                sb.append(running);
                                sb.append("\n");
                            }
                            input(sb.toString());
                            break;
                        case OPT_IDLE:
                            input("idle:" + pm.getMemoSize());
                            break;
                        default:
                            input(HELP);
                    }
                } else {
                    //wc.x -ls
                    input(HELP);
                }
            }
        }
    }
}
