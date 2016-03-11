package shinado.indi.items;

import android.util.Log;

import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.entity.Pipe;

public class SystemConsole  implements DeviceConsole {

    public static final String TAG = "SystemConsole";

    @Override
    public void input(String string) {
        log("console input:" + string);
    }

    @Override
    public void replaceCurrentLine(String line) {
        log("replace:" + line);
    }

    @Override
    public void blockInput() {
        log("block input");
    }

    @Override
    public void releaseInput() {
        log("release input");
    }

    @Override
    public void clear() {
        log("clear");
    }

    @Override
    public void onSystemReady() {
        log("onSystemReady");
    }

    @Override
    public void displayResult(Pipe pipe) {
        log("display result:" + pipe.getDisplayName());
    }

    @Override
    public void displayPrevious(Pipe pipe) {
        log("display previous:" + pipe.getDisplayName());
    }

    @Override
    public void onEnter(Pipe pipe) {
        log("enter:" + pipe.getDisplayName());
    }

    @Override
    public void onShift(Pipe pipe) {
        log("shift:" + pipe.getDisplayName());
    }

    @Override
    public void onNothing() {
        log("nothing to show");
    }

    public void log(String msg){
        Log.d(TAG, msg);
    }
}
