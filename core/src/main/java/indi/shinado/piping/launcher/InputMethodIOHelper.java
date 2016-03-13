package indi.shinado.piping.launcher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import indi.shinado.piping.launcher.impl.ConsoleHelper;

public class InputMethodIOHelper implements IOHelper{

    private InputMethodManager mInputMethodManager;
    private EditText mInputTextView;
    private Context mContext;
    private InputTextHandler mHandler;
    private boolean mBlockInput = false;
    private HandlerHelper mHandlerHelper = new HandlerHelper();

    @Override
    public void connect(Context context, View view, final ConsoleHelper helper) {
        mInputTextView = (EditText) view;
        this.mContext = context;
        mHandler = new InputTextHandler(this);
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
                if (mBlockInput){
                    return true;
                }
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    helper.onEnter();
                    return true;
                }
                return false;
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

    private HandlerHelper getHandlerHelper(){
        return mHandlerHelper;
    }

    public void blockInput(){
        mBlockInput = true;
        if (mHandler == null){
            return;
        }
        mHandler.obtainMessage(InputTextHandler.WHAT_BLOCK).sendToTarget();
    }

    public void releaseInput(){
        mBlockInput = false;
        if (mHandler == null){
            return;
        }
        mHandler.obtainMessage(InputTextHandler.WHAT_RELEASE).sendToTarget();
    }

    private class HandlerHelper{

        private void blockInput(){
            if (mInputTextView != null){
                mInputTextView.setEnabled(false);
            }
        }

        private void releaseInput(){
            if (mInputTextView != null){
                mInputTextView.setEnabled(true);
            }
        }

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

    static class InputTextHandler extends Handler{

        static final int WHAT_BLOCK = 1;
        static final int WHAT_RELEASE = 2;

        private WeakReference<InputMethodIOHelper> ref;

        public InputTextHandler(InputMethodIOHelper helper) {
            ref = new WeakReference<>(helper);
        }

        @Override
        public void handleMessage(Message msg) {
            HandlerHelper helper = ref.get().getHandlerHelper();
            switch (msg.what){
                case WHAT_BLOCK:
                    helper.blockInput();
                    break;
                case WHAT_RELEASE:
                    helper.releaseInput();
                    break;
            }
        }
    }
}
