package shinado.indi.lib.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.ryg.dynamicload.DLBasePluginActivity;

import shinado.indi.lib.GlobalDefs;

public abstract class BaseLauncherView extends DLBasePluginActivity{

	protected FeedHelper feedHelper;
	public SearchHelper searchHelper;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

//		Intent service = new Intent(mProxyActivity, CharmingService.class);
//		mProxyActivity.startService(service);

		IntentFilter filter = new IntentFilter(GlobalDefs.ACTION_CHANGE_THEME);
		filter.addAction(GlobalDefs.ACTION_RESTART);
		filter.addAction(GlobalDefs.ACTION_LOCK);
		filter.addAction(GlobalDefs.ACTION_QUIT);
		mProxyActivity.registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy(){
        super.onDestroy();
//		mProxyActivity.stopService(new Intent(this, CharmingService.class));
		mProxyActivity.unregisterReceiver(mReceiver);
		destroy();
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("receive:"+action);
			if(GlobalDefs.ACTION_CHANGE_THEME.equals(action)){
				mProxyActivity.finish();
			}else if(GlobalDefs.ACTION_RESTART.equals(action)){
				mProxyActivity.finish();
			}else if(GlobalDefs.ACTION_LOCK.equals(action)){
				System.out.println("onlock");
				onLock(mProxyActivity.getWindowManager());
			}else if(GlobalDefs.ACTION_QUIT.equals(action)){
				mProxyActivity.finish();
			}
		}
	};

	public void addSearchable(Searchable searchable){
		searchHelper = new SearchHelper(that, searchable);
	}
	public void addFeedable(Feedable feedable){
		feedHelper = new FeedHelper(that, feedable);
	}

	/**
	 * @param animNotRequired true if not needed to animate back
	 */
	protected void resume(boolean animNotRequired){
	}

	public void destroy(){
		if(feedHelper != null){
			feedHelper.destroy();
		}
	}

	public void onBack(){

	}

	public void onLock(WindowManager wm){

	}

	private boolean flag = false;


	@Override
	public void onResume(){
        super.onResume();

		resume(flag);
		System.out.println("on resume"+flag);
		flag = false;
	}
	
	@Override
	public void onStart(){
        super.onStart();
        
		flag = true;
		mProxyActivity.sendBroadcast(new Intent(GlobalDefs.ACTION_START));
	}
	
	@Override
	public void onStop(){
        super.onStop();
        mProxyActivity.sendBroadcast(new Intent(GlobalDefs.ACTION_STOP));
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_HOME:
				return true;
			case KeyEvent.KEYCODE_BACK:
				onBack();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
