package shinado.indi.lib.statusbar;

import android.content.Context;

import shinado.indi.lib.launcher.Feedable;

/**
 * Created by Administrator on 2015/12/28.
 */
public abstract class StatusBar {

    protected Statusable mStatusable;
    protected Context mContext;
    public int id;

    public StatusBar(Context context, Statusable statusable, int id){
        this.mStatusable = statusable;
        this.mContext = context;
        this.id = id;
    }

    public abstract void register();
    public abstract void unregister();
    public abstract int[] getFlags();

}
