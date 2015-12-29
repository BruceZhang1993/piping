package shinado.indi.lib.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatteryStatusBar extends StatusBar{
    public BatteryStatusBar(Context context, Statusable s, int id) {
        super(context, s, id);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(mReceiver, filter);
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

        private String marker = "%";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case Intent.ACTION_BATTERY_CHANGED:
                    int level = intent.getIntExtra("level", 0);
                    int scale = intent.getIntExtra("scale", 100);
                    String msgBattery = (level * 100 / scale) + marker;
                    mStatusable.onStatusBarNotified(id, 0, msgBattery);
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    marker = "⚡";
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    marker = "%";
                    break;
            }
        }
    };
}
