package indi.shinado.piping.util.android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.Value;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class AppManager {

    private AbsTranslator mTranslator;
    private static final String TAG = "App@Manager";

    public static AppManager getAppManager(Context context, AbsTranslator translator) {
        if (appManager == null) {
            appManager = new AppManager(context, translator);
        }
        return appManager;
    }

    private static AppManager appManager;
    private Context context;

    private HashMap<String, ResolveInfo> mActivityMap = new HashMap<>();
    private HashMap<String, ResolveInfo> mPackageMap = new HashMap<>();
    private PackageManager pm;

    private AppManager(Context context, AbsTranslator translator) {
        if (mTranslator == null) {
            mTranslator = translator;
        }
        this.context = context;
        pm = context.getPackageManager();
        loadApps();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(mReceiver, filter);
    }

    private void loadApps() {
        Log.d(TAG, "start loading apps");
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo app = list.get(i);

            if (app.activityInfo.packageName.equals(context.getPackageName())) {
                continue;
            }

            mActivityMap.put(app.activityInfo.name, app);
            mPackageMap.put(app.activityInfo.packageName, app);

        }
        Log.d(TAG, "end loading apps");
    }

    public int getAppCount() {
        return mActivityMap.size();
    }

    private ResolveInfo getInfo(int index) {
        ResolveInfo info = (ResolveInfo) mActivityMap.values().toArray()[index];
        return info;
    }

    public String getAppName(int index) {
        ResolveInfo info = getInfo(index);
        if (info != null) {
            return info.loadLabel(pm).toString();
        } else {
            return "";
        }
    }

    public String getAppName(String activityName) {
        return mActivityMap.get(activityName).loadLabel(pm).toString();
    }

    public Bitmap getIcon(int index) {
        ResolveInfo info = getInfo(index);
        return ((BitmapDrawable) (info.loadIcon(context.getPackageManager())))
                .getBitmap();
    }

    public Bitmap getIcon(String activityName) {
        ResolveInfo info = mActivityMap.get(activityName);
        return ((BitmapDrawable) (info.loadIcon(context.getPackageManager())))
                .getBitmap();
    }

    public String getActivityName(int position) {
        return (String) mActivityMap.keySet().toArray()[position];
    }

    public ResolveInfo getResolveByActivity(String activityName) {
        return mActivityMap.get(activityName);
    }

    public ResolveInfo getResolveByPackage(String packageName) {
        return mPackageMap.get(packageName);
    }

    public void launch(String value) {
        String[] split = value.split(",");
        ResolveInfo info = null;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        if (split[0].isEmpty() && !split[1].isEmpty()) {
            info = getResolveByActivity(split[1]);
        } else {
            info = getResolveByPackage(split[0]);
        }
        intent.setComponent(new ComponentName(info.activityInfo.applicationInfo.packageName,
                info.activityInfo.name));
        context.startActivity(intent);
    }

    public void launch(int idx) {
        String activityName = getActivityName(idx);
        launch(activityName);
    }

    private void onInstall(ApplicationInfo app) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(app.packageName);
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : list) {
            mActivityMap.put(info.activityInfo.name, info);
            mPackageMap.put(info.activityInfo.packageName, info);
        }

        if (onAppChangeListener != null) {
            for (OnAppChangeListener l : onAppChangeListener) {
                l.onAppChange(OnAppChangeListener.FLAG_ADD, getResult(app.packageName + ","));
            }
        }
    }

    public Pipe getResult(int idx) {
        ResolveInfo info = getInfo(idx);

        return getResult(info);
    }

    /**
     * "packageName,activityName"
     */
    private Pipe getResult(ResolveInfo info) {
        String value = info.activityInfo.packageName + "," + info.activityInfo.name;
        String label = info.loadLabel(pm).toString();
        Pipe item = new Pipe(Pipe.BUILD_IN_ID_APP, label, mTranslator.getName(label), new Value(value));
        return item;
    }

    private ResolveInfo getResolveByValue(String value) {
        String[] split = value.split(",");
        ResolveInfo info = null;
        if (split[0].isEmpty()) {
            info = getResolveByActivity(split[1]);
        } else {
            info = getResolveByPackage(split[0]);
        }

        return info;
    }

    public Pipe getResult(String value) {
        ResolveInfo info = getResolveByValue(value);

        if (info == null) {
            return new Pipe(Pipe.BUILD_IN_ID_APP, new Value(value));
        } else {
            value = info.activityInfo.packageName + "," + info.activityInfo.name;
            String label = info.loadLabel(pm).toString();
            return new Pipe(Pipe.BUILD_IN_ID_APP, label, mTranslator.getName(label), new Value(value));
        }

    }

    private void onUninstall(String packageName) {
        ResolveInfo info = getResolveByPackage(packageName);

        mPackageMap.remove(info.activityInfo.packageName);
        mActivityMap.remove(info.activityInfo.name);
        if (onAppChangeListener != null) {
            for (OnAppChangeListener l : onAppChangeListener) {
                l.onAppChange(OnAppChangeListener.FLAG_REMOVE, getResult(info));
            }
        }
    }

    public interface OnAppChangeListener {
        public int FLAG_REMOVE = 1;
        public int FLAG_ADD = 2;

        public void onAppChange(int flag, Pipe vo);
    }

    private ArrayList<OnAppChangeListener> onAppChangeListener =
            new ArrayList<OnAppChangeListener>();
    ;

    public void addOnAppChangeListener(OnAppChangeListener onAppChangeListener) {
        this.onAppChangeListener.add(onAppChangeListener);
    }

    public static void uninstall(Context context, String value) {
        String[] split = value.split(",");
        String pkgName = split[0];
        Uri uri = Uri.fromParts("package", pkgName, null);
        Intent it = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(it);
    }

    public static void info(Context context, String value) {
        String[] split = value.split(",");
        String pkgName = split[0];
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", pkgName, null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     *
     */
    public void destroy() {
        if (mTranslator != null) {
            mTranslator.destroy();
        }
        appManager = null;
        context.unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().replace("package:", "");
                ApplicationInfo info = null;
                try {
                    info = pm.getApplicationInfo(
                            packageName, PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                onInstall(info);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getDataString().replace("package:", "");
                onUninstall(packageName);
            }
        }
    };
}
