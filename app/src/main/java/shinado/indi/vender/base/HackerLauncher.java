package shinado.indi.vender.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.IOHelper;
import indi.shinado.piping.launcher.IOHelperFactory;
import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.launcher.impl.HackerView;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;
import indi.shinado.piping.util.android.AppManager;
import shinado.indi.vender.R;

public class HackerLauncher extends BaseLauncherView implements DeviceConsole, Feedable{

    private ScrollView mScrollView;
    private HackerView mHackerView;

    /**
     * user input
     */
//    private String mInputText = "";

    private ConsoleHelper mConsoleHelper;

    private IOHelper mIOHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_launcher);

        mIOHelper = new IOHelperFactory().getInstance();
        initViews();

        mConsoleHelper = new ConsoleHelper(this, this, new PipesLoader(), TranslatorFactory.getTranslator(this, 2));
        mIOHelper.connect(this, /*findViewById(R.id.keyboard)*/ findViewById(R.id.input), mConsoleHelper);

        setupStatusBar();
        addFeedable(this);
//        startService(new Intent(this, LockService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHackerView.stop();
        mConsoleHelper.destroy();
//        stopService(new Intent(this, LockService.class));
    }

    private void setupStatusBar(){
        new StatusBarHelper().setupStatusBar(this);
    }

    private void initViews() {
        mScrollView = (ScrollView) this.findViewById(R.id.scrollView);
        TextView console = (TextView) this.findViewById(R.id.displayText);
        console.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOHelper.restartInput();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 800);
            }
        });
        mHackerView = new HackerView(console, this);
        mHackerView.init();
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
        mIOHelper.startInput();
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
    }

    @Override
    public void intercept() {
        releaseInput();
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
    public void onFeed(int flag, String msg, String pkg) {
        //clear input
        mIOHelper.clearInput();
        mConsoleHelper.reset();
        mConsoleHelper.forceShow(pkg, msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                mConsoleHelper.onShift();
                return true;
            case KeyEvent.KEYCODE_BACK:
                mConsoleHelper.onUpArrow();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void resume(boolean flag) {
        if (!flag){
            mConsoleHelper.intercept();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
