package indi.shinado.piping.pipes.action;

import android.content.Context;

import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.Value;

public abstract class ActionPipe extends BasePipe{

    public ActionPipe(int id) {
        super(id);
    }

    @Override
    public void search(TreeSet<Pipe> prev, String input, int length, SearchResultCallback callback) {
        Pipe result = doSearch(prev, input);

        callback(result, callback);
    }

    private void callback(Pipe result, SearchResultCallback callback){
        TreeSet<Pipe> list = new TreeSet<>();
        if (result != null) {
            list.add(result);
        }
        callback.onSearchResult(list);
    }

    public Pipe doSearch(TreeSet<Pipe> prev, String input) {
        Pipe result = getResult();
        result.setValue(new Value(input));
        if (!result.getSearchableName().contains(input)){
            return null;
        }
        result.setPrevious(prev);
        return result;
    }

    /**
     * @return the Pipe with display name, id, and searchable name
     */
    protected abstract Pipe getResult();


}
