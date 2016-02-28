package indi.shinado.piping.util.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

	private ActivityManager am;
	private Context context;
	
	private ArrayList<String> filter;
	
	public ProcessManager(Context context){
		this.am = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
		this.context = context;
		
		addFilter();
	}

	private void addFilter(){
		filter = new ArrayList<String>();
		filter.add("com.android");
		filter.add("android.process");
	}
	
	public String getMemoSize(){
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(memoryInfo) ;  
		long memSize = memoryInfo.availMem ;      
		return Formatter.formatFileSize(context, memSize);
	}
	
	public void runLastTask(){
		List<RunningTaskInfo> runningTask = am.getRunningTasks(5);
		for(int i=1; i< runningTask.size(); i++){
			RunningTaskInfo info = runningTask.get(1);
			if(info.numRunning > 0){
				String pkgName = info.topActivity.getPackageName();
				Log.e("pkgName", pkgName);
				Intent it = context.getPackageManager().getLaunchIntentForPackage(pkgName);
				if(it != null){
					it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					context.startActivity(it);
					return;
				}
			} 
		}
	}

	public ArrayList<String> getRunningProcess(){
		ArrayList<String> list = new ArrayList<String>();
		List<RunningAppProcessInfo> runningProcess = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info:runningProcess){
			list.add(info.processName);
		}
		return list;
	} 
	
	public void killAll(){
		List<RunningAppProcessInfo> runningProcess = am.getRunningAppProcesses();
		for (RunningAppProcessInfo amPro : runningProcess){
            String processName = amPro.processName;  
            am.killBackgroundProcesses(processName);
        } 
	}
	
	public void killProcess(String value){
		am.killBackgroundProcesses(value);
	}
	
	public boolean isFiltered(String process){
		for(int i=0; i<filter.size(); i++){
			if(process.contains(filter.get(i))){
				return true;
			}
		}
		return false;
	}
}
