package indi.shinado.piping.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionStatusBar extends StatusBar{

    public static final int FLAG_WIFI = 2;
    public static final int FLAG_NETWORK = 3;
    public static final int FLAG_BLUETOOTH = 4;
    public static final int FLAG_AIRPLANE = 5;
    public ConnectionStatusBar(Context context, Statusable s, int id) {
        super(context, s, id);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void unregister() {
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public int[] getFlags() {
        return new int[]{FLAG_AIRPLANE, FLAG_BLUETOOTH, FLAG_NETWORK, FLAG_WIFI};
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
                if (activeNetwork != null) { // connected to the internet
                    int type = activeNetwork.getType();
                    boolean connected = activeNetwork.isConnectedOrConnecting();
                    switch (type){
                        case ConnectivityManager.TYPE_WIFI:
                            mStatusable.onStatusBarNotified(id, FLAG_WIFI, connected ? "w" : "");
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            mStatusable.onStatusBarNotified(id, FLAG_NETWORK, connected ? "m" : "");
                            break;
                        case ConnectivityManager.TYPE_BLUETOOTH:
                            mStatusable.onStatusBarNotified(id, FLAG_BLUETOOTH, connected ? "b" : "");
                            break;
                    }
                }
            }else if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
                boolean noConnectivity =
                        intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                mStatusable.onStatusBarNotified(id, FLAG_AIRPLANE, noConnectivity ? "✈" : "");
            }
        }
    };
}
