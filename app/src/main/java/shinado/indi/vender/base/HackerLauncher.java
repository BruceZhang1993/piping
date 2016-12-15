package shinado.indi.vender.base;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.shinado.piping.geek.HeadLoadder;
import com.shinado.piping.geek.ShoppingHeadView;
import com.shinado.piping.geek.Tutorial;
import com.shinado.piping.geek.header.IHeadView;
import java.util.Collection;
import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.color.ColorActivity;
import indi.shinado.piping.feed.Feedable;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.CharacterInputCallback;
import indi.shinado.piping.launcher.IOHelper;
import indi.shinado.piping.launcher.IOHelperFactory;
import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.launcher.functionality.IAnimation;
import indi.shinado.piping.launcher.functionality.IBase;
import indi.shinado.piping.launcher.functionality.IText;
import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.launcher.impl.HackerView;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.ConsoleInfo;
import indi.shinado.piping.pipes.IPipeManager;
import indi.shinado.piping.pipes.PipeManager;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.search.translator.TranslatorFactory;
import indi.shinado.piping.settings.ConsoleAnimation;
import indi.shinado.piping.settings.Preferences;
import indi.shinado.piping.view.AnimationTextView;
import indi.shinado.piping.view.BoundaryView;
import shinado.indi.vender.R;

public class HackerLauncher extends BaseLauncherView implements DeviceConsole, Feedable, IText, IBase, IAnimation {

    private static final int REQUEST_COLOR = 1;
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
    private IPipeManager mPipeManager;

    private IHeadView mHeadView;
    private Tutorial mTutorial;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_launcher);
        log("onCreate");

        mTutorial = new Tutorial(this, this);
        preferences = new Preferences(this);
        reloadKey();

        mIOHelper = new IOHelperFactory().getInstance();
        initViews();

        mPipeManager = new PipeManager();
        mPipeManager.load(this, new PipesLoader(), this, TranslatorFactory.getTranslator(this));

        mConsoleHelper = new ConsoleHelper(this, mPipeManager);
        mIOHelper.connect(this, /*findViewById(R.id.keyboard)*/ findViewById(R.id.input), mConsoleHelper);

        setupStatusBar();
        addFeedable(this);
        setTextColor(wallpaper, mPref.getColor());
        setTextSize(wallpaper, mPref.getTextSize());
        setBoundaryColor(mPref.getColor());

        IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
        registerReceiver(mWallpaperSetReceiver, filter);

    }

    private void reloadKey(){
        KEY_SHIFT = preferences.getShiftKey();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHackerView.stop();
        unregisterReceiver(mWallpaperSetReceiver);
        mPipeManager.destroy();
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
        log("onResume");
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
            View view = mHeadView.getView(this, headLayout);
            headLayout.addView(view);
            mHeadView.onCreate();
            if (view instanceof ViewGroup){
                setTextSize((ViewGroup) view, mPref.getTextSize());
            }
        }
    }

    private void reloadHeadView(){
        if (mHeadView != null){
            mHeadView.onDestroy();
        }
        initHeadView();
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
        mTutorial.start();
    }

    @Override
    public void displayResult(Collection<Pipe> results) {
        if (results.size() > 0){
            replaceItem(true, (Pipe) results.toArray()[0]);
        }
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
        mTutorial.resume();
    }

    @Override
    public void onSelected(Pipe pipe) {
        replaceItem(true, pipe);
    }

    @Override
    public void onNothing() {
        replaceItem(true, null);
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
        //TODO
    }

    @Override
    public void searchOnly(int pipeId) {
        mPipeManager.getSearcher().searchOnly(pipeId);
    }

    @Override
    public void searchAll() {
        mPipeManager.getSearcher().searchAll();
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
    public BasePipe getPipeById(int id) {
        for (BasePipe item : mPipeManager.getAllPipes()){
            if (item.getId() == id){
                return item;
            }
        }
        return null;
    }

    @Override
    public void startTutorial() {
        mTutorial.start();
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
        if (waitingForKeyDown){
            if (mKeyDownCallback != null){
                mKeyDownCallback.onKeyDown(keyCode);
            }
            reloadKey();
            waitingForKeyDown = false;
            input("Special key set.");
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

    public ViewGroup getBackgroundView() {
        return wallpaper;
    }

    public BoundaryView getBoundaryView() {
        return boundaryView;
    }

    public void setInitText(String text) {
        mHackerView.setInitText(text);
    }

    public void setConsoleAnimation(ConsoleAnimation animation) {

    }

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
    }

    private void log(String msg){
        Log.d("HackerLauncher", msg);
    }


	protected void initWallpaper(){
		if (mPref.isWallpaperSet()){
			setWallpaper();
		}
	}

	@SuppressWarnings("deprecation")
	private void setWallpaper(){
		WallpaperManager wallpaperManager
				= WallpaperManager.getInstance(getApplicationContext());
		Drawable drawable = wallpaperManager.getDrawable();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			getBackgroundView().setBackground(drawable);
		}else {
			getBackgroundView().setBackgroundDrawable(drawable);
		}
	}

	protected void setBoundaryColor(int color){
		getBoundaryView().setBoundaryColor(color);
	}

	public void setBoundaryWidth(float width){
		getBoundaryView().setBoundaryWidth(width);
	}

	public void setTextSize(float size){
		setTextSize(getBackgroundView(), size);
	}

	protected void setTextSize(ViewGroup viewGroup, float size){
		for (int i=0;  i < viewGroup.getChildCount(); i++){
			View view = viewGroup.getChildAt(i);
			if (view instanceof TextView){
				TextView textView = (TextView) view;
				textView.setTextSize(size);
			}else if (view instanceof ViewGroup){
				setTextSize((ViewGroup) view, size);
			}
		}
	}

	protected void setTextColor(ViewGroup viewGroup, int color){
		if (viewGroup.getTag() != null && viewGroup.getTag().equals("no-format")){
			return;
		}

		for (int i=0;  i < viewGroup.getChildCount(); i++){
			View view = viewGroup.getChildAt(i);
			if (view instanceof TextView && ! (view instanceof EditText)){
				TextView textView = (TextView) view;
				textView.setTextColor(color);
			}else if (view instanceof ViewGroup){
				setTextColor((ViewGroup) view, color);
			}
		}
	}

	public void selectWallpaper(){
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
		startActivity(Intent.createChooser(intent, "Select Wallpaper"));
	}

	public void selectColor(){
		Intent intent = new Intent(this, ColorActivity.class);
		startActivityForResult(intent, REQUEST_COLOR);
	}

	public void setColor(int color){
		setTextColor(getBackgroundView(), color);
		setBoundaryColor(color);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		switch (requestCode) {
			case REQUEST_COLOR:
				if(resultCode == RESULT_OK){
					int color = intent.getIntExtra(GlobalDefs.EXTRA_COLOR, 0);
					mPref.setColor(color);
					setColor(color);
				}
				break;
            case ShoppingHeadView.REQUEST_SHOPPING:
                if (resultCode == RESULT_OK){
                    reloadHeadView();
                }
                break;
		}
	}

	private BroadcastReceiver mWallpaperSetReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mPref.setWallpaper(true);
			setWallpaper();
		}
	};



}
