package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.pipes.action.CopyPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class PipesLoader {

    private Context context;
    private Console console;
    private AbsTranslator translator;
    private SearchablePipe.OnItemsLoadedListener listener;

    public PipesLoader(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener) {
        this.context = context;
        this.console = console;
        this.translator = translator;
        this.listener = listener;
    }

    public ArrayList<BasePipe> loadFromLocal(){
        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.add(new CopyPipe(1));

        for (BasePipe basePipe : pipes){
            register(basePipe);
        }
        return pipes;
    }

    public void register(BasePipe basePipe){
        basePipe.setConsole(console);
        basePipe.setContext(context);
        if (basePipe instanceof SearchablePipe){
            ((SearchablePipe)basePipe).load(translator, listener);
        }
    }

    public ArrayList<BasePipe> loadFromStorage(){
        ArrayList<BasePipe> pipes = new ArrayList<>();
        return pipes;
    }

}
