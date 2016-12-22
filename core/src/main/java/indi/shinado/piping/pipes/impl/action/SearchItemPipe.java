package indi.shinado.piping.pipes.impl.action;

import java.util.TreeSet;
import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.search.applications.SimpleAppInfo;
import indi.shinado.piping.util.android.AppManager;

public class SearchItemPipe extends ActionPipe {

    private static final int DEFAULT_COUNT = 10;
    private Pipe mResult;

    public SearchItemPipe(int id) {
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$list");
        mResult.setSearchableName(new SearchableName("list"));
        mResult.setBasePipe(this);
        mResult.setAcceptableParams(new Pipe(getId(), "a"));
    }

    private void roll(int count){
        AppManager appManager = AppManager.getInstance();
        if (appManager == null){
            getConsole().input("Error, can not load application info.");
        }else{
            String msg = "Latest install:\n";
            TreeSet<SimpleAppInfo> appList = appManager.getAppList();
            int i =0 ;
            for (SimpleAppInfo appInfo : appList){
                String appName = appManager.getAppName(appInfo.activityName);
                msg += appName + "\n";
                if (++i >= count){
                    break;
                }
            }
            getConsole().input(msg);
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (!result.getInstruction().isParamsEmpty()) {
            String param = result.getInstruction().params[0];
            if ("a".equals(param)){
                roll(Integer.MAX_VALUE);
            }
        }

        if (previous == null) {
            roll(DEFAULT_COUNT);
        } else {
            TreeSet<Pipe> all = previous.getAll();
            StringBuilder sb = new StringBuilder();
            for (Pipe item : all) {
                sb.append(item.getDisplayName());
                sb.append("\n");
            }
            callback.onOutput(sb.toString());
        }
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("error, cannot get output from $ls");
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void execute(Pipe result) {
        getConsole().input("$ls must take an application or contact");
    }

}
