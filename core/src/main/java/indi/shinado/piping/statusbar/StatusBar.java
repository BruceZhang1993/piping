package indi.shinado.piping.statusbar;

import android.content.Context;

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
