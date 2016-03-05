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

    protected ArrayList<BasePipe> mBasePipes = new ArrayList<>();
    protected Pipe.PreviousPipes mPrevious = new Pipe.PreviousPipes();

    private OnResultChangeListener mOnResultChangeListener;

    private int recallTimes = 0;

    public void addPipe(BasePipe pipe) {
        mBasePipes.add(pipe);
    }

    public boolean removePipe(int id){
        int i = 0;
        for (BasePipe pipe : mBasePipes){
            if (pipe.getId() == id){
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
        getPreviousPipes(input, pointer);
        resetOnSearch();

        doSearch(input, count - before);
    }

    private void getPreviousPipes(String input, int pointer) {
        if (input.endsWith(Keys.PIPE)) {
            mPrevious = new Pipe.PreviousPipes(mResults, pointer);
        }
    }

    private void resetOnSearch(){
        mResults.clear();
        recallTimes = 0;
    }

    public void clearPrevious(){
        mPrevious.clear();
    }

    private void doSearch(String input, int length) {
        for (BasePipe pipe : mBasePipes) {
            pipe.search(input, length, mCallback);
        }
    }

    private BasePipe.SearchResultCallback mCallback = new BasePipe.SearchResultCallback() {
        @Override
        public void onSearchResult(TreeSet<Pipe> results, String input) {
            if (results != null && results.size() != 0){
                mResults.addAll(results);
            }
            if (++recallTimes == mBasePipes.size()){
                removeFirstPreviousInResult();
                setPreviousForFirstResult();
                notifyResultChange(mResults, input, mPrevious);
            }
        }

        //only set previous for the first item
        //pass it on to next when shifting
        private void setPreviousForFirstResult(){
            if (!mResults.isEmpty()){
                Pipe first = mResults.first();
                first.setPrevious(mPrevious);
            }
        }

        //remove the result that matches the previous
        private void removeFirstPreviousInResult(){
            if(!mResults.isEmpty()){
                Pipe display = mPrevious.get();
                if (display != null){
                    mResults.remove(display);
                }
            }
        }
    };

    private void notifyResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous) {
        if (mOnResultChangeListener != null){
            mOnResultChangeListener.onResultChange(results, input, previous);
        }
    }

    public void setOnResultChangeListener(OnResultChangeListener listener){
        mOnResultChangeListener = listener;
    }

    public interface OnResultChangeListener{
        public void onResultChange(TreeSet<Pipe> results, String input, Pipe.PreviousPipes previous);
    }

}
