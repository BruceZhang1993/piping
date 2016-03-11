package indi.shinado.piping.launcher;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import indi.shinado.piping.launcher.impl.ConsoleHelper;

public class InputMethodIOHelper implements IOHelper{

    private InputMethodManager mInputMethodManager;
    private boolean blockInput = false;
    private EditText mInputTextView;
    private Context mContext;

    @Override
    public void connect(Context context, View view, final ConsoleHelper helper) {
        mInputTextView = (EditText) view;
        this.mContext = context;
        mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                helper.onUserInput(s.toString(), before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mInputTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    helper.onEnter();
                    handled = true;
                }
                return handled;
            }
        });

        helper.setOnHistoryListener(new ConsoleHelper.OnHistoryListener() {
            @Override
            public void onHistoryInput(String history) {
                mInputTextView.setText(history);
                mInputTextView.setSelection(mInputTextView.getText().toString().length());
            }
        });

    }

    public void blockInput(){
        this.blockInput = true;
    }

    public void releaseInput(){
        this.blockInput = false;
    }

    @Override
    public void clearInput() {
        mInputTextView.setText("");
    }

    @Override
    public String getCurrentUserInput() {
        return mInputTextView.getText().toString();
    }

    @Override
    public void startInput() {
        //wait till initiated
        while (mInputMethodManager == null);
        mInputMethodManager.showSoftInput(mInputTextView, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void restartInput() {
        startInput();
    }

}
