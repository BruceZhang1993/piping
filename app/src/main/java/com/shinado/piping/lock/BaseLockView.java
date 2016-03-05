package com.shinado.piping.lock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public abstract class BaseLockView extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			return true;
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean idle = false;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		IntentFilter onfilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(screenon, onfilter);
		IntentFilter offfilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenoff, offfilter);
	}

	public abstract void screenOn();

	public abstract void screenOff();

	BroadcastReceiver screenon = new BroadcastReceiver() {

		public static final String Screenon = "android.intent.action.SCREEN_ON";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(Screenon))
				return;
			screenOn();
		}
	};

	BroadcastReceiver screenoff = new BroadcastReceiver() {

		public static final String TAG = "screenoff";
		public static final String Screenoff = "android.intent.action.SCREEN_OFF";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(Screenoff))
				return;
			screenOff();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenon);
		unregisterReceiver(screenoff);
		Log.v("destroy Guard", "Destroying");
	}

}
