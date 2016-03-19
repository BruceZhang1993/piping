package indi.shinado.piping.launcher.impl;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.IPipeManager;
import indi.shinado.piping.pipes.IPipesLoader;
import indi.shinado.piping.pipes.PipeSearcher;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.pipes.impl.search.ApplicationPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class ConsoleHelper implements IPipeManager{

    private DeviceConsole console;

    private PipeSearcher mSearcher;

    private IPipesLoader mLoader;
    private Context mContext;
    private AbsTranslator mTranslator;

    private ArrayList<BasePipe> mPipes = new ArrayList<>();
    private ArrayList<String> mHistory = new ArrayList<>();
    private int mHistoryPointer = 0;

    private TreeSet<Pipe> mResults = new TreeSet<>();

    //introduced in version 3
    //to receive interception
    private Pipe mCurrent;

    private int mCurrentSelection = 0;
    private String mCurrentInput = "";
    private OnHistoryListener mOnHistoryListener;

    public ConsoleHelper(Context context, final DeviceConsole console, final IPipesLoader loader, AbsTranslator translator) {
        this.console = console;
        this.mLoader = loader;
        this.mContext = context;
        this.mTranslator = translator;

        mPipes = new ArrayList<>();
        mPipes.addAll(loader.load(context, console, translator, new SearchablePipe.OnItemsLoadedListener() {

            private int loadedItemsCount = 0;

            @Override
            public void onItemsLoaded(int id, int total) {
                if (++loadedItemsCount == total) {
                    console.onSystemReady();
                }
            }
        }));

        mSearcher = new PipeSearcher();
        mSearcher.addPipes(mPipes);
        for (BasePipe pipe : mPipes){
            pipe.setPipeManager(this);
        }

        mSearcher.setOnResultChangeListener(new PipeSearcher.OnResultChangeListener() {

            @Override
            public void onResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous) {
                System.out.println("get results from input:" + input + ", size:" + results.size());
                mResults.addAll(results);

                if (!mResults.isEmpty()) {
                    console.displayResult(mResults.first());
                } else {
                    if (!previous.isEmpty()) {
                        if (input.endsWith(".")){
                            console.displayPrevious(previous.get());
                        } else {
                            console.onNothing();
                        }
                    } else {
                        console.onNothing();
                    }
                }
            }
        });
    }

    @Override
    public void addNewPipe(PipeEntity entity){
        entity.save();
        console.blockInput();
        BasePipe pipe = mLoader.load(entity, mContext, console, mTranslator, new BasePipe.OnItemsLoadedListener() {
            @Override
            public void onItemsLoaded(int id, int total) {
                console.releaseInput();
            }
        });
        pipe.setPipeManager(this);
        mSearcher.addPipe(pipe);
    }

    @Override
    public boolean removePipe(int id) {
        if (inDatabase(id)){
            mSearcher.removePipe(id);
            new Delete().from(PipeEntity.class).where("cId = " + id).execute();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public ArrayList<BasePipe> getAllPipes() {
        return mPipes;
    }

    @Override
    public void destroy() {
        for (BasePipe pipe : mPipes){
            if (pipe instanceof SearchablePipe){
                ((SearchablePipe) pipe).destroy();
            }
        }
    }

    private boolean inDatabase(int id){
        PipeEntity search = new Select().from(PipeEntity.class).where("cId = ?", id).executeSingle();
        return (search != null);
    }

    public void forceShow(String pkg, String msg){
        Pipe item = getPipe(pkg);
        mResults.clear();
        mResults.add(item);
        console.input(msg);
        console.displayResult(item);
    }

    public void reset(){
        mCurrentSelection = 0;
        mCurrentInput = "";
        mResults.clear();
        mSearcher.clearPrevious();
    }

    public void onUserInput(String input, int before, int count) {
        mCurrentInput = input;
        mResults.clear();
        mSearcher.search(input, before, count, mCurrentSelection);
        mCurrentSelection = 0;
    }

    public void onShift() {
        Pipe current = passPreviousToNext();
        if (current != null) {
            console.onShift(current);
        }
    }

    public void onUpArrow(){
        if (!mHistory.isEmpty()){
            if (--mHistoryPointer < 0){
                mHistoryPointer = mHistory.size() - 1;
            }

            onHistory();
        }
    }

    public void onDownArrow(){
        if (!mHistory.isEmpty()){
            if (++mHistoryPointer >= mHistory.size()){
                mHistoryPointer = 0;
            }

            onHistory();
        }
    }

    private void onHistory(){
        String history = mHistory.get(mHistoryPointer);
        int before = mCurrentInput.length();

        if(mOnHistoryListener != null){
            mOnHistoryListener.onHistoryInput(history);
        }
        reset();
        onUserInput(history, before, history.length());
    }

    /**
     * pass previous to next item and return the next item
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
        if (mResults.isEmpty()){
            return null;
        }
        mCurrentSelection = (mCurrentSelection + 1) % mResults.size();
        return getCurrent();
    }

    public void onEnter() {
        addToHistory();
        Pipe current = getCurrent();
        if (current != null) {
            console.onEnter(current);
            current.getBasePipe().startExecution(current);
            current.setPrevious(null);
            mCurrent = current;
        }
        reset();
    }

    private void addToHistory(){
        if (!mCurrentInput.isEmpty()){
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

    public void setOnHistoryListener(OnHistoryListener mOnHistoryListener) {
        this.mOnHistoryListener = mOnHistoryListener;
    }

    //introduced from version 3
    public void intercept() {
        console.intercept();

        if (mCurrent != null){
            mCurrent.getBasePipe().intercept();
        }
    }

    private Pipe getPipe(String pkg){
        for (BasePipe pipe : mPipes){
           if (pipe instanceof ApplicationPipe){
               return ((ApplicationPipe)pipe).getByPackageName(pkg);
           }
        }
        return null;
    }

    public interface OnHistoryListener{
        void onHistoryInput(String history);
    }

}
