package com.shinado.app.task;

import java.util.List;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.process.ProcessManager;
import indi.shinado.piping.process.models.AndroidAppProcess;

public class TaskPipe extends DefaultInputActionPipe{

    private static final String NAME = "process";
    private static final String OPT_LS = "ls";
    private static final String OPT_IDLE = "i";
    private static final String OPT_ALL = "all";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "[application]"+ Keys.PIPE + NAME +" to kill a running application\n" +
            NAME + " " + Keys.PARAMS + OPT_LS + " to list running processes\n" +
            NAME + " " + Keys.PARAMS + OPT_IDLE + " to print idle memories\n" +
            NAME + " " + Keys.PARAMS + OPT_ALL + " to kill all process";

    public TaskPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$" + NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{NAME});
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
                List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses();
                for (AndroidAppProcess running : list) {
                    sb.append(running.getPackageName());
                    sb.append("\n");
                }
                callback.onOutput(sb.toString());
                break;
            case OPT_IDLE:
                callback.onOutput("idle:" + ProcessManager.getMemoSize(getLauncher()));
                break;
            case OPT_ALL:
                List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
                getConsole().input("Number of running processes: " + runningAppProcesses.size());
                ProcessManager.killAll(getLauncher());
                callback.onOutput("idle:" + ProcessManager.getMemoSize(getLauncher()));
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
                String value = prev.getExecutable();
                String split[] = value.split(",");
                ProcessManager.killProcess(getLauncher(), split[0]);
            } else {
                callback.onOutput(prev.getDisplayName() + " is not an application");
            }
        }
    }
}
