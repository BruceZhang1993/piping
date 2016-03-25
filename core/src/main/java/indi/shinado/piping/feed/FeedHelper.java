package indi.shinado.piping.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.util.android.ContactManager;

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
							Pipe item = ContactManager.getInstance(context, null).getResult(dialNum);
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
