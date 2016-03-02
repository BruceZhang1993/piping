package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.pipes.action.CopyPipe;
import indi.shinado.piping.pipes.action.InstallPipe;
import indi.shinado.piping.pipes.search.ApplicationPipe;
import indi.shinado.piping.pipes.search.ContactPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class PipesLoader implements IPipesLoader{

    public ArrayList<BasePipe> loadFromLocal(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener){
        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.add(new CopyPipe(1));
        pipes.add(new ApplicationPipe(2));
        pipes.add(new ContactPipe(3));
        pipes.add(new InstallPipe(4));

        for (BasePipe basePipe : pipes){
            register(basePipe, context, console, translator, listener, pipes.size());
        }
        return pipes;
    }

    public void register(BasePipe basePipe, Context context, Console console, AbsTranslator translator,
                         SearchablePipe.OnItemsLoadedListener listener, int total){
        basePipe.setConsole(console);
        basePipe.setContext(context);
        basePipe.load(translator, listener, total);
    }

    public ArrayList<BasePipe> loadFromStorage(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener){
        ArrayList<BasePipe> pipes = new ArrayList<>();
        return pipes;
    }

    @Override
    public ArrayList<BasePipe> load(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener) {
        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.addAll(loadFromLocal(context, console, translator, listener));
        pipes.addAll(loadFromStorage(context, console, translator, listener));
        return pipes;
    }
}
