package indi.shinado.piping.pipes.impl.action;

import android.content.pm.ResolveInfo;

import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.search.applications.SimpleAppInfo;
import indi.shinado.piping.util.android.AppManager;

/**
 * Created by shinado on 16/5/15.
 */
public class LastInstallPipe extends DefaultInputActionPipe{

    private static final String NAME = "$last-install";
    private int defaultCount = 5;

    public LastInstallPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"last", "install"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        AppManager appManager = AppManager.getInstance();
        if (appManager == null){
            getConsole().input("Error, can not load application info.");
        }else{
            String msg = "";
            TreeSet<SimpleAppInfo> appList = appManager.getAppList();
            int i =0 ;
            for (SimpleAppInfo appInfo : appList){
                String appName = appManager.getAppName(appInfo.activityName);
                msg += appName +"\n";
                if (++i >= defaultCount){
                    break;
                }
            }
            getConsole().input(msg);
        }
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }
}
