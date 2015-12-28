package shinado.indi.lib.statusbar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import shinado.indi.lib.launcher.Feedable;
import shinado.indi.lib.util.TimeUtil;

/**
 * Created by Administrator on 2015/12/28.
 */
public class TimeStatusBar extends StatusBar{

    private boolean running;

    public TimeStatusBar(Context context, Statusable s, int id) {
        super(context, s, id);
        mHandler = new TimeHandler(this);
    }

    private TimeHandler mHandler;

    private static class TimeHandler extends Handler{

        private WeakReference<TimeStatusBar> reference;

        public TimeHandler(TimeStatusBar statusBar){
            reference = new WeakReference<TimeStatusBar>(statusBar);
        }

        @Override
        public void handleMessage(Message msg) {
            TimeStatusBar sb = reference.get();
            sb.mStatusable.onStatusBarNotified(sb.id, 0, TimeUtil.getTime()[0]);
        }

    }

    private class TimeThread extends Thread{

        @Override
        public void run() {
            while (running){
                mHandler.obtainMessage().sendToTarget();
                try {
                    sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int[] getFlags() {
        return null;
    }

    @Override
    public void register() {
        running = true;
        new TimeThread().start();
    }

    @Override
    public void unregister() {
        running = false;
    }
}
