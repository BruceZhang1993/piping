package shinado.indi.lib.launcher;

import android.view.ViewGroup;
import android.widget.EditText;

import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;

public interface Searchable {

	public static final int FLAG_REPLACE = 1;
	public static final int FLAG_INPUT = 2;

	public EditText shapSearchInput();
	public ViewGroup getKeyboard();
	public void onNotified(TreeSet<VenderItem> result_set);
	
	public void onEnter();
	public void onShift();
	public void onDisplay(String msg, int flag);
	
}
