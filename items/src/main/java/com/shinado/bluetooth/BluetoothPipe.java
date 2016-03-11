package com.shinado.bluetooth;

import android.bluetooth.BluetoothAdapter;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class BluetoothPipe extends DefaultInputActionPipe{

    public BluetoothPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$Bluetooth";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"blue", "tooth"});
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
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        String output;
        boolean isEnabled = adapter.isEnabled();
        if (isEnabled){
            output = "Bluetooth is on.";
        }else{
            output = "Bluetooth is off.";
        }
        if (callback == getConsoleCallback()){
            if (isEnabled){
                output += " Turning it off...";
                adapter.disable();
            }else{
                output += " Turning it on...";
                adapter.enable();
            }
        }
        callback.onOutput(output);
    }
}
