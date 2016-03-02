package indi.shinado.piping.launcher;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.IPipesLoader;
import indi.shinado.piping.pipes.PipeSearcher;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class ConsoleHelper {

    private DeviceConsole console;

    private PipeSearcher mSearcher;

    private ArrayList<String> mHistory = new ArrayList<>();

    private TreeSet<Pipe> mResults = new TreeSet<>();
    private int mCurrentSelection = 0;
    private String mCurrentInput = "";

    public ConsoleHelper(Context context, final DeviceConsole console, final IPipesLoader loader, AbsTranslator translator) {
        this.console = console;

        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.addAll(loader.load(context, console, translator, new SearchablePipe.OnItemsLoadedListener() {

            private int loadedItemsCount = 0;

            @Override
            public void onItemsLoaded(int id, int total) {
                if (++loadedItemsCount == total) {
                    console.onSystemReady();
                }
            }
        }));

        mSearcher = new PipeSearcher();
        mSearcher.addPipes(pipes);
        mSearcher.setOnResultChangeListener(new PipeSearcher.OnResultChangeListener() {

            @Override
            public void onResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous) {
                System.out.println("get results from input:" + input + ", size:" + results.size());
                mResults.addAll(results);
                mCurrentInput = input;

                if (!mResults.isEmpty()) {
                    console.displayResult(mResults.first());
                } else {
                    if (!previous.isEmpty()) {
                        console.displayPrevious(previous.get());
                    } else {
                        console.onNothing();
                    }
                }
            }
        });
    }

    public void forceShow(Pipe item){
        mResults.clear();
        mResults.add(item);
        console.displayResult(item);
    }

    public void reset(){
        mCurrentSelection = 0;
        mCurrentInput = "";
    }

    public void onUserInput(String input, int before, int count) {
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
        Pipe current = getCurrent();
        if (current != null) {
            console.onEnter(current);
            current.getBasePipe().startExecution(current);
            current.setPrevious(null);
        }
        mSearcher.clearPrevious();
        mHistory.add(mCurrentInput);
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


}
