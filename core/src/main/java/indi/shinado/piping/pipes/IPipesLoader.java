package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public interface IPipesLoader {

    public ArrayList<BasePipe> load(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener);

}
