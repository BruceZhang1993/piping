package shinado.indi.vender.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.ConsoleHelper;
import indi.shinado.piping.launcher.DeviceConsole;
import indi.shinado.piping.launcher.IOHelper;
import indi.shinado.piping.launcher.IOMethod;
import indi.shinado.piping.pipes.PipesLoader;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;
import indi.shinado.piping.statusbar.StatusBar;
import indi.shinado.piping.statusbar.Statusable;
import indi.shinado.piping.util.CommonUtil;
import indi.shinado.piping.util.android.AppManager;
import shinado.indi.vender.R;

public class HackerLauncher extends BaseLauncherView implements IOMethod, DeviceConsole, Statusable, Feedable {

    private TextView mInputView;
    private TextView mConsole;
    private ScrollView mScrollView;
    private ViewGroup mKeyboard;

    /**
     * display text, not to be modified
     */
    private StringBuffer mPreviousLines = new StringBuffer();

    /**
     * last line of displaying text, could be modified
     * contains two parts, hint and user input
     */
    private String mCurrentLine = "";

    /**
     * user input
     */
    private String mInputText = "";

    private ConsoleHelper mConsoleHelper;

    private OutputHandler mHandler;

    private HashMap<Integer, View> mStatusBarMap = new HashMap<>();
    private LinearLayout mStatusBarLayout;

    private boolean initializing = true;
    private boolean isRunning = true;
    private boolean isSystemReady = false;

    private final String[] INIT_TEXT = {"wsp_stp_auth_build.bui",
            "Account:******",
            "Password:***************",
            "Access granted",
            "initializing"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_launcher);

        mHandler = new OutputHandler(this);
        initViews();
        setupStatusBar();
        addFeedable(this);

        IOHelper helper = new IOHelper(this);
        mConsoleHelper = new ConsoleHelper(this, this, new PipesLoader(), TranslatorFactory.getTranslator(this, 2));
        helper.connect(this, mConsoleHelper);

        initText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    //clean text and start initiate text animation
    private void initText() {
        mPreviousLines = new StringBuffer();
        mInputView.setText("");
        new InitTextingThread().start();
    }

    private void initViews() {
        mScrollView = (ScrollView) this.findViewById(R.id.scrollView);
        mKeyboard = (ViewGroup) findViewById(R.id.keyboard);
        mConsole = (TextView) this.findViewById(R.id.displayText);
        mInputView = new TextView(this, null);
        startTicking();
    }

    private void startTicking() {
        new TickThread().start();
    }

    private void setupStatusBar(){
        ArrayList<StatusBar> mStatusBars = addStatusable(this);
        mStatusBarMap.put(Statusable.ID_TIME, findViewById(R.id.status_time_tv));

        mStatusBarLayout = (LinearLayout) findViewById(R.id.status_right_ll);
        mStatusBarLayout.removeAllViews();

        for (StatusBar sb : mStatusBars){
            sb.register();
            if (sb.id == Statusable.ID_TIME){
                continue;
            }

            //add view and put in map
            int[] flags = sb.getFlags();
            View view;
            if (sb.getFlags() != null){
                view = addStatusBarView(mStatusBarLayout, flags);
            }else {
                view = addStatusBarView(mStatusBarLayout, new int[]{sb.id});
            }
            mStatusBarMap.put(sb.id, view);
        }

    }

    private View addStatusBarView(ViewGroup parent, int[] keys){
        if (keys.length > 1){
            LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_connection, parent, false);
            for (int key : keys){
                addStatusBarView(layout, new int[]{key});
            }
            parent.addView(layout);
            return layout;
        }else {
            View view = LayoutInflater.from(this).inflate(R.layout.text_status_bar, parent, false);
            view.setTag(keys[0]);
            parent.addView(view);
            return view;
        }
    }
    /**
     * replace the current line displayed
     * @param line
     */
    private void replaceLine(String line) {
        mCurrentLine = line;
        mConsole.setText(mPreviousLines.toString() + mCurrentLine);
    }

    private void replaceLineInUIThread(String line) {
        mHandler.obtainMessage(OutputHandler.WHAT_LINE, line).sendToTarget();
    }

    private void displayNewPipe(Pipe pipe) {
        String value = mInputView.getText().toString();
        mInputText = value;
        String newLine = constructDisplay(value, pipe);
        replaceLineInUIThread(newLine);
    }

    private void hideKeyboard() {
        mKeyboard.setVisibility(View.GONE);
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

    @Override
    public void onSystemReady() {
        isSystemReady = true;
    }

    @Override
    public void displayResult(Pipe pipe) {
        displayNewPipe(pipe);
    }

    @Override
    public void displayPrevious(Pipe pipe) {
        displayNewPipe(pipe);
    }

    @Override
    public void onEnter(Pipe pipe) {
        //add new line
        mPreviousLines.append(mCurrentLine);
        mPreviousLines.append("\n");
        mCurrentLine = "";
        mInputView.setText("");
    }

    @Override
    public void onShift(Pipe pipe) {
        replaceItem(true, pipe);
    }

    @Override
    public void onNothing() {
        replaceItem(true, null);
    }

    @Override
    public void input(String string) {
        typeText(string);
    }

    @Override
    public void blockInput() {

    }

    @Override
    public void releaseInput() {

    }

    @Override
    public TextView getInputView() {
        return mInputView;
    }

    @Override
    public ViewGroup getKeyboard() {
        return mKeyboard;
    }

    @Override
    public void onStatusBarNotified(int id, int flag, String msg) {
        View view = mStatusBarMap.get(id);
        TextView textView = null;
        if (view instanceof TextView){
            textView = (TextView) view;
        } else if (view instanceof ViewGroup){
            textView = (TextView) view.findViewWithTag(flag);
        }
        textView.setText(msg);
    }

    private void displayText(String sth) {
        Message msg = new Message();
        msg.what = OutputHandler.WHAT_INPUT;
        msg.obj = sth;
        mHandler.sendMessage(msg);
    }

    private void replaceItem(boolean ignoreMatch, Pipe pipe) {
        if (initializing) {
            return;
        }
        String value = mInputView.getText().toString();
        if (mInputText.equals(value)) {
            if (!ignoreMatch) {
                return;
            }
        }
        mInputText = value;

        mCurrentLine = constructDisplay(value, pipe);
        mConsole.setText(mPreviousLines.toString() + mCurrentLine);
    }

    private String constructDisplay(String input, Pipe pipe) {
        String line = "";
        if (pipe != null) {
            line += pipe.getDisplayName();
        }
        line += " : " + input;
        return line;
    }

    /*
     * display on screen as if it's typed into
     */
    private void typeText(String str) {
        blockInput();
        for (int j = 0; j < str.length(); ) {
            int length = CommonUtil.getRandom(2, 2);
            String token;
            if (j + length >= str.length()) {
                token = str.substring(j, str.length());
            } else {
                token = str.substring(j, j + length);
            }
            Message msg = new Message();
            msg.what = OutputHandler.WHAT_INPUT;
            msg.obj = token;
            mHandler.sendMessage(msg);
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            j += length;
        }
        releaseInput();
    }

	@Override
	public void onFeed(int flag, String msg, String pkg) {
        //clear input
        mInputView.setText("");
        mInputText = "";
        mConsoleHelper.reset();
        mConsoleHelper.forceShow(AppManager.getAppManager().getResult(pkg + ","));
	}

    private class InitTextingThread extends Thread {
        @Override
        public void run() {
            while (mConsole == null) ;
            initializing = true;

            mHandler.sendEmptyMessage(OutputHandler.WHAT_HIDE);

            typeTexts();
//            typeDots();
            loading();

            displayText("\n");
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            displayText("\n");

            initializing = false;
            mHandler.sendEmptyMessage(OutputHandler.WHAT_START);
        }

        private void typeTexts() {
            for (String str : INIT_TEXT) {
                Message message = new Message();
                message.what = OutputHandler.WHAT_INPUT;
                message.obj = "\n";
                mHandler.sendMessage(message);
                typeText(str);
                try {
                    sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void typeDots() {
            for (int k = 0; k < 3; k++) {
                displayText(".");
                try {
                    sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void loading() {
            while (!isSystemReady) {
                int length = mPreviousLines.toString().length();
                if (mPreviousLines.subSequence(length - 3, length).toString().equals("...")) {
                    mPreviousLines.delete(length - 3, length);
                }
                displayText(".");
                try {
                    sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            displayText("...");
        }
    }

    private class TickThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            while (isRunning) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (initializing) {
                    continue;
                }
                mHandler.sendEmptyMessage(++i % 2);
            }
        }
    }


    static class OutputHandler extends Handler {

        private static final int WHAT_TIK = 1;
        private static final int WHAT_TOK = 0;
        private static final int WHAT_INPUT = 2;
        private static final int WHAT_START = 3;
        private static final int WHAT_HIDE = 4;
        private static final int WHAT_LINE = 5;

        private WeakReference<HackerLauncher> deskViewWeakReference;

        public OutputHandler(HackerLauncher launcher) {
            deskViewWeakReference = new WeakReference<>(launcher);
        }

        @Override
        public void handleMessage(Message msg) {
            HackerLauncher launcher = deskViewWeakReference.get();
            switch (msg.what) {
                case WHAT_TIK:
                    launcher.mConsole.setText(launcher.mPreviousLines.toString() + launcher.mCurrentLine + "_");
                    break;
                case WHAT_TOK:
                    launcher.mConsole.setText(launcher.mPreviousLines.toString() + launcher.mCurrentLine);
                    break;
                case WHAT_INPUT:
                    launcher.mPreviousLines.append(msg.obj);
                    launcher.mConsole.setText(launcher.mPreviousLines.toString());
                    launcher.mScrollView.fullScroll(View.FOCUS_DOWN);
                    break;
                case WHAT_START:
                    launcher.mInputView.setText("");
                    launcher.showKeyboard();
                    break;
                case WHAT_HIDE:
                    launcher.hideKeyboard();
                    break;
                case WHAT_LINE:
                    launcher.replaceLine((String) msg.obj);
                    break;
            }
        }
    }

}
