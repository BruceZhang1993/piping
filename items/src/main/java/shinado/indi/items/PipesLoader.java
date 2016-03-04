package shinado.indi.items;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.IPipesLoader;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import com.shinado.translate.TranslatingPipe;

public class PipesLoader implements IPipesLoader {

    public PipesLoader(){

    }

    @Override
    public ArrayList<BasePipe> load(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener) {
        ArrayList<BasePipe> pipes = new ArrayList<>();

        TranslatingPipe actionPipe = new TranslatingPipe(100);
        pipes.add(actionPipe);
        register(actionPipe, context, console, translator, listener, 1);

        // TODO
        // register your pipe here and add to pipes

        return pipes;
    }

    @Override
    public BasePipe load(PipeEntity entity, Context context, Console console, AbsTranslator translator, BasePipe.OnItemsLoadedListener listener) {
        return null;
    }

    private void register(BasePipe basePipe, Context context, Console console, AbsTranslator translator,
                         SearchablePipe.OnItemsLoadedListener listener, int size){
        basePipe.setConsole(console);
        basePipe.setContext(context);
        basePipe.load(translator, listener, size);
    }

}
