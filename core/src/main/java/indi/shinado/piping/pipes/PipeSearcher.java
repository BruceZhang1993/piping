package indi.shinado.piping.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;

/**
 * organize searching from every BasePipe added
 * organize previous items
 */
public class PipeSearcher {

    /**
     * searching results
     */
    private TreeSet<Pipe> mResults = new TreeSet<>();
    private int mCurrentSelection = 0;

    protected ArrayList<BasePipe> mBasePipes = new ArrayList<>();
    protected Pipe.PreviousPipes mPrevious = new Pipe.PreviousPipes();

    private OnResultChangeListener mOnResultChangeListener;

    private int recallTimes = 0;
    private int searchOnlyId = -1;

    public void addPipe(BasePipe pipe) {
        mBasePipes.add(pipe);
    }

    public boolean removePipe(int id) {
        int i = 0;
        for (BasePipe pipe : mBasePipes) {
            if (pipe.getId() == id) {
                mBasePipes.remove(i);
                return true;
            }
            i++;
        }
        return false;
    }

    public void addPipes(Collection<BasePipe> pipes) {
        mBasePipes.addAll(pipes);
    }

    public void search(String input, int before, int count, int pointer) {
        if (before < count) {
            getPreviousPipes(input, pointer);
        }
        resetOnSearch();

        doSearch(input, count - before);
    }

    public void setCurrent(int selection) {
        removePrevious();
        mCurrentSelection = selection;
        setPreviousForSelectedResult();
    }

    public void searchOnly(int pipeId) {
        searchOnlyId = pipeId;
    }

    public void searchAll() {
        searchOnlyId = -1;
    }

    private void getPreviousPipes(String input, int pointer) {
        if (input.endsWith(Keys.PIPE)) {
            mPrevious = new Pipe.PreviousPipes(mResults, pointer);
        }
    }

    private void resetOnSearch() {
        mResults.clear();
        recallTimes = 0;
    }

    public void clearPrevious() {
        mPrevious.clear();
    }

    private void doSearch(String input, int length) {
        for (BasePipe pipe : mBasePipes) {
            if (searchOnlyId > 0) {
                if (pipe.getId() == searchOnlyId){
                    pipe.search(input, length, mCallback);
                }
            }else {
                pipe.search(input, length, mCallback);
            }
        }
    }

    private BasePipe.SearchResultCallback mCallback = new BasePipe.SearchResultCallback() {
        @Override
        public void onSearchResult(TreeSet<Pipe> results, String input) {
            if (results != null && results.size() != 0) {
                //create a new copy of results
                //to avoid dead nesting
                for (Pipe pipe : results) {
                    mResults.add(new Pipe(pipe));
                }
//                mResults.addAll(results);
            }

            if (++recallTimes == (searchOnlyId >= 0 ? 1 : mBasePipes.size())) {
                removeFirstPreviousInResult(mResults, mPrevious.get());
                setPreviousForSelectedResult();
                notifyResultChange(mResults, input, mPrevious);
            }
        }

        //remove the result that matches the previous
        private void removeFirstPreviousInResult(TreeSet<Pipe> result, Pipe prev) {
            if (!result.isEmpty()) {
                if (prev != null) {
                    result.remove(prev);
                }
            }
        }
    };

    private void removePrevious() {
        if (mCurrentSelection >= 0 && mCurrentSelection < mResults.size()) {
            Pipe pipe = (Pipe) mResults.toArray()[mCurrentSelection];
            pipe.setPrevious(null);
        }
    }

    //only set previous for the selected item
    //pass it on to next when shifting
    private void setPreviousForSelectedResult() {
        if (mCurrentSelection >= 0 && mCurrentSelection < mResults.size()) {
            Pipe pipe = (Pipe) mResults.toArray()[mCurrentSelection];
            pipe.setPrevious(mPrevious);
        }
    }

    private void notifyResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous) {
        if (mOnResultChangeListener != null) {
            mOnResultChangeListener.onResultChange(results, input, previous);
        }
    }

    public void setOnResultChangeListener(OnResultChangeListener listener) {
        mOnResultChangeListener = listener;
    }

    public interface OnResultChangeListener {
        void onResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous);
    }

}
