package shinado.indi.lib.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.translator.AbsTranslator;

public class AppManager {

	private AbsTranslator mTranslator;
	private static final String TAG = "App@Manager";
	public static AppManager getAppManager(Context context, AbsTranslator translator){
		if(appManager == null){
			appManager = new AppManager(context, translator);
		}
		return appManager;
	}
	private static AppManager appManager;
	private Context context;
	
	private ArrayList<String> activityNames = new ArrayList<String>();
	private List<ResolveInfo> resolveInfo = new ArrayList<ResolveInfo>();
	private PackageManager pm;
	
	public static final int APP_HOME = -315273;
	public static final String DESTROY_OR_CREATE = "doc";
	public static final String ACTION_DESTROY_OR_CREATE = "com.shinado.doc";
	public static final String ACTION_LONG_TOUCH = "com.shinado.long_touch";
	
	private AppManager(Context context, AbsTranslator translator){
		this.mTranslator = translator;
		this.context = context;
		pm = context.getPackageManager();
		loadApps();
	}

	private void loadApps(){
		Log.d(TAG, "start loading apps");
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
		for(int i=0; i<list.size(); i++){
			ResolveInfo app = list.get(i);
			if (app.resolvePackageName != null){
				if (app.resolvePackageName.equals(context.getPackageName())){
					continue;
				}
			}
			activityNames.add(app.activityInfo.name);
			resolveInfo.add(app);
		}
		Log.d(TAG, "end loading apps");
    }

	public int getAppCount(){
		return activityNames.size();
    }
	public String getAppName(int index){
		ResolveInfo info = resolveInfo.get(index);
		if(info != null){
			return info.loadLabel(pm).toString();
		}else{
			return "";
		}
    }
	public String getAppName(String activityName){
		int index = activityNames.indexOf(activityName);
		if(index >= 0){
			return getAppName(index);
		}else{
			return "";
		}
    }
	public Bitmap getIcon(int index){
		ResolveInfo info = resolveInfo.get(index);
		if(info != null){
			return ((BitmapDrawable)(info.loadIcon(context.getPackageManager())))
				.getBitmap();
		}else{
			return null;
		}
    }
	public Bitmap getIcon(String activityName){
		int index = activityNames.indexOf(activityName);
		if(index >= 0){
			return getIcon(index);
		}else{
			return null;
		}
    }
	public String getActivityName(int position){
		return activityNames.get(position);
    }
	public ResolveInfo getResolve(String activityName){
		int index = activityNames.indexOf(activityName);
		if(index >= 0){
			return resolveInfo.get(index);
		}else{
			return null;
		}
	}
	
	public void launch(String activityName)
    {
		ResolveInfo info = getResolve(activityName);
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(info.activityInfo.applicationInfo.packageName,
				activityName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_NO_ANIMATION |
				Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		context.startActivity(intent); 
    }

	public void launch(int idx)
    {
		String activityName = getActivityName(idx);
		launch(activityName);
    }
	
	public void onInstall(ApplicationInfo app){
		System.out.println("appChange:init add");
		activityNames.add(app.packageName);

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(app.packageName);
		List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
		resolveInfo.addAll(list);
		
		if(onAppChangeListener != null){
			for(OnAppChangeListener l:onAppChangeListener){
				l.onAppChange(OnAppChangeListener.FLAG_ADD, getResult(app.packageName));
			}  
		}
	}

	public VenderItem getResult(int idx){
		String value = getActivityName(idx);
		return getResult(value);
	}
	    
	public VenderItem getResult(String value){
		String label = appManager.getAppName(value);
		return new VenderItem(value, label, mTranslator.getName(label), VenderItem.BUILD_IN_ID_APP);
	}
	    
	public void onUninstall(String name){
		System.out.println("appChange:init remove");
		int index = activityNames.indexOf(name);
		activityNames.remove(index);
		resolveInfo.remove(index);
		if(onAppChangeListener != null){
			for(OnAppChangeListener l:onAppChangeListener){
				l.onAppChange(OnAppChangeListener.FLAG_REMOVE, new VenderItem(name));
			}
		}
	}

	public interface OnAppChangeListener{
		public int FLAG_REMOVE = 1;
		public int FLAG_ADD = 2;
		public void onAppChange(int flag, VenderItem vo);
	}
	private ArrayList<OnAppChangeListener> onAppChangeListener =
			new ArrayList<OnAppChangeListener>();;
	public void addOnAppChangeListener(OnAppChangeListener onAppChangeListener) {
		this.onAppChangeListener.add(onAppChangeListener);
	}

	public void removeApp(String activityName) {
		String pkgName = getResolve(activityName).activityInfo.applicationInfo.packageName;
		Uri uri = Uri.fromParts("package", pkgName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(it); 
	}
	
	public void removeApp(int idx){
		removeApp(getActivityName(idx));
	}
	
	public void detailApp(String activityName) {
		String pkgName = getResolve(activityName).activityInfo.applicationInfo.packageName;
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");  
		Uri uri = Uri.fromParts("package", pkgName, null);  
		intent.setData(uri);  
		context.startActivity(intent);  
	}
	
	public void detailApp(int idx){
		detailApp(getActivityName(idx));
	}

	public void destroy() {
		mTranslator.destroy();
		appManager = null;
	}

}
