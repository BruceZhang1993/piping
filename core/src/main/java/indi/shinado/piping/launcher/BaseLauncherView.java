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
import android.widget.TextView;

import java.util.ArrayList;

import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.color.ColorActivity;
import indi.shinado.piping.feed.BarService;
import indi.shinado.piping.feed.FeedHelper;
import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.settings.Preferences;
import indi.shinado.piping.statusbar.BatteryStatusBar;
import indi.shinado.piping.statusbar.ConnectionStatusBar;
import indi.shinado.piping.statusbar.StatusBar;
import indi.shinado.piping.statusbar.Statusable;
import indi.shinado.piping.statusbar.TimeStatusBar;


public abstract class BaseLauncherView extends Activity{

	private static final int REQUEST_COLOR = 1;
	private ArrayList<StatusBar> mStatusBars = new ArrayList<>();
	private Intent mBarService;
	private FeedHelper feedHelper;
	protected Preferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = new Preferences(this);
		initWallpaper();
		mBarService = new Intent(this, BarService.class);
		startService(mBarService);
		IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
		registerReceiver(mWallpaperSetReceiver, filter);
	}

	@Override
	public void onDestroy(){
        super.onDestroy();
		stopService(mBarService);
		unregisterReceiver(mWallpaperSetReceiver);
		destroy();
	}

	private void initWallpaper(){
		if (mPref.isWallpaperSet()){
			setWallpaper();
		}
	}

	@SuppressWarnings("deprecation")
	private void setWallpaper(){
		WallpaperManager wallpaperManager
				= WallpaperManager.getInstance(getApplicationContext());
		Drawable drawable = wallpaperManager.getDrawable();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			getBackgroundView().setBackground(drawable);
		}else {
			getBackgroundView().setBackgroundDrawable(drawable);
		}
	}

	public void selectWallpaper(){
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
		startActivity(Intent.createChooser(intent, "Select Wallpaper"));
	}

	public void selectColor(){
		startActivityForResult(
				new Intent(this, ColorActivity.class),
				REQUEST_COLOR);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		switch (requestCode) {
			case REQUEST_COLOR:
				if(resultCode == RESULT_OK){
					int color = intent.getIntExtra(GlobalDefs.EXTRA_COLOR, 0);
					getConsoleTextView().setTextColor(color);
					mPref.setColor(color);
				}
				break;
		}
	}

	private BroadcastReceiver mWallpaperSetReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mPref.setWallpaper(true);
			setWallpaper();
		}
	};


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

	public abstract View getBackgroundView();

	public abstract TextView getConsoleTextView();
}
