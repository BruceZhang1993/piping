package indi.shinado.piping.keyboard;

import android.content.Context;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import indi.shinado.piping.pipes.PipeSearcher;
import indi.shinado.piping.settings.Preferences;

public class SearchHelper {

    private boolean blockInput = false;
    private Preferences mPreferences;
    private Vibrator mVib;
    private TextView mInputView;
    private PipeSearcher mSearcher;

    public SearchHelper(Context context, Searchable searchable, Keyboard keyboard, PipeSearcher searcher){
        mInputView = searchable.getInputView();
        setOnKeyboardListener(keyboard.getKeyboard());
        mSearcher = searcher;
        setTextChangeListener(mInputView);

        if (context != null) {
            mVib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        mPreferences = new Preferences();
    }

    private void setTextChangeListener(TextView inputView) {
        inputView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearcher.search(s.toString().trim(), before, count);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        String key = (String) v.getTag();
        if (key.length() == 1) {
            String text = mInputView.getText().toString();
            mInputView.setText(text + key);
        } else {
            String text = mInputView.getText().toString();
            if (key.equals("backspace")) {
                //EditView must have something
                if (text.length() > 0) {
                    mInputView.setText(text.subSequence(0, text.length() - 1));
                }
            } else if (key.equals("shift")) {
                mSearcher.onKeyDown(KeyCode.KEY_SHIFT);
            } else if (key.equals("space")) {
                mInputView.setText(text + " ");
            } else if (key.equals("enter")) {
                mSearcher.onKeyDown(KeyCode.KEY_ENTER);
            }
        }
    }

}
