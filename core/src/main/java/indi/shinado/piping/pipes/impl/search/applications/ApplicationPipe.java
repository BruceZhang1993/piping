package indi.shinado.piping.pipes.impl.search.applications;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.impl.ShareIntent;
import indi.shinado.piping.pipes.search.FrequentPipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.util.android.AppManager;

public class ApplicationPipe extends FrequentPipe {

    private AppManager appManager;

    public ApplicationPipe(int id) {
        super(id);
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                while (!translator.ready()) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                refreshAppMessage(translator, listener, total);
            }
        }.start();
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        PackageManager pm = getLauncher().getPackageManager();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("*/*");

        ShareIntent shareIntent = new Gson().fromJson(input, ShareIntent.class);
        if (shareIntent != null) {
            sharingIntent.setType(shareIntent.type);
            for (String key : shareIntent.extras.keySet()) {
                sharingIntent.putExtra(key, Uri.parse(shareIntent.extras.get(key)));
            }
        } else {
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, input);
        }

        //looking for shareable apps
        List<ResolveInfo> list = pm.queryIntentActivities(sharingIntent, 0);
        String[] split = result.getExecutable().split(",");
        String pkg = split[0];
        for (ResolveInfo info : list) {
            if (pkg.equals(info.activityInfo.packageName)) {
                sharingIntent.setComponent(new ComponentName(split[0], info.activityInfo.name));
                getLauncher().startActivity(sharingIntent);
                return;
            }
        }

        sharingIntent.setComponent(new ComponentName(split[0], split[1]));
        getLauncher().startActivity(sharingIntent);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        ShareIntent shareIntent = new ShareIntent("application/vnd.android.package-archive");

        PackageManager pm = getLauncher().getPackageManager();
        ResolveInfo info = appManager.getResolveByPackage(result.getExecutable().split(",")[0]);
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(info.activityInfo.packageName, 0);
            shareIntent.extras.put(Intent.EXTRA_STREAM, appInfo.publicSourceDir);
//                output.put(Intent.EXTRA_TEXT, "market://details?id="+info.resolvePackageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        callback.onOutput(shareIntent.toString());
    }

    @Override
    protected void execute(Pipe rs) {
        String body = rs.getExecutable();
        if ("android".equals(body)) {
            return;
        }
        try {
            appManager.launch(body);
            addFrequency(rs);
        } catch (Exception e) {
            //if there is any trouble, remove it
            e.printStackTrace();
            removeFrequency(rs);
            removeItemInMap(rs);
        }
    }

    private void refreshAppMessage(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        appManager = AppManager.getInstance(getLauncher(), translator);
        appManager.start();
        new Thread() {
            public void run() {
                appManager.loadApps();
                appManager.addOnAppChangeListener(new AppManager.OnAppChangeListener() {
                    @Override
                    public void onAppChange(int flag, Pipe vo) {
                        if (flag == AppManager.OnAppChangeListener.FLAG_ADD) {
                            putItemInMap(vo);
                            addFrequency(vo);
                            getConsole().input("Application " + vo.getDisplayName() + " has been installed.");
                        } else if (flag == AppManager.OnAppChangeListener.FLAG_REMOVE) {
                            removeItemInMap(vo);
                            removeFrequency(vo);
                            getConsole().input("Application " + vo.getDisplayName() + " has been uninstalled.");
                        }
                    }
                });
                for (int i = 0; i < appManager.getAppCount(); i++) {
                    Pipe vo = appManager.getResult(i);
                    putItemInMap(vo);
                }
                listener.onItemsLoaded(ApplicationPipe.this.getId(), total);
            }
        }.start();

    }

    @Override
    public void destroy() {
        if (appManager != null) {
            appManager.destroy();
            appManager = null;
        }
    }

    @Override
    public Pipe getByValue(/** package name **/String value) {
        Pipe item = appManager.getResult(value + ",", true);
        item.setBasePipe(this);
        item.setInstruction(new Instruction(""));
        return item;
    }

}
