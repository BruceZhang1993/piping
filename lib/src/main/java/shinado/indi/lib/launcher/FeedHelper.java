package shinado.indi.lib.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.Calendar;

import shinado.indi.lib.GlobalDefs;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.statusbar.BarService;
import shinado.indi.lib.statusbar.BatteryStatusBar;
import shinado.indi.lib.statusbar.ConnectionStatusBar;
import shinado.indi.lib.statusbar.StatusBar;
import shinado.indi.lib.statusbar.TimeStatusBar;
import shinado.indi.lib.util.ContactManager;
import shinado.indi.lib.util.TimeUtil;

public class FeedHelper {

	private Context context;
	private Feedable feedable;
	
    private String dialNum = null;
    private long ringingTime;

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)){
				TelephonyManager telephony =   
							  (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
				int state = telephony.getCallState(); 
				switch(state){
					case TelephonyManager.CALL_STATE_RINGING: 
						ringingTime = System.currentTimeMillis();
						dialNum = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); 
						break;  
					case TelephonyManager.CALL_STATE_IDLE:
						context.sendBroadcast(new Intent(GlobalDefs.ACTION_PHONE_OFF));
						if(dialNum != null){
							int s = (int) ((System.currentTimeMillis() - ringingTime) / 1000);
							VenderItem item = ContactManager.getContactManager(context, null).getResult(dialNum);
							String msg = "Incoming call from " + item.getDisplayName() + ", lasting " + s;
							feedable.onFeed(Feedable.FLAG_PHONE, msg, null);
							dialNum = null;
						}
						break;  
					case TelephonyManager.CALL_STATE_OFFHOOK:  
						context.sendBroadcast(new Intent(GlobalDefs.ACTION_PHONE_ONLINE));
						dialNum = null;
						break; 
				}
			}else if (action.equals(BarService.Constants.ACTION_CATCH_NOTIFICATION)){
				String pkg = intent.getStringExtra(BarService.Constants.EXTRA_PACKAGE);
				String msg = intent.getStringExtra(BarService.Constants.EXTRA_MESSAGE);
				feedable.onFeed(Feedable.FLAG_NORM, msg, pkg);
			}
		}
	};

	public FeedHelper(Context context, Feedable feedable){
		this.context = context;
		this.feedable = feedable;

	}

    public void start(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction(BarService.Constants.ACTION_CATCH_NOTIFICATION);
		context.registerReceiver(mReceiver, filter);

    }


	public void destroy(){
    	context.unregisterReceiver(mReceiver);
    }
}
