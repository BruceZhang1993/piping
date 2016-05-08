package indi.shinado.piping.pipes.impl.action;

import java.io.IOException;
import java.util.List;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.process.ProcessManager;
import indi.shinado.piping.process.models.AndroidAppProcess;

public class TaskPipe extends DefaultInputActionPipe {

    private static final String NAME = "process";
    private static final String OPT_LS = "ls";
    private static final String OPT_IDLE = "i";
    private static final String OPT_ALL = "all";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "[application]" + Keys.PIPE + NAME + " to kill a running application\n" +
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

        switch (params[0]) {
            case OPT_LS:
                StringBuilder sb = new StringBuilder();
                sb.append("List of running process:");
                sb.append("\n");
                List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses(getLauncher());
                for (AndroidAppProcess running : list) {
                    try {
                        sb.append(running.pid + " " + running.getPackageName() + " " + running.status().content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb.append("\n");
                }
                callback.onOutput(sb.toString());
                break;
            case OPT_IDLE:
                callback.onOutput("idle:" + ProcessManager.getMemoSize(getLauncher()));
                break;
            case OPT_ALL:
                List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses(getLauncher());
                getConsole().input("Number of running processes: " + runningAppProcesses.size());

                for (AndroidAppProcess running : runningAppProcesses) {
                    if (running.pid != android.os.Process.myPid()) {
                        ProcessManager.killProcess(running.pid);
                    }
                }

                callback.onOutput("idle:" + ProcessManager.getMemoSize(getLauncher()));
                break;
            default:
                callback.onOutput(HELP);
                break;
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (!result.getInstruction().isParamsEmpty()) {
            callback.onOutput("Parameters ignored.");
        }
        if (previous == null) {
            try {
                int pid = Integer.parseInt(input);
                android.os.Process.killProcess(pid);
            } catch (NumberFormatException e) {
                callback.onOutput("No application found");
            }
        } else {
            Pipe prev = previous.get();
            if (prev.getId() == PipesLoader.ID_APPLICATION) {
                String value = prev.getExecutable();
                String split[] = value.split(",");
                ProcessManager.killProcess(getLauncher(), split[0]);
            } else {
                callback.onOutput(prev.getDisplayName() + " is not an application");
            }
        }
        getConsole().notifyUI();
    }
}
