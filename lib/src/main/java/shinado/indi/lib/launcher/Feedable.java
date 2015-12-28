package shinado.indi.lib.launcher;

public interface Feedable {

	public static final int FLAG_PHONE = 1;
	public static final int FLAG_NORM  = 100;

	public void onFeed(int flag, String msg, String pkg);
}
