package shinado.indi.lib.launcher;

import android.app.Activity;

import android.view.KeyEvent;
import android.view.WindowManager;



public abstract class BaseLauncherView extends Activity{

	protected FeedHelper feedHelper;
	public SearchHelper searchHelper;
	
	@Override
	public void onDestroy(){
        super.onDestroy();
		destroy();
	}

	public void addSearchable(Searchable searchable){
		searchHelper = new SearchHelper(this, searchable);
	}
	public void addFeedable(Feedable feedable){
		feedHelper = new FeedHelper(this, feedable);
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
