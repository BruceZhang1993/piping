package shinado.indi.lib.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import shinado.indi.lib.launcher.Feedable;

/**
 * Created by Administrator on 2015/12/28.
 */
public class BatteryStatusBar extends StatusBar{
    public BatteryStatusBar(Context context, Statusable s, int id) {
        super(context, s, id);
    }

    @Override
    public void register() {
        mContext.registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void unregister() {
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public int[] getFlags() {
        return null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
             if (action.equals(Intent.ACTION_BATTERY_CHANGED)){
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				String msgBattery = (level * 100 / scale) + "";
				mStatusable.onStatusBarNotified(id, 0, msgBattery);
			}
        }
    };
}
