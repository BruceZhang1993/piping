package shinado.indi.vender.base;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.BaseLauncherView;
import shinado.indi.lib.launcher.Searchable;
import shinado.indi.lib.util.CommonUtil;
import shinado.indi.vender.R;

public class BaseLauncher extends BaseLauncherView implements Searchable {

	private final String TAG = "IFC BaseLauncher";
	private EditText input_search;

	private boolean initializing = true;
	private boolean isRun = true;
	private TextView showView;
	private ScrollView scrollView;
	private View keyboard;
	private StringBuffer mPreviousLines = new StringBuffer();
	private String mCurrentLine = "";

	private int resultIndex = 0;
	private TreeSet<VenderItem> resultList;
	private String mInputText = "";

	private OutputHandler mHandler;
	private boolean initiated = false;

	private final String[] INIT_TEXT = {"wsp_stp_auth_build.bui",
			"Account:******",
			"Password:***************",
			"Access granted",
			"initializing"};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= 16){
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}else{

		}

		that.setContentView(R.layout.layout_base_launcher);

		mHandler = new OutputHandler(this);
		initViews();

		addSearchable(this);
	}

	@Override
	public void resume(boolean animNotRequired){
		super.resume(animNotRequired);
		//if not initiated yet, initiate the text
		if(!initiated){
			initText();
			initiated = true;
		}
		//re-initiate text if animation is required
		else if(!animNotRequired){
			initText();
		}
	}

	//clean text and start initiate text animation
	private void initText(){
		mPreviousLines = new StringBuffer();
		input_search.setText("");
		new InitTextingThread().start();
	}

	private void initViews(){
		scrollView = (ScrollView) that.findViewById(R.id.scrollView);
		keyboard = that.findViewById(R.id.keyboard);
		showView = (TextView) that.findViewById(R.id.displayText);
		initSearchWidget();
		startTicking();
	}

	//ticking_
	//ticking
	//ticking_
	//ticking
	private void startTicking(){
		new TickThread().start();
	}

	@Override
	public void destroy(){
		super.destroy();
		isRun = false;
	}
	
	private static final int WHAT_TIK = 1;
	private static final int WHAT_TOK = 0;
	private static final int WHAT_INPUT = 2;
	private static final int WHAT_START = 3;
	private static final int WHAT_HIDE = 4;
	private static final int WHAT_LINE = 5;

	static class OutputHandler extends Handler{

		private WeakReference<BaseLauncher> deskViewWeakReference;

		public OutputHandler(BaseLauncher baseLauncher){
			deskViewWeakReference = new WeakReference<>(baseLauncher);
		}

		@Override
		public void handleMessage(Message msg){
			BaseLauncher baseLauncher = deskViewWeakReference.get();
			switch(msg.what){
				case WHAT_TIK:
					baseLauncher.showView.setText(baseLauncher.mPreviousLines.toString() + baseLauncher.mCurrentLine + "_");
					break;
				case WHAT_TOK:
					baseLauncher.showView.setText(baseLauncher.mPreviousLines.toString() + baseLauncher.mCurrentLine);
					break;
				case WHAT_INPUT:
					baseLauncher.mPreviousLines.append(msg.obj);
					baseLauncher.showView.setText(baseLauncher.mPreviousLines.toString());
					baseLauncher.scrollView.fullScroll(View.FOCUS_DOWN);
					break;
				case WHAT_START:
					baseLauncher.input_search.setText("");
					baseLauncher.showKeyboard();
					break;
				case WHAT_HIDE:
					baseLauncher.hideKeyboard();
					break;
				case WHAT_LINE:
					baseLauncher.replaceLine((String) msg.obj);
					break;
			}
		}
	}

	private void initSearchWidget(){
		input_search = new EditText(that, null);
	}

	private void hideKeyboard(){
		keyboard.setVisibility(View.GONE);
	}
	
	private void showKeyboard(){
		int TIME_SHOW = 150;
		keyboard.setVisibility(View.VISIBLE);
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1, 
				Animation.RELATIVE_TO_SELF, 0);
		anim.setDuration(TIME_SHOW);
		anim.setInterpolator(new DecelerateInterpolator(3.0f));
		keyboard.startAnimation(anim);
	}

	@Override
	public EditText shapSearchInput() {
		return input_search;
	}

	@Override
	public void onLock(WindowManager wm) {
	}

	@Override
	public ViewGroup getKeyboard() {
		return (ViewGroup) keyboard;
	}

	@Override
	public void onNotified(TreeSet<VenderItem> result_set) {
		Log.d(TAG, "onNotified");
		resultList = result_set;
		resultIndex = 0;
		replaceLine(false);
	}

	@Override
	public void onShift(){
		if(resultList == null || resultList.size() == 0){
			return;
		}
		resultIndex = (resultIndex+1) % resultList.size();
		replaceLine(true);
	}

	@Override
	public void onDisplay(final String msg, final int flag) {
		new Thread(){
			@Override
			public void run(){
				switch (flag){
					case FLAG_REPLACE:
						mHandler.obtainMessage(WHAT_LINE, msg).sendToTarget();
						break;
					case FLAG_INPUT:
						typeText(msg);
						break;
				}
			}
		}.start();
	}

	@Override
	public void onEnter() {
		VenderItem result;
		try {
			result = (VenderItem) resultList.toArray()[resultIndex];
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//add new line
		mPreviousLines.append(mCurrentLine);
		mPreviousLines.append("\n");
		mCurrentLine = "";
		searchHelper.launch(result);
	}

	private class InitTextingThread extends Thread{
		@Override
		public void run(){
			while(showView == null);
			initializing = true;

			mHandler.sendEmptyMessage(WHAT_HIDE);

			typeTexts();
			typeDots();

			displayText("\n");
			try { sleep(30); } catch (InterruptedException e) { e.printStackTrace();}
			displayText("\n");

			initializing = false;
			mHandler.sendEmptyMessage(WHAT_START);
		}

		private void typeTexts(){
			for (String str: INIT_TEXT) {
				Message message = new Message();
				message.what = WHAT_INPUT;
				message.obj = "\n";
				mHandler.sendMessage(message);
				typeText(str);
				try {
					sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void typeDots(){
			for(int k=0; k<3; k++){
				displayText(".");
				try {
					sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class TickThread extends Thread{
		@Override
		public void run(){
			int i = 0;
			while(isRun){
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(++i%2);
			}
		}
	}

	private void replaceLine(String line){
		mCurrentLine = line;
		showView.setText(mPreviousLines.toString() + mCurrentLine);
	}

	private void replaceLine(boolean ignoreMatch){
		if(initializing){
			return;
		}
		String value = input_search.getText().toString();
		Log.d(TAG, "value:"+value +"," + mInputText);
		if(mInputText.equals(value)){
			if(!ignoreMatch){
				return;
			}
		}
		mInputText = value;

		VenderItem result = null;
		try {
			result = (VenderItem) resultList.toArray()[resultIndex];
		} catch (Exception e) {
			e.printStackTrace();
		}
		String line = "";
		if(result == null){
			line = "null -"+value;
		}else{
			line += result.getDisplayName();
			line += " -" + value;
			Log.v(TAG, "input:" + result.getValue());
			Log.v(TAG, "display:"+line);
		}

		mCurrentLine = line;
		showView.setText(mPreviousLines.toString() + mCurrentLine);
	}

	/*
	 * display on screen as if it's typed into
	 */
	private void typeText(String str){
		searchHelper.blockInput();
		for(int j=0; j<str.length();){
			int length = CommonUtil.getRandom(2, 2);
			String token;
			if(j+length >= str.length()){
				token = str.substring(j, str.length());
			}else{
				token = str.substring(j, j+length);
			}
			Message msg = new Message();
			msg.what = WHAT_INPUT;
			msg.obj = token;
			mHandler.sendMessage(msg);
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			j += length;
		}
		searchHelper.releaseInput();
	}

	private void displayText(String sth){
		Message msg = new Message();
		msg.what = WHAT_INPUT;
		msg.obj = sth;
		mHandler.sendMessage(msg);
	}

}
