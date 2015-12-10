package shinado.indi.lib.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import shinado.indi.lib.GlobalDefs;

public class FeedHelper {
	
	private Context context;
	private Feedable feedable;
	
    private String dialNum = null;
    private long ringingTime;
    
	private static final String SMS_REC="android.provider.Telephony.SMS_RECEIVED";
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("basic receive "+action);
			if(SMS_REC.equals(action)){
				Bundle bundle = intent.getExtras();
				if(bundle != null)
				{
					Object[] myObjPdus = (Object[]) bundle.get("pdus");
					SmsMessage[] message = new SmsMessage[myObjPdus.length];
					for(int i=0; i<myObjPdus.length; i++)
					{
						message[i] = SmsMessage.createFromPdu((byte[]) myObjPdus[i]);
					}
					for(SmsMessage currentMessage : message)
					{
						String num = currentMessage.getDisplayOriginatingAddress();
						String sms = currentMessage.getDisplayMessageBody();
						feedable.onMsgReceived(num, sms);
					}
				}
			}else if(TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)){
				TelephonyManager telephony =   
							  (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
				int state = telephony.getCallState(); 
				System.out.println("phone state:"+state);
				switch(state){  
					case TelephonyManager.CALL_STATE_RINGING: 
						ringingTime = System.currentTimeMillis();
						dialNum = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); 
						break;  
					case TelephonyManager.CALL_STATE_IDLE:
						context.sendBroadcast(new Intent(GlobalDefs.ACTION_PHONE_OFF));
						if(dialNum != null){
							int s = (int) ((System.currentTimeMillis() - ringingTime) / 1000);
							feedable.onPhoneReceived(s, dialNum);
							dialNum = null;
						}
						break;  
					case TelephonyManager.CALL_STATE_OFFHOOK:  
						context.sendBroadcast(new Intent(GlobalDefs.ACTION_PHONE_ONLINE));
						dialNum = null;
						break; 
				}
			}
		}
	};
	public FeedHelper(Context context, Feedable feedable){
		this.context = context;
		this.feedable = feedable;

		registerReceiver();
	}

    public void registerReceiver(){
    	System.out.println("register broadcast------");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMS_REC);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        context.registerReceiver(mReceiver, filter); 
    }
	
    public void destroy(){
    	context.unregisterReceiver(mReceiver);
    }
}
