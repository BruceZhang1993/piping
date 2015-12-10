package shinado.indi.lib.launcher;

public interface Feedable {

	public void onPhoneReceived(int second, String num);
	public void onMsgReceived(String num, String msg);
}
