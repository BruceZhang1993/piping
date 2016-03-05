package shinado.indi.vender.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shinado.piping.lock.LockService;

import java.lang.ref.WeakReference;

import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.launcher.impl.HackerView;
import indi.shinado.piping.launcher.IOHelper;
import indi.shinado.piping.launcher.IOMethod;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;
import indi.shinado.piping.util.android.AppManager;
import shinado.indi.vender.R;

public class HackerLauncher extends BaseLauncherView implements IOMethod, DeviceConsole, Feedable {

    private ScrollView mScrollView;
    private ViewGroup mKeyboard;
    private HackerView mHackerView;

    /**
     * user input
     */
//    private String mInputText = "";

    private ConsoleHelper mConsoleHelper;

    private IOHelper mIOHelper;

    private KeyboardHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_launcher);

        mHandler = new KeyboardHandler(this);
        mIOHelper = new IOHelper(this);
        initViews();

        mConsoleHelper = new ConsoleHelper(this, this, new PipesLoader(), TranslatorFactory.getTranslator(this, 2));
        mIOHelper.connect(this, mConsoleHelper);

        setupStatusBar();
        addFeedable(this);
//        startService(new Intent(this, LockService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHackerView.stop();
//        stopService(new Intent(this, LockService.class));
    }

    private void setupStatusBar(){
        new StatusBarHelper().setupStatusBar(this);
    }

    private void initViews() {
        mScrollView = (ScrollView) this.findViewById(R.id.scrollView);
        mKeyboard = (ViewGroup) findViewById(R.id.keyboard);
        TextView console = (TextView) this.findViewById(R.id.displayText);
        mHackerView = new HackerView(console, this);
        mHackerView.init();
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

    private void replaceItem(boolean ignoreMatch, Pipe pipe) {
        String newLine = constructDisplay(mIOHelper.getCurrentUserInput(), pipe);
        mHackerView.replaceCurrentLine(newLine);
    }

    private String constructDisplay(String input, Pipe pipe) {
        String line = "";
        if (pipe != null) {
            line += pipe.getDisplayName();
        }
        line += " : " + input;
        return line;
    }

    @Override
    public void onSystemReady() {
        mHackerView.start();
        mHandler.obtainMessage(KeyboardHandler.WHAT_START).sendToTarget();
    }

    @Override
    public void displayResult(Pipe pipe) {
        replaceItem(true, pipe);
    }

    @Override
    public void displayPrevious(Pipe pipe) {
        replaceItem(true, pipe);
    }

    @Override
    public void onEnter(Pipe pipe) {
        //add new line
        mHackerView.appendNewLine();
        mIOHelper.clearInput();
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
        mHackerView.type(string);
    }

    @Override
    public void replaceCurrentLine(String line) {
        mHackerView.replaceCurrentLine(line);
    }

    @Override
    public void blockInput() {
        mIOHelper.blockInput();
    }

    @Override
    public void releaseInput() {
        mIOHelper.releaseInput();
    }

    @Override
    public ViewGroup getKeyboard() {
        return mKeyboard;
    }

    @Override
    public void onFeed(int flag, String msg, String pkg) {
        //clear input
        mIOHelper.clearInput();
        mConsoleHelper.reset();
        mConsoleHelper.forceShow(AppManager.getAppManager().getResult(pkg + ","));
    }

    static class KeyboardHandler extends Handler {

        private static final int WHAT_START = 3;

        private WeakReference<HackerLauncher> deskViewWeakReference;

        public KeyboardHandler(HackerLauncher launcher) {
            deskViewWeakReference = new WeakReference<>(launcher);
        }

        @Override
        public void handleMessage(Message msg) {
            HackerLauncher launcher = deskViewWeakReference.get();
            switch (msg.what) {
                case WHAT_START:
                    launcher.showKeyboard();
                    break;
            }
        }
    }

}
