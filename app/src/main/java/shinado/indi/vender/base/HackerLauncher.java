package shinado.indi.vender.base;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.shinado.piping.geek.HeadLoadder;
import com.shinado.piping.geek.header.IHeadView;

import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.IOHelper;
import indi.shinado.piping.launcher.IOHelperFactory;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.UserInputCallback;
import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.launcher.impl.HackerView;
import indi.shinado.piping.pipes.ConsoleInfo;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;
import indi.shinado.piping.settings.ConsoleAnimation;
import indi.shinado.piping.settings.Preferences;
import indi.shinado.piping.view.AnimationTextView;
import indi.shinado.piping.view.BoundaryView;
import shinado.indi.vender.R;

public class HackerLauncher extends BaseLauncherView implements DeviceConsole, Feedable {

    private ScrollView mScrollView;
    private HackerView mHackerView;
    private ViewGroup wallpaper;
    private BoundaryView boundaryView;
    private AnimationTextView consoleTextView;
    private int mConsoleWidth;
    private boolean mInputBlock = false;

    private ConsoleHelper mConsoleHelper;

    private KeyDownCallback mKeyDownCallback;
    private boolean waitingForKeyDown;

    private Preferences preferences;
    private int KEY_SHIFT;

    private IOHelper mIOHelper;

    private IHeadView mHeadView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_launcher);

        preferences = new Preferences(this);
        reloadKey();

        mIOHelper = new IOHelperFactory().getInstance();
        initViews();

        mConsoleHelper = new ConsoleHelper(this, this, new PipesLoader(), TranslatorFactory.getTranslator(this));
        mIOHelper.connect(this, /*findViewById(R.id.keyboard)*/ findViewById(R.id.input), mConsoleHelper);

        setupStatusBar();
        addFeedable(this);
        setTextColor(wallpaper, mPref.getColor());
        setTextSize(wallpaper, mPref.getTextSize());
        setBoundaryColor(mPref.getColor());

    }

    private void reloadKey(){
        KEY_SHIFT = preferences.getShiftKey();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHackerView.stop();
        mConsoleHelper.destroy();
        if (mHeadView != null){
            mHeadView.onDestroy();
        }
    }

    private void setupStatusBar() {
        new StatusBarHelper().setupStatusBar(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mHeadView != null){
            mHeadView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHeadView != null){
            mHeadView.onPause();
        }
    }

    private void initHeadView(){
        mHeadView = HeadLoadder.load(this);
        if (mHeadView != null){
            FrameLayout headLayout = (FrameLayout) findViewById(R.id.head_fl);
            headLayout.addView(mHeadView.getView(this, headLayout));
            mHeadView.onCreate();
        }
    }

    private void initViews() {
        wallpaper = (ViewGroup) this.findViewById(R.id.background);
        boundaryView = (BoundaryView) this.findViewById(R.id.boundary);
        initWallpaper();
        mScrollView = (ScrollView) this.findViewById(R.id.scrollView);
        consoleTextView = (AnimationTextView) this.findViewById(R.id.displayText);

        consoleTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectWallpaper();
                return true;
            }
        });
        consoleTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mConsoleWidth = getConsoleWidth();
            }
        });
        mHackerView = new HackerView(this, consoleTextView, this);
        mHackerView.init();

        initHeadView();
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

    private int getConsoleWidth() {
        String space = "█";
        String text = "";
        float textWidth = 0;
        while (textWidth < consoleTextView.getMeasuredWidth()) {
            text += space;
            textWidth = consoleTextView.getPaint().measureText(text);
        }
        return text.length();
    }

    @Override
    public void onSystemReady() {
        mHackerView.start();
        mIOHelper.startInput();

        ConsoleAnimation animation = ConsoleAnimation.get();
        setAnimation(animation);
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
    public void clear() {
        mHackerView.clear();
        mIOHelper.clearInput();
    }

    @Override
    public void intercept() {
        releaseInput();
        mConsoleHelper.enableSearch();
        mHackerView.forceTextToShow();
    }

    @Override
    public String getLastInput() {
        return mHackerView.getLastInput();
    }

    @Override
    public void waitForUserInput(UserInputCallback inputCallback) {
        mConsoleHelper.waitForUserInput(inputCallback);
    }

    @Override
    public void waitForKeyDown(KeyDownCallback inputCallback) {
        mKeyDownCallback = inputCallback;
        waitingForKeyDown = true;
    }

    @Override
    public void display(String string) {
        mHackerView.appendCurrentLine(string);
        mHackerView.appendNewLine();
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
    public ConsoleInfo getConsoleInfo() {
        return new ConsoleInfo(mConsoleWidth, /**TODO**/consoleTextView.getLineCount());
    }

    @Override
    public void occupyMode() {
        mHackerView.stopTicking();
        mConsoleHelper.occupyMode();
    }

    @Override
    public void quitOccupy() {
        mHackerView.startTicking();
        mConsoleHelper.quitOccupy();
    }

    @Override
    public void hideInitText() {
        mHackerView.hideInitText();
    }

    @Override
    public void showInitText() {
        mHackerView.showInitText();
    }

    @Override
    public void blindMode() {
        mHackerView.stopTicking();
        mConsoleHelper.blindMode();
    }

    @Override
    public void quitBlind() {
        mHackerView.startTicking();
        mConsoleHelper.quitBlind();
        mIOHelper.clearInput();
    }

    @Override
    public void notifyUI() {
        if (mHeadView != null){
            mHeadView.notifyUI();
        }
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
        mInputBlock = true;
        Log.d("Hacker", "blockInput:" + mInputBlock);
    }

    @Override
    public void releaseInput() {
        mIOHelper.releaseInput();
        mInputBlock = false;
        Log.d("Hacker", "releaseInput:" + mInputBlock);
    }

    @Override
    public void onFeed(int flag, String msg, String value) {
        //clear input
        mIOHelper.clearInput();
        mConsoleHelper.reset();
        mConsoleHelper.forceShow(value, msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (waitingForKeyDown){
            if (mKeyDownCallback != null){
                mKeyDownCallback.onKeyDown(keyCode);
            }
            reloadKey();
            waitingForKeyDown = false;
            return true;
        }

        if (keyCode == KEY_SHIFT){
            mConsoleHelper.onShift();
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mInputBlock) {
                    mConsoleHelper.intercept();
                } else {
                    mConsoleHelper.onUpArrow();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public ViewGroup getBackgroundView() {
        return wallpaper;
    }

    @Override
    public BoundaryView getBoundaryView() {
        return boundaryView;
    }

    @Override
    public void setInitText(String text) {
        mHackerView.setInitText(text);
    }

    @Override
    public void setConsoleAnimation(ConsoleAnimation animation) {

    }

    @Override
    public void setAnimation(ConsoleAnimation animation) {
//        consoleTextView.setConsoleAnimation(animation);
    }

    @Override
    public void resume(boolean flag) {
        if (!flag) {
            mIOHelper.restartInput();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIOHelper.requestLayout();
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 800);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
