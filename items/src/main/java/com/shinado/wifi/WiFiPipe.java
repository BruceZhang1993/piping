package com.shinado.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class WiFiPipe extends DefaultInputActionPipe{

    public WiFiPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$WiFi";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"wi", "fi"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        roll(callback);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        roll(callback);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        roll(callback);
    }

    private void roll(OutputCallback callback){
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        switch (wm.getWifiState()){
            case WifiManager.WIFI_STATE_DISABLING:
                callback.onOutput("WiFi is being disabled");
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                if (callback == getConsoleCallback()){
                    callback.onOutput("WiFi is off, enabling it...");
                    wm.setWifiEnabled(true);
                }else{
                    callback.onOutput("WiFi is off.");
                }
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                callback.onOutput("WiFi is being enabled");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                if (callback == getConsoleCallback()){
                    callback.onOutput("WiFi is on, disabling it...");
                    wm.setWifiEnabled(false);
                }else{
                    callback.onOutput("WiFi is on.");
                }
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                callback.onOutput("Unknown error");
            default:
                break;
        }
    }
}
