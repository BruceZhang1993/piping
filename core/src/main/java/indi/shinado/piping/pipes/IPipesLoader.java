package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public interface IPipesLoader {

    public ArrayList<BasePipe> load(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener);

    public BasePipe load(PipeEntity entity, Context context, Console console, AbsTranslator translator, BasePipe.OnItemsLoadedListener listener);

}
