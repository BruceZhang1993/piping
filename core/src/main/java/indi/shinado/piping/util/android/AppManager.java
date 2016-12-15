package indi.shinado.piping.util.android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.impl.search.applications.SimpleAppInfo;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class AppManager extends SearchableItemManager {

    private static final String TAG = "App@Manager";

    private HashMap<String, ResolveInfo> mActivityMap = new HashMap<>();
    private HashMap<String, ResolveInfo> mPackageMap = new HashMap<>();
    private TreeSet<SimpleAppInfo> mAppList = new TreeSet();
    private PackageManager pm;
    private static AppManager appManager;

    AppManager(Context context, AbsTranslator translator) {
        super(context, translator);
        pm = context.getPackageManager();
    }

    public static AppManager getInstance(Context context, AbsTranslator translator) {
        if (appManager == null) {
            appManager = new AppManager(context, translator);
        }
        return appManager;
    }

    public static AppManager getInstance() {
        return appManager;
    }

    public void loadApps() {
        Log.d(TAG, "start loading apps");
        onInstall(null);
//        PackageManager pm = context.getPackageManager();
//        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
//        for (int i = 0; i < list.size(); i++) {
//            ResolveInfo app = list.get(i);
//
////            if (app.activityInfo.packageName.equals(context.getPackageName())) {
////                continue;
////            }
//
//            mActivityMap.put(app.activityInfo.name, app);
//            mPackageMap.put(app.activityInfo.packageName, app);
//
//            try {
//                ApplicationInfo appInfo = pm.getApplicationInfo(app.activityInfo.packageName, 0);
//                String appFile = appInfo.sourceDir;
//                long installTime = new File(appFile).lastModified(); //Epoch Time
//                mAppList.add(new SimpleAppInfo(installTime, app.activityInfo.name, app.activityInfo.packageName));
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        Log.d(TAG, "end loading apps");
    }

    public int getAppCount() {
        return mActivityMap.size();
    }

    private ResolveInfo getInfo(int index) {
        return (ResolveInfo) mActivityMap.values().toArray()[index];
    }

    public TreeSet<SimpleAppInfo> getAppList(){
        return mAppList;
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
        ResolveInfo info;
        Intent intent = new Intent();
        //with FLAG_ACTIVITY_MULTIPLE_TASK, QQLite would not launch occasionally
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION  /** | Intent.FLAG_ACTIVITY_MULTIPLE_TASK **/
        );

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
        if (app != null){
            mainIntent.setPackage(app.packageName);
        }
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo info : list) {
            mActivityMap.put(info.activityInfo.name, info);
            mPackageMap.put(info.activityInfo.packageName, info);

            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(info.activityInfo.packageName, 0);
                String appFile = appInfo.sourceDir;
                long installTime = new File(appFile).lastModified(); //Epoch Time
                Log.d(TAG, "app: " + info.activityInfo.name + ", " + new Date(installTime).toString());
                mAppList.add(new SimpleAppInfo(installTime, appFile, info.activityInfo.name, info.activityInfo.packageName));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (app != null){
            if (onAppChangeListener != null) {
                for (OnAppChangeListener l : onAppChangeListener) {
                    l.onAppChange(OnAppChangeListener.FLAG_ADD, getResult(app.packageName + ",", true));
                }
            }
        }
    }

    public Pipe getResult(int idx) {
        ResolveInfo info = getInfo(idx);

        return getResult(info, true);
    }

    /**
     * "packageName,activityName"
     */
    private Pipe getResult(ResolveInfo info, boolean searchableNamRequired) {
        String value = info.activityInfo.packageName + "," + info.activityInfo.name;
        String label = info.loadLabel(pm).toString();
        Pipe item;
        if (searchableNamRequired) {
            AbsTranslator translator = getTranslator();
            item = new Pipe(PipesLoader.ID_APPLICATION, label, translator.getName(label), value);
        } else {
            item = new Pipe(PipesLoader.ID_APPLICATION, label, new SearchableName(new String[]{}), value);
        }
        return item;
    }

    public Pipe getResult(String value, boolean searchableNamRequired) {
        ResolveInfo info = getResolveByValue(value);

        if (info == null) {
            return new Pipe(PipesLoader.ID_APPLICATION, new Instruction(value));
        } else {
            value = info.activityInfo.packageName + "," + info.activityInfo.name;
            String label = info.loadLabel(pm).toString();
            Pipe item;
            if (searchableNamRequired) {
                AbsTranslator translator = getTranslator();
                item = new Pipe(PipesLoader.ID_APPLICATION, label, translator.getName(label), value);
            } else {
                item = new Pipe(PipesLoader.ID_APPLICATION, label, new SearchableName(new String[]{}), value);
            }
            return item;
        }

    }

    private ResolveInfo getResolveByValue(String value) {
        String[] split = value.split(",");
        ResolveInfo info;
        if (split[0].isEmpty()) {
            info = getResolveByActivity(split[1]);
        } else {
            info = getResolveByPackage(split[0]);
        }
        return info;
    }

    private void onUninstall(String packageName) {
        ResolveInfo info = getResolveByPackage(packageName);
        Pipe result = getResult(packageName, false);

        mPackageMap.remove(info.activityInfo.packageName);
        mActivityMap.remove(info.activityInfo.name);
        for (SimpleAppInfo simpleAppInfo : mAppList){
            if (simpleAppInfo.packageName.equals(packageName)){
                mAppList.remove(simpleAppInfo);
                break;
            }
        }

        if (onAppChangeListener != null) {
            for (OnAppChangeListener l : onAppChangeListener) {
                l.onAppChange(OnAppChangeListener.FLAG_REMOVE, result);
            }
        }
    }

    public interface OnAppChangeListener {
        int FLAG_REMOVE = 1;
        int FLAG_ADD = 2;

        void onAppChange(int flag, Pipe vo);
    }

    private ArrayList<OnAppChangeListener> onAppChangeListener =
            new ArrayList<>();

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

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    public void unregister() {
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
