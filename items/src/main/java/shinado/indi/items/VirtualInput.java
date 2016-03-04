package shinado.indi.items;

import indi.shinado.piping.launcher.impl.ConsoleHelper;

public class VirtualInput {

    public static final String KEY_SPACE = "space";
    public static final String KEY_BACKSPACE = "backspace";
    public static final String KEY_ENTER = "enter";
    public static final String KEY_SHIFT = "shift";

    private String mCurrentInput = "";

    private ConsoleHelper helper;

    public VirtualInput(ConsoleHelper helper){
        this.helper = helper;
    }

    public void inputString(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            pressKey("" + c);
        }
    }

    public void clear() {
        mCurrentInput = "";
    }

    public void pressKey(String key) {
        if (key.length() == 1) {
            input(key);
        } else {
            if (key.equals(KEY_BACKSPACE)) {
                //EditView must have something
                if (mCurrentInput.length() > 0) {
                    mCurrentInput = mCurrentInput.substring(0, mCurrentInput.length() - 1);
                }
            } else if (key.equals(KEY_SHIFT)) {
                helper.onShift();
            } else if (key.equals(KEY_SPACE)) {
                input(key);
            } else if (key.equals(KEY_ENTER)) {
                helper.onEnter();
                clear();
            }
        }
    }

    public void input(String key) {
        int before = mCurrentInput.length();
        mCurrentInput += key;
        helper.onUserInput(mCurrentInput, before, mCurrentInput.length());
    }

}
