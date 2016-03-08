package indi.shinado.piping.launcher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.settings.Preferences;

public class KeyboardIOHelper implements IOHelper{

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
    private KeyboardHandler mHandler;
    private View mKeyboard;

    /**
     * dang dang dang~ dang~dang~~dang~~~dang dang dang dang dang dang dang dang dang dang dang dang~~dangdangdangdangdangdangdang~~~
     */
    public void connect(Context context, View keyboard, ConsoleHelper helper){
        mHandler = new KeyboardHandler(this);
        if (context != null) {
            mVib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        mPreferences = new Preferences();
        mKeyboard = keyboard;

        setOnKeyboardListener((ViewGroup) keyboard);
        setSpecialKeys((ViewGroup) keyboard);
        mConsoleHelper = helper;
        mConsoleHelper.setOnHistoryListener(new ConsoleHelper.OnHistoryListener() {
            @Override
            public void onHistoryInput(String history) {
                mUserInput = history;
            }
        });
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

    @Override
    public void startInput() {
        mHandler.obtainMessage(KeyboardHandler.WHAT_START).sendToTarget();
    }

    @Override
    public void restartInput() {
        //none of my business
    }

    private void showKeyboard() {
        int TIME_SHOW = 150;
        mKeyboard.setVisibility(View.VISIBLE);
        TranslateAnimation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(TIME_SHOW);
        anim.setInterpolator(new DecelerateInterpolator(3.0f));
        mKeyboard.startAnimation(anim);
    }

    static class KeyboardHandler extends Handler {

        private static final int WHAT_START = 3;

        private WeakReference<KeyboardIOHelper> deskViewWeakReference;

        public KeyboardHandler(KeyboardIOHelper launcher) {
            deskViewWeakReference = new WeakReference<>(launcher);
        }

        @Override
        public void handleMessage(Message msg) {
            KeyboardIOHelper launcher = deskViewWeakReference.get();
            switch (msg.what) {
                case WHAT_START:
                    launcher.showKeyboard();
                    break;
            }
        }
    }

}
