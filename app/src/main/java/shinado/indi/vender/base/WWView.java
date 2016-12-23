package shinado.indi.vender.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.widget.TextView;

import com.shinado.annotation.TargetVersion;

import java.lang.ref.WeakReference;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.settings.Preferences;
import indi.shinado.piping.util.CommonUtil;

public class WWView {

    private String[] initTexts;
    private String[] initTextsCache;

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
    private CharSequence mPreviousLines;

    /**
     * last line of displaying text, could be modified
     * contains two parts, hint and user input
     */
    private CharSequence mCurrentLine = "";

    private boolean mBlocked = false;

    /**
     * set true on start
     */
    private boolean mSystemReady = false;

    private Context mContext;

    private TextView mConsole;

    private Console mDevice;

    private OutputHandler mHandler;

    private HandlerHelper mHandlerHelper = new HandlerHelper();

    private TypeThread mTypeThread;

    public WWView(Context context, TextView console, Console device) {
        this.mConsole = console;
        this.mDevice = device;
        this.mContext = context;
        initText();
        mHandler = new OutputHandler(this);
    }

    private void initText(){
        Preferences preferences = new Preferences(mContext);
        String text = preferences.getInitText();
        initTexts = text.split("\n");
    }

    /**
     * type text in thread
     */
    public void type(Spanned string) {
        mTypeThread = new TypeThread(string);
        mTypeThread.start();
    }

    /**
     * started from version 4
     * force {@link #typeText(Spanned)}  to stop
     */
    @TargetVersion(4)
    public void forceTextToShow(){
        if (mTypeThread != null){
            mTypeThread.interrupt();
        }
    }

    /**
     * type text in UI thread
     */
    private void typeText(final Spanned str) {
        //wait if blocked already
        while (isBlocked()) ;

        blockInput();
        for (int j = 0; j < str.length(); ) {
            int length = CommonUtil.getRandom(2, 2);
            CharSequence token;
            if (j + length >= str.length()) {
                token = TextUtils.substring(str, j, str.length());
//                token = str.subSequence(j, str.length());
            } else {
                token = TextUtils.substring(str, j, j + length);
//                token = str.subSequence(j, j + length);
            }
            appendCurrentLine(token);
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                //being interrupted by forceTextToShow
                j += length;
                appendCurrentLine(TextUtils.substring(str, j, str.length()));
//                appendCurrentLine(str.subSequence(j, str.length()));
                appendNewLine();
                releaseInput();
                return;
            }
            j += length;
        }
        appendNewLine();
        releaseInput();
    }

    /**
     * put string to the end of current line
     */
    public void appendCurrentLine(CharSequence sth) {
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
    public void replaceCurrentLine(CharSequence line) {
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

    public void startTicking() {
        if (mTicking){
            return;
        }
        mTicking = true;
        new TickThread().start();
    }

    public void stopTicking() {
        mTicking = false;
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
        mPreviousLines = new SpannedString("");
        for (String str : initTexts){
            mPreviousLines = TextUtils.concat(mPreviousLines, str, "\n");
//            mPreviousLines.append(str);
//            mPreviousLines.append("\n");
        }
        mPreviousLines = TextUtils.concat(mPreviousLines, "\n");
//        mPreviousLines.append("\n");
        mCurrentLine = "";
        mConsole.setText(TextUtils.concat(mPreviousLines.toString(), mCurrentLine));
    }

    public void setInitText(String text){
        initTexts = text.split("\n");
        clear();
    }

    public void hideInitText(){
        initTextsCache = initTexts;
        initTexts = new String[]{};
        clear();
    }

    public void showInitText(){
        if (initTextsCache != null){
            initTexts = initTextsCache;
        }
        clear();
    }

    public String getLastInput(){
        String[] splits = mPreviousLines.toString().split("\n");
        return splits[splits.length-1];
    }



    private class HandlerHelper {

        /**
         * put string to the end of current line
         */
        private void appendCurrentLine(CharSequence sth) {
            mCurrentLine = TextUtils.concat(mCurrentLine, sth);
            validate();
        }

        /**
         * add current line to the previous and start a new line
         */
        private void appendNewLine() {
            mPreviousLines = TextUtils.concat(mPreviousLines, mCurrentLine);
//            mPreviousLines.append(mCurrentLine);
            mCurrentLine = "";
            mPreviousLines = TextUtils.concat(mPreviousLines, "\n");
//            mPreviousLines.append("\n");
            validate();
        }

        private void replaceCurrentLine(CharSequence line) {
            mCurrentLine = line;
            validate();
        }

        /**
         * force message to be shown in the console
         */
        private void validate() {
            tok();
        }

        private void tik() {
            mConsole.setText(TextUtils.concat(mPreviousLines, mCurrentLine, "▊"));
        }

        private void tok() {
            mConsole.setText(TextUtils.concat(mPreviousLines, mCurrentLine));
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
            for (String str : initTexts) {
                typeText(new SpannedString(str));
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
//                mCurrentLine = mCurrentLine.replace("...", "");
                try {
                    sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            replaceCurrentLine("initialization completed");
        }
    }

    class TypeThread extends Thread{

        private Spanned text;

        TypeThread(Spanned text){
            this.text = text;
        }

        public void run() {
            if (text == null){
                return;
            }
            mDevice.blockInput();
            typeText(text);
            mDevice.releaseInput();
        }
    }

    static class OutputHandler extends Handler {

        private static final int WHAT_TIK = 1;
        private static final int WHAT_TOK = 0;
        private static final int WHAT_APPEND_NEW_LINE = 5;
        private static final int WHAT_APPEND_CURRENT_LINE = 6;
        private static final int WHAT_REPLACE_CURRENT_LINE = 7;

        private WeakReference<WWView> mRef;

        public OutputHandler(WWView view) {
            mRef = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WWView view = mRef.get();
            HandlerHelper helper = view.getHandlerHelper();

            switch (msg.what) {
                case WHAT_TIK:
                    helper.tik();
                    break;
                case WHAT_TOK:
                    helper.tok();
                    break;
                case WHAT_APPEND_CURRENT_LINE:
                    helper.appendCurrentLine((CharSequence) msg.obj);
                    break;
                case WHAT_APPEND_NEW_LINE:
                    helper.appendNewLine();
                    break;
                case WHAT_REPLACE_CURRENT_LINE:
                    helper.replaceCurrentLine((CharSequence) msg.obj);
                    break;
                default:
                    //do nothing
                    break;
            }
        }
    }
}
