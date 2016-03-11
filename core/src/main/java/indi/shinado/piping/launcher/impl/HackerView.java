package indi.shinado.piping.launcher.impl;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.util.CommonUtil;

public class HackerView {

    private final String[] INIT_TEXT = {"wsp_stp_auth_build.bui",
            "Account:******",
            "Password:***************",
            "Access granted"};

    /**
     * tick_
     * tick
     * tick_
     * tick
     * ...
     */
    private boolean mTicking = false;

    /**
     * displaying initializing texts
     */


    /**
     * display text, not to be modified
     */
    private StringBuffer mPreviousLines = new StringBuffer();

    /**
     * last line of displaying text, could be modified
     * contains two parts, hint and user input
     */
    private String mCurrentLine = "";

    private boolean mBlocked = false;

    /**
     * set true on start
     */
    private boolean mSystemReady = false;

    private TextView mConsole;

    private Console mDevice;

    private OutputHandler mHandler;

    private HandlerHelper mHandlerHelper = new HandlerHelper();

    public HackerView(TextView console, Console device) {
        this.mConsole = console;
        this.mDevice = device;
        mHandler = new OutputHandler(this);
    }

    /**
     * type text in thread
     */
    public void type(final String string) {
        new Thread() {
            public void run() {
                typeText(string);
            }
        }.start();
    }

    /**
     * type text in without thread
     */
    private void typeText(final String str) {
        //wait if blocked already
        while (isBlocked()) ;
        blockInput();
        for (int j = 0; j < str.length(); ) {
            int length = CommonUtil.getRandom(2, 2);
            String token;
            if (j + length >= str.length()) {
                token = str.substring(j, str.length());
            } else {
                token = str.substring(j, j + length);
            }
            appendCurrentLine(token);
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            j += length;
        }
        appendNewLine();
        releaseInput();
    }

    /**
     * put string to the end of current line
     */
    public void appendCurrentLine(String sth) {
        mHandler.obtainMessage(OutputHandler.WHAT_APPEND_CURRENT_LINE, sth).sendToTarget();
    }

    /**
     * add current line to the previous and start a new line
     */
    public void appendNewLine() {
        mHandler.obtainMessage(OutputHandler.WHAT_APPEND_NEW_LINE).sendToTarget();
    }

    /**
     * replace current line with new line
     */
    public void replaceCurrentLine(String line) {
        mHandler.obtainMessage(OutputHandler.WHAT_REPLACE_CURRENT_LINE, line).sendToTarget();
    }

    public void blockInput() {
        mBlocked = true;
        mDevice.blockInput();
    }

    public void releaseInput() {
        mBlocked = false;
        mDevice.releaseInput();
    }

    public boolean isBlocked(){
        return mBlocked;
    }

    /**
     * doing some initializing work
     */
    public void init() {
        displayInitText();
    }

    /**
     * start to accept input
     * start ticking
     */
    public void start() {
        allSystemGo();
        startTicking();
    }

    public void stop() {
        stopTicking();
    }

    private void allSystemGo() {
        mSystemReady = true;
    }

    private void displayInitText() {
        new InitTextingThread().start();
    }

    private void startTicking() {
        mTicking = true;
        new TickThread().start();
    }

    private void stopTicking() {
        mTicking = false;
    }

    /**
     * input text into console
     * adding string to the end
     */
    public void appendText(String sth) {
        mHandler.obtainMessage(OutputHandler.WHAT_INPUT, sth).sendToTarget();
    }

    public void tik() {
        mHandler.sendEmptyMessage(OutputHandler.WHAT_TIK);
    }

    public void tok() {
        mHandler.sendEmptyMessage(OutputHandler.WHAT_TOK);
    }

    public HandlerHelper getHandlerHelper() {
        return mHandlerHelper;
    }

    public void clear() {
        mPreviousLines = new StringBuffer();
        for (String str : INIT_TEXT){
            mPreviousLines.append(str);
            mPreviousLines.append("\n");
        }
        mPreviousLines.append("\n");
        mCurrentLine = "";
        mConsole.setText(mPreviousLines.toString() + mCurrentLine);
    }

    private class HandlerHelper {

        /**
         * put string to the end of current line
         */
        private void appendCurrentLine(String sth) {
            mCurrentLine += sth;
            validate();
        }

        /**
         * add current line to the previous and start a new line
         */
        private void appendNewLine() {
            mPreviousLines.append(mCurrentLine);
            mCurrentLine = "";
            mPreviousLines.append("\n");
            validate();
        }

        private void replaceCurrentLine(String line) {
            mCurrentLine = line;
            validate();
        }

        /**
         * force message to be shown in the console
         */
        private void validate() {
            mConsole.setText(mPreviousLines.toString() + mCurrentLine);
        }

        private void tik() {
            mConsole.setText(mPreviousLines.toString() + mCurrentLine + "_");
        }

        private void tok() {
            mConsole.setText(mPreviousLines.toString() + mCurrentLine);
        }

    }

    private class TickThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            while (mTicking) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mBlocked) {
                    continue;
                }
                if (++i % 2 == 0) {
                    tik();
                } else {
                    tok();
                }
            }
        }
    }

    private class InitTextingThread extends Thread {

        @Override
        public void run() {
            while (mConsole == null) ;

            appendNewLine();
            typeInitTexts();

            blockInput();
            loading();

            appendNewLine();
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            appendNewLine();

            releaseInput();
        }

        private void typeInitTexts() {
            for (String str : INIT_TEXT) {
                typeText(str);
                try {
                    sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void loading() {
            appendCurrentLine("initializing");
            while (!mSystemReady) {
                appendCurrentLine(".");
                //clear ... if exists
                mCurrentLine = mCurrentLine.replace("...", "");
                try {
                    sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            replaceCurrentLine("initialization completed");
        }
    }


    static class OutputHandler extends Handler {

        private static final int WHAT_TIK = 1;
        private static final int WHAT_TOK = 0;
        private static final int WHAT_INPUT = 2;
        private static final int WHAT_APPEND_NEW_LINE = 5;
        private static final int WHAT_APPEND_CURRENT_LINE = 6;
        private static final int WHAT_REPLACE_CURRENT_LINE = 7;

        private WeakReference<HackerView> mRef;

        public OutputHandler(HackerView view) {
            mRef = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HackerView view = mRef.get();
            HandlerHelper helper = view.getHandlerHelper();

            switch (msg.what) {
                case WHAT_TIK:
                    helper.tik();
                    break;
                case WHAT_TOK:
                    helper.tok();
                    break;
                case WHAT_APPEND_CURRENT_LINE:
                    helper.appendCurrentLine((String) msg.obj);
                    break;
                case WHAT_APPEND_NEW_LINE:
                    helper.appendNewLine();
                    break;
                case WHAT_REPLACE_CURRENT_LINE:
                    helper.replaceCurrentLine((String) msg.obj);
                    break;
                default:
                    //do nothing
                    break;
            }
        }
    }
}
