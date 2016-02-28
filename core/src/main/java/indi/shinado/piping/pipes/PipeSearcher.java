package indi.shinado.piping.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Pipe;

public class PipeSearcher {

    /**
     * searching results
     */
    private TreeSet<Pipe> mResults = new TreeSet<>();

    private TreeSet<Pipe> mPrevious = new TreeSet<>();

    protected ArrayList<BasePipe> mBasePipes = new ArrayList<>();

    private OnResultChangeListener mOnResultChangeListener;

    private OnKeyDownListener mOnKeyDownListener;

    public void addPipe(BasePipe pipe) {
        mBasePipes.add(pipe);
    }

    public void addPipes(Collection<BasePipe> pipes) {
        mBasePipes.addAll(pipes);
    }

    public void search(String input, int before, int count) {
        mPrevious.addAll(getPreviousPipes(input));

        mResults.clear();
        doSearch(mPrevious, input, count - before);
    }

    public void onKeyDown(int keyCode){
        if (mOnKeyDownListener != null){
            mOnKeyDownListener.onKeyDown(keyCode);
        }
    }

    public TreeSet<Pipe> getPreviousPipes(String input) {
        TreeSet<Pipe> pipes = new TreeSet<>();
        if (input.endsWith(".")) {
            mPrevious.clear();
            pipes.addAll(mResults);
        }
        return pipes;
    }

    private void doSearch(TreeSet<Pipe> prev, String input, int length) {
        for (BasePipe pipe : mBasePipes) {
            pipe.search(prev, input, length, mCallback);
        }
    }

    private BasePipe.SearchResultCallback mCallback = new BasePipe.SearchResultCallback() {
        @Override
        public void onSearchResult(TreeSet<Pipe> results) {
            if (results != null && results.size() != 0){
                mResults.addAll(results);
                notifyResultChange(mResults);
            }
        }
    };

    private void notifyResultChange(TreeSet<Pipe> results) {
        if (mOnResultChangeListener != null){
            mOnResultChangeListener.onResultChange(results);
        }
    }

    public void setOnResultChangeListener(OnResultChangeListener listener){
        mOnResultChangeListener = listener;
    }

    public void setOnKeyDownListener(OnKeyDownListener listener){
        this.mOnKeyDownListener = listener;
    }

    public interface OnResultChangeListener{
        public void onResultChange(TreeSet<Pipe> results);
    }

    public interface OnKeyDownListener{
        public void onKeyDown(int keyCode);
    }

}
