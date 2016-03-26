package indi.shinado.piping.pipes.impl.action;

import java.util.List;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.util.android.ProcessManager;

public class TaskPipe extends DefaultInputActionPipe{

    private static final String NAME = "$kill";
    private static final String OPT_LS = "ls";
    private static final String OPT_IDLE = "i";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "[application]"+ Keys.PIPE +"kill to kill a running application\n" +
            "kill " + Keys.PARAMS + OPT_LS + " to list running processes\n" +
            "kill " + Keys.PARAMS + OPT_IDLE + " to print idle memories";

    private ProcessManager pm;

    public TaskPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"kill"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput(HELP);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1) {
            callback.onOutput("Warning:" + NAME + " takes only one param, ignoring the rests.");
        }

        switch (params[0]){
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
                callback.onOutput(HELP);
                break;
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (!result.getInstruction().isParamsEmpty()){
            callback.onOutput("Parameters ignored.");
        }
        if (previous == null){
            callback.onOutput("No application found");
        }else{
            Pipe prev = previous.get();
            if (prev.getId() == PipesLoader.ID_APPLICATION) {
                if (pm == null){
                    pm = new ProcessManager(getLauncher());
                }
                String value = prev.getExecutable();
                String split[] = value.split(",");
                pm.killProcess(split[0]);
            } else {
                callback.onOutput(prev.getDisplayName() + " is not an application");
            }
        }
    }
}
