package com.shinado.app.task;

import java.util.List;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.util.android.ProcessManager;

public class TaskPipe extends DefaultInputActionPipe{

    private static final String NAME = "$kill";
    private static final String OPT_LS = "ls";
    private static final String OPT_IDLE = "i";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "[application].kill to kill a running application\n" +
            "kill -" + OPT_LS + " to list running processes\n" +
            "kill -" + OPT_IDLE + " to print idle memories";

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
    public void onEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void onParamsEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void onPreEmpty(Pipe rs, IInput input) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1) {
            input.input("Warning:" + NAME + " takes only one param, ignoring the rests.");
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
                input.input(sb.toString());
                break;
            case OPT_IDLE:
                input.input("idle:" + pm.getMemoSize());
                break;
            default:
                input.input(HELP);
                break;
        }
    }

    @Override
    public void onNoEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void acceptInput(Pipe result, String input) {
        Pipe prev = result.getPrevious().get();
        if (prev.getId() == PipesLoader.ID_APPLICATION) {
            if (pm == null){
                pm = new ProcessManager(context);
            }
            String value = prev.getExecutable();
            String split[] = value.split(",");
            pm.killProcess(split[0]);
        } else {
            getConsole().input(prev.getDisplayName() + " is not an application");
        }
    }
}
