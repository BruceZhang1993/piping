package indi.shinado.piping.statusbar;

/**
 * Created by Administrator on 2015/12/28.
 */
public interface Statusable {

    public static final int ID_BATTERY = 1;
    public static final int ID_TIME = 2;
    public static final int ID_CONNECTION = 3;

    public void onStatusBarNotified(int id, int flag, String msg);

}
