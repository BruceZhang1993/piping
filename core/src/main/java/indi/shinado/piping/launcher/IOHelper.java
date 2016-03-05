package indi.shinado.piping.launcher;

import android.content.Context;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.settings.Preferences;

public class IOHelper {

    public static final String KEY_SPACE = "space";
    public static final String KEY_BACKSPACE = "backspace";
    public static final String KEY_ENTER = "enter";
    public static final String KEY_SHIFT = "shift";
    public static final String KEY_PARAMS = "param";
    public static final String KEY_PIPE = "pipe";


    private boolean blockInput = false;
    private Preferences mPreferences;
    private Vibrator mVib;
    private String mUserInput = "";
    private ConsoleHelper mConsoleHelper;

    public IOHelper(Context context){
        if (context != null) {
            mVib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        mPreferences = new Preferences();
    }

    /**
     * dang dang dang~ dang~dang~~dang~~~dang dang dang dang dang dang dang dang dang dang dang dang~~dangdangdangdangdangdangdang~~~
     */
    public void connect(IOMethod method, ConsoleHelper helper){
        ViewGroup keyboard = method.getKeyboard();
        setOnKeyboardListener(keyboard);
        setSpecialKeys(keyboard);
        mConsoleHelper = helper;
    }

    public void blockInput(){
        this.blockInput = true;
    }

    public void releaseInput(){
        this.blockInput = false;
    }

    private void setOnKeyboardListener(ViewGroup root) {
        if (root == null) {
            return;
        }
        int count = root.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = root.getChildAt(i);
            if (v instanceof ViewGroup) {
                setOnKeyboardListener((ViewGroup) v);
            } else {
                v.setOnClickListener(onKeyboardListener);
            }
        }
    }

    private void setSpecialKeys(ViewGroup root){
        TextView paramTv = (TextView) root.findViewWithTag(KEY_PARAMS);
        paramTv.setText(Keys.PARAMS);
        TextView pipeTv = (TextView) root.findViewWithTag(KEY_PIPE);
        pipeTv.setText(Keys.PIPE);
    }

    private View.OnClickListener onKeyboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pressKey(v);
        }
    };

    private void pressKey(View v) {
        if (blockInput) {
            return;
        }
        if (mPreferences.isVibrating()) {
            mVib.vibrate(5);
        }
        int before = mUserInput.length();
        String key = (String) v.getTag();
        if (key.length() == 1) {
            mUserInput += key;
        } else {
            switch (key){
                case KEY_BACKSPACE:
                    if (mUserInput.length() > 0) {
                        mUserInput = mUserInput.substring(0, mUserInput.length() - 1);
                    }
                    break;
                case KEY_SPACE:
                    mUserInput += "";
                    break;
                case KEY_PARAMS:
                    mUserInput += Keys.PARAMS;
                    break;
                case KEY_PIPE:
                    mUserInput += Keys.PIPE;
                    break;
                case KEY_ENTER:
                    mConsoleHelper.onEnter();
                    break;
                case KEY_SHIFT:
                    mConsoleHelper.onShift();
                    break;
            }
        }

        mConsoleHelper.onUserInput(mUserInput, before, mUserInput.length());
    }

    public void clearInput(){
        mUserInput = "";
    }

    public String getCurrentUserInput(){
        return mUserInput;
    }
}
