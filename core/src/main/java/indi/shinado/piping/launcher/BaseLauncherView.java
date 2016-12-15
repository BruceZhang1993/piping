package indi.shinado.piping.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shinado.annotation.TargetVersion;
import com.shinado.core.R;

import java.util.ArrayList;

import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.color.ColorActivity;
import indi.shinado.piping.feed.BarService;
import indi.shinado.piping.feed.FeedHelper;
import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.settings.ConsoleAnimation;
import indi.shinado.piping.settings.Preferences;
import indi.shinado.piping.statusbar.BatteryStatusBar;
import indi.shinado.piping.statusbar.ConnectionStatusBar;
import indi.shinado.piping.statusbar.StatusBar;
import indi.shinado.piping.statusbar.Statusable;
import indi.shinado.piping.statusbar.TimeStatusBar;
import indi.shinado.piping.view.BoundaryView;


public abstract class BaseLauncherView extends Activity{

	private ArrayList<StatusBar> mStatusBars = new ArrayList<>();
	private Intent mBarService;
	private FeedHelper feedHelper;
	protected Preferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = new Preferences(this);
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

	public void addFeedable(Feedable feedable){
		feedHelper = new FeedHelper(this, feedable);
		feedHelper.start();
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
		if(feedHelper != null){
			feedHelper.destroy();
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
