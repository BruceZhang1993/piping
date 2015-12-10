package shinado.indi.lib;

import android.os.Environment;

import java.io.File;

/**
 * jar file named as:
 * PATH_HOME/your_name.jar
 */
public class GlobalDefs {

	public static final String PATH_HOME = 
			Environment.getExternalStorageDirectory().toString() + 
			File.separator + 
			"vender"+
			File.separator;

	public static final String ACTION_START = "com.shinado.desk.start";
	public static final String ACTION_STOP = "com.shinado.desk.stop";
	public static final String ACTION_PHONE_ONLINE = "com.shinado.desk.phone.online";
	public static final String ACTION_PHONE_OFF = "com.shinado.desk.phone.off";
	public static final String ACTION_LOCK = "com.shinado.desk.lock";
	public static final String ACTION_QUIT = "com.shinado.desk.quit";

	public static final String ACTION_CHANGE_THEME = "com.shinado.yi.desk.theme.change";
	public static final String ACTION_RESTART = "com.shinado.yi.desk.theme.restart";
}
