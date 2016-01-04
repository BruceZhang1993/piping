package shinado.indi.lib.items.action;

import android.content.Context;
import android.os.Handler;

import java.util.List;
import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.util.ProcessManager;

public class TaskVender extends PreActionVender{

    private VenderItem mResult;
    private ProcessManager pm;
    private static final String OPT_LS = "-ls";
    private static final String OPT_IDLE = "-i";

    private static final String HELP = "Usage of install:\n" +
            "[key].x [-option]\n" +
            "where option includes:\n" +
            OPT_LS + " list running process\n" +
            OPT_IDLE + " show idle memory\n";

    public TaskVender(){
        mResult = new VenderItem();
        mResult.setId(VenderItem.BUILD_IN_ID_TASKS);
        mResult.setDisplayName(".x");
        mResult.setValue("");
        mResult.setName(new String[]{".", "x"});
    }

    @Override
    public void init(Context context, SearchHelper s, int id) {
        super.init(context, s, id);
        pm = new ProcessManager(context);
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
    public int function(VenderItem result) {
        String value = result.getValue();
        TreeSet<VenderItem> successors = result.getSuccessors();
        if (value == null || value.trim().isEmpty()){
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
        }else{
            if (value.contains(" ")){
                String[] split = value.split(" ", 2);
                //e.g, ".x -ls" -> " -ls"
                if (split[0].equals("")){
                    String ins = split[1];
                    switch (ins){
                        case OPT_LS:
                            StringBuilder sb = new StringBuilder();
                            sb.append("List of running process:");
                            sb.append("\n");
                            List<String> list = pm.getRunningProcess();
                            for (String running:list){
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
                }
                else{
                    //e.g.  "k.x -ls" -> "k -ls"
                    input(HELP);
                }
            }
            //e.g.  "k.x" -> "k"
            else{
                if (successors != null && successors.size() > 0){
                    VenderItem pre = successors.first();
                    if (pre.getId() == VenderItem.BUILD_IN_ID_APP){
                        input("Killing: " + pre.getDisplayName());
                        pm.killProcess(pre.getValue());
                    }else {
                        input("Target is not process");
                    }
                    return 0;
                }else{
                    input("Process not found");
                }
            }

        }

        return 0;
    }
}
