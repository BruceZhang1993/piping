package indi.shinado.piping.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import java.util.ArrayList;

import indi.shinado.piping.statusbar.BarService;
import indi.shinado.piping.statusbar.BatteryStatusBar;
import indi.shinado.piping.statusbar.ConnectionStatusBar;
import indi.shinado.piping.statusbar.StatusBar;
import indi.shinado.piping.statusbar.Statusable;
import indi.shinado.piping.statusbar.TimeStatusBar;


public abstract class BaseLauncherView extends Activity{

	private ArrayList<StatusBar> mStatusBars = new ArrayList<>();
	private Intent mBarService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBarService = new Intent(this, BarService.class);
		startService(mBarService);
	}

	@Override
	public void onDestroy(){
        super.onDestroy();
		stopService(mBarService);
		destroy();
	}

	public ArrayList<StatusBar> addStatusable(Statusable statusable){
		loadLocalStatusBar(statusable);
		loadServerStatusBar(statusable);
		for (StatusBar sb : mStatusBars){
			sb.register();
		}
		return mStatusBars;
	}

	private void loadLocalStatusBar(Statusable sb){
		TimeStatusBar timeSb = new TimeStatusBar(this, sb, Statusable.ID_TIME);
		mStatusBars.add(timeSb);

		ConnectionStatusBar connSb = new ConnectionStatusBar(this, sb, Statusable.ID_CONNECTION);
		mStatusBars.add(connSb);

		BatteryStatusBar batterySb = new BatteryStatusBar(this, sb, Statusable.ID_BATTERY);
		mStatusBars.add(batterySb);
	}

	private void loadServerStatusBar(Statusable sb){
		//TODO
	}

	public void addSearchable(){
		//TODO
	}

	/**
	 * @param animNotRequired true if not needed to animate back
	 */
	protected void resume(boolean animNotRequired){
	}

	public void destroy(){
		for (StatusBar sb : mStatusBars){
			sb.unregister();
		}
	}

	private boolean flag = false;

	@Override
	public void onResume(){
        super.onResume();

		resume(flag);
		flag = false;
	}
	
	@Override
	public void onStart(){
        super.onStart();
        
		flag = true;
	}
	
	@Override
	public void onStop(){
        super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_HOME:
				return true;
			case KeyEvent.KEYCODE_BACK:
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
