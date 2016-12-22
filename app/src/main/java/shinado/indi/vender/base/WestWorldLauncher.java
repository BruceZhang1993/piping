package shinado.indi.vender.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.shinado.piping.geek.ShoppingHeadView;
import com.shinado.piping.geek.Tutorial;
import java.util.Collection;
import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.CharacterInputCallback;
import indi.shinado.piping.launcher.IOHelper;
import indi.shinado.piping.launcher.IOHelperFactory;
import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.launcher.functionality.ITutorial;
import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.IPipeManager;
import indi.shinado.piping.pipes.PipeManager;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;
import indi.shinado.piping.settings.ConsoleAnimation;
import indi.shinado.piping.settings.Preferences;
import shinado.indi.vender.R;

public class WestWorldLauncher extends BaseLauncherView implements DeviceConsole, Feedable, ITutorial {

    private static final int REQUEST_COLOR = 1;
    private ScrollView mScrollView;
    private TextView indicatorTv;
    private TextView consoleTextView;
    private ViewGroup selections;
    private boolean mInputBlock = false;

    private ConsoleHelper mConsoleHelper;

    private KeyDownCallback mKeyDownCallback;
    private boolean waitingForKeyDown;

    private Preferences preferences;
    private int KEY_SHIFT;

    private IOHelper mIOHelper;
    private IPipeManager mPipeManager;

    private Tutorial mTutorial;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_west_world);
        log("onCreate");

        mTutorial = new Tutorial(this, this);
        preferences = new Preferences(this);
        reloadKey();

        mIOHelper = new IOHelperFactory().getInstance();
        initViews();

        mPipeManager = new PipeManager();
        mPipeManager.load(this, new PipesLoader(), this, TranslatorFactory.getTranslator(this));

        mConsoleHelper = new ConsoleHelper(this, mPipeManager);
        mIOHelper.connect(this, findViewById(R.id.input), mConsoleHelper);

        setupStatusBar();
        addFeedable(this);

    }

    private void reloadKey() {
        KEY_SHIFT = preferences.getShiftKey();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPipeManager.destroy();

    }

    private void setupStatusBar() {
//        new StatusBarHelper().setupStatusBar(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        log("onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initViews() {
        selections = (ViewGroup) findViewById(R.id.selections);
        indicatorTv = (TextView) findViewById(R.id.indicator);
        mScrollView = (ScrollView) this.findViewById(R.id.scrollView);
        consoleTextView = (TextView) this.findViewById(R.id.console);

    }

    private void replaceItem(boolean ignoreMatch, Pipe pipe) {
        String newLine = constructDisplay(mIOHelper.getCurrentUserInput(), pipe);
        consoleTextView.append(newLine);
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
        mIOHelper.startInput();

        ConsoleAnimation animation = ConsoleAnimation.get();
        setAnimation(animation);
        mTutorial.start();
    }

    @Override
    public void displayResult(Collection<Pipe> results) {
        if (results.size() > 0) {
            selections.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
            int i = 0;
            for (Pipe pipe : results){
                TextView item = (TextView) inflater.inflate(R.layout.item_selection, selections, false);
                item.setText(pipe.getDisplayName());
                final int index = i++;
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mConsoleHelper.select(index);
                    }
                });
                selections.addView(item);
            }
        }
    }

    @Override
    public void displayPrevious(Pipe pipe) {
        setIndicator(pipe.getDisplayName());
//        replaceItem(true, pipe);
    }

    @Override
    public void clear() {
        mIOHelper.clearInput();
    }

    @Override
    public void intercept() {
        releaseInput();
        mConsoleHelper.enableSearch();
    }


    @Override
    public void waitForSingleLineInput(SingleLineInputCallback inputCallback) {
        mConsoleHelper.waitForUserInput(inputCallback);
    }

    @Override
    public void waitForCharacterInput(final CharacterInputCallback inputCallback) {
        blindMode();

        mConsoleHelper.addInputCallback(new InputCallback() {
            @Override
            public void onInput(String character) {
                inputCallback.onCharacterInput(character);
                mConsoleHelper.removeInputCallback(this);
                quitBlind();
            }
        });
    }

    @Override
    public void waitForKeyDown(KeyDownCallback inputCallback) {
        mKeyDownCallback = inputCallback;
        waitingForKeyDown = true;
    }

    @Override
    public String getLastInput() {
        //TODO
        String input = consoleTextView.getText().toString();
        String[] split = input.split("\n");
        if (split.length > 0){
            return split[split.length-1];
        }else {
            return "";
        }
    }

    @Override
    public void display(String string) {
        consoleTextView.append(string);
    }

    @Override
    public void onEnter(Pipe pipe) {
        //add new line
        consoleTextView.append("\n");
        mIOHelper.clearInput();
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        mTutorial.resume();
    }

    @Override
    public void onSelected(Pipe pipe) {

//        replaceItem(true, pipe);
    }

    @Override
    public void onNothing() {
//        replaceItem(true, null);
        selections.removeAllViews();
    }

    @Override
    public void addInputCallback(InputCallback inputCallback) {
        mConsoleHelper.addInputCallback(inputCallback);
    }

    @Override
    public void removeInputCallback(InputCallback inputCallback) {
        mConsoleHelper.removeInputCallback(inputCallback);
    }

    @Override
    public void setIndicator(String indicator) {
        indicatorTv.setText(indicator);
    }

    @Override
    public void occupyMode() {
        mConsoleHelper.occupyMode();
    }

    @Override
    public void quitOccupy() {
        mConsoleHelper.quitOccupy();
    }

    @Override
    public void blindMode() {
        mConsoleHelper.blindMode();
    }

    @Override
    public void quitBlind() {
        mConsoleHelper.quitBlind();
        mIOHelper.clearInput();
    }

    @Override
    public void notifyUI() {

    }

    @Override
    public void startTutorial() {
        mTutorial.start();
    }

    @Override
    public void input(String string) {
        consoleTextView.append(string);
    }

    @Override
    public void replaceCurrentLine(String line) {
        //TODO
    }

    @Override
    public void blockInput() {
        mIOHelper.blockInput();
        mInputBlock = true;
    }

    @Override
    public void releaseInput() {
        mIOHelper.releaseInput();
        mInputBlock = false;
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
        if (waitingForKeyDown) {
            if (mKeyDownCallback != null) {
                mKeyDownCallback.onKeyDown(keyCode);
            }
            reloadKey();
            waitingForKeyDown = false;
            input("Special key set.");
            return true;
        }

        if (keyCode == KEY_SHIFT) {
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


    public void setAnimation(ConsoleAnimation animation) {

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
    }

    private void log(String msg) {
        Log.d("HackerLauncher", msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_COLOR:
                if (resultCode == RESULT_OK) {
                    int color = intent.getIntExtra(GlobalDefs.EXTRA_COLOR, 0);
                    mPref.setColor(color);
                }
                break;
            case ShoppingHeadView.REQUEST_SHOPPING:
                if (resultCode == RESULT_OK) {
                }
                break;
        }
    }


}
