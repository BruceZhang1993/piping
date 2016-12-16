package indi.shinado.piping.launcher.impl;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.IPipeManager;
import indi.shinado.piping.pipes.PipeSearcher;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchableActionPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;

public class ConsoleHelper {

    private DeviceConsole console;

    private boolean mSearchable = true;
    private boolean mBlind = false;
    private SingleLineInputCallback userInputCallback;

    //    private ArrayList<BasePipe> mPipes = new ArrayList<>();
    private ArrayList<String> mHistory = new ArrayList<>();
    private int mHistoryPointer = 0;

    private TreeSet<Pipe> mResults = new TreeSet<>();

    //introduced in version 3
    //to receive interception
    private Pipe mCurrent;

    private ArrayList<InputCallback> mInputCallbacks = new ArrayList<>();
    private int mCurrentSelection = 0;
    private String mCurrentInput = "";
    private OnHistoryListener mOnHistoryListener;
    private IPipeManager mPipeManager;

    public ConsoleHelper(final DeviceConsole console, final IPipeManager pipeManager) {
        this.console = console;
        this.mPipeManager = pipeManager;

        pipeManager.getSearcher().setOnResultChangeListener(new PipeSearcher.OnResultChangeListener() {

            @Override
            public void onResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous) {
                log("on result change");
                if (inOccupyMode()) {
                    console.onNothing();
                    return;
                }

                log("get results from input:" + input + ", size:" + results.size());
                mResults.addAll(results);

                if (input.endsWith(Keys.PARAMS)) {
                    if (!mResults.isEmpty()) {
                        //TODO
                        Pipe current = getCurrent();
                        List<Pipe> acceptableParams = current.getAcceptableParams();
                        if (acceptableParams != null && acceptableParams.size() > 0) {
                            console.displayResult(acceptableParams);
                        }
                        BasePipe basePipe = current.getBasePipe();
                        if (basePipe instanceof SearchableActionPipe){
                            SearchableActionPipe searchableActionPipe = (SearchableActionPipe) basePipe;
                            pipeManager.getSearcher().searchAction(searchableActionPipe);
                            searchableActionPipe.start(new SearchableActionPipe.OnQuitSearchActionListener() {
                                @Override
                                public void onQuit() {
                                    mPipeManager.getSearcher().searchAll();
                                }
                            });
                        }
                    }
                } else {
                    if (!mResults.isEmpty()) {
                        console.displayResult(mResults);
                    } else {
                        if (!previous.isEmpty()) {
                            if (input.endsWith(Keys.PIPE)) {
                                console.displayPrevious(previous.get());
                            } else {
                                console.onNothing();
                            }
                        } else {
                            console.onNothing();
                        }
                    }
                }

            }
        });
    }

    public void occupyMode() {
        mSearchable = false;
    }

    private boolean inOccupyMode() {
        return !mSearchable;
    }

    public void quitOccupy() {
        mSearchable = true;
    }

    public void blindMode() {
        mBlind = true;
    }

    private boolean inBlindMode() {
        return mBlind;
    }

    public void quitBlind() {
        mBlind = false;
    }

    public void waitForUserInput(SingleLineInputCallback callback) {
        userInputCallback = callback;
        occupyMode();
    }

    public void enableSearch() {
        quitOccupy();
    }

    public void forceShow(String value, String msg) {
        final Pipe item = mPipeManager.getPipe(value);
        mResults.clear();
        mResults.add(item);
        console.input(msg);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //display in the new line
                TreeSet<Pipe> set = new TreeSet<>();
                set.add(item);
                console.displayResult(set);
            }
        }, 500);
    }

    public void reset() {
        mCurrentSelection = 0;
        mCurrentInput = "";
        mResults.clear();
        mPipeManager.getSearcher().clearPrevious();
    }

    /**
     * @param input the current string in the console
     */
    public void onUserInput(String input, int before, int count) {
        if (inBlindMode()) {
            return;
        }
        log("onUserInput");
        mCurrentInput = input;
        mResults.clear();
        mPipeManager.getSearcher().search(input, before, count, mCurrentSelection);
        mCurrentSelection = 0;
    }

    /**
     * @param character the current input
     */
    public void onInput(String character) {
        log("onInput");
        for (InputCallback callback : mInputCallbacks) {
            callback.onInput(character);
        }
    }

    public void onShift() {
        Pipe current = passPreviousToNext();
        if (current != null) {
            console.onSelected(current);
        }
    }

    public void select(int index) {
        if (index >= 0 && index < mResults.size()) {
            select((Pipe) mResults.toArray()[index]);
        }
    }

    public void select(Pipe pipe) {
        Pipe current = passPreviousTo(pipe);
        if (current != null) {
            console.onSelected(current);
        }
    }

    public void onUpArrow() {
        if (!mHistory.isEmpty()) {
            if (--mHistoryPointer < 0) {
                mHistoryPointer = mHistory.size() - 1;
            }

            onHistory();
        }
    }

    @SuppressWarnings("unused")
    public void onDownArrow() {
        if (!mHistory.isEmpty()) {
            if (++mHistoryPointer >= mHistory.size()) {
                mHistoryPointer = 0;
            }

            onHistory();
        }
    }

    public void onEnter() {
        if (inOccupyMode()) {
            enableSearch();
            userInputCallback.onUserInput(mCurrentInput);
            console.onEnter(null);
        } else {
            addToHistory();
            Pipe current = getCurrent();
            if (current != null) {
                console.onEnter(current);
                current.getBasePipe().startExecution(current);
                current.setPrevious(null);
                mCurrent = current;
            }
        }
        reset();
    }

    public void setOnHistoryListener(OnHistoryListener mOnHistoryListener) {
        this.mOnHistoryListener = mOnHistoryListener;
    }

    //introduced from version 3
    public void intercept() {
        console.intercept();

        if (mCurrent != null) {
            mCurrent.getBasePipe().intercept();
        }
    }

    private void addToHistory() {
        if (!mCurrentInput.isEmpty()) {
            mHistory.add(mCurrentInput);
            mHistoryPointer = mHistory.size();
        }
    }

    private Pipe getCurrent() {
        if (mResults.isEmpty()) {
            return null;
        }
        int i = 1;
        Iterator<Pipe> it = mResults.iterator();
        while (i++ <= mCurrentSelection) {
            it.next();
        }
        return it.next();
    }

    private void onHistory() {
        String history = mHistory.get(mHistoryPointer);
        int before = mCurrentInput.length();

        if (mOnHistoryListener != null) {
            mOnHistoryListener.onHistoryInput(history);
        }
        reset();
        onUserInput(history, before, history.length());
    }

    /**
     * TODO
     *
     * @return the next item
     */
    private Pipe passPreviousTo(Pipe pipe) {
        Pipe.PreviousPipes previous = null;
        if (!mResults.isEmpty()) {
            Pipe prev = getCurrent();
            if (prev != null) {
                previous = prev.getPrevious();
                prev.setPrevious(null);
            }
        }

        int i = 0;
        for (Pipe item : mResults) {
            if (item.getId() == pipe.getId()) {
                item.setPrevious(previous);
                mCurrentSelection = i;
                return item;
            }
            i++;
        }
        return null;
    }

    /**
     * pass previous to next item and return the next item
     *
     * @return the next item
     */
    private Pipe passPreviousToNext() {
        Pipe.PreviousPipes previous = null;
        if (!mResults.isEmpty()) {
            Pipe prev = getCurrent();
            if (prev != null) {
                previous = prev.getPrevious();
                prev.setPrevious(null);
            }
        }
        Pipe next = nextItem();
        if (next != null) {
            next.setPrevious(previous);
        }
        return next;
    }

    private Pipe nextItem() {
        if (mResults.isEmpty()) {
            return null;
        }
        mCurrentSelection = (mCurrentSelection + 1) % mResults.size();
        return getCurrent();
    }

    public void addInputCallback(InputCallback inputCallback) {
        mInputCallbacks.add(inputCallback);
    }

    public void removeInputCallback(InputCallback inputCallback) {
        mInputCallbacks.remove(inputCallback);
    }

    private void log(String msg) {
        System.out.println("ConsoleHelper: " + msg);
    }

    public interface OnHistoryListener {
        void onHistoryInput(String history);
    }

}
