package indi.shinado.piping.pipes.action;

import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public abstract class ActionPipe extends BasePipe{

    public ActionPipe(int id) {
        super(id);
    }

    @Override
    public void search(String input, int length, SearchResultCallback callback) {
        Pipe result = search(input);

        callback(result, input, callback);
    }

    private void callback(Pipe result, String input, SearchResultCallback callback){
        TreeSet<Pipe> list = new TreeSet<>();
        if (result != null) {
            list.add(result);
        }
        callback.onSearchResult(list, input);
    }

    public Pipe search(String input) {
        if (input.isEmpty()){
            return null;
        }
        //create a new pipe
        Pipe result = new Pipe(getResult());
        fulfill(result, input);
        if (!result.getSearchableName().contains(result.getInstruction().body)){
            return null;
        }
//        result.setPrevious(prev);
        return result;
    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total){
        listener.onItemsLoaded(getId(), total);
    }

    /**
     * @return the Pipe with display name, id, and searchable name
     */
    protected abstract Pipe getResult();

    public String getDisplayName(){
        return getResult().getDisplayName();
    }
}
