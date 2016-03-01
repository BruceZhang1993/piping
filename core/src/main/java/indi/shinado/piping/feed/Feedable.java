package indi.shinado.piping.feed;

public interface Feedable {

	public static final int FLAG_PHONE = 1;
	public static final int FLAG_NORM  = 100;

	public void onFeed(int flag, String msg, String pkg);
}
