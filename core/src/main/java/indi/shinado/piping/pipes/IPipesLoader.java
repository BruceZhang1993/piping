package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public interface IPipesLoader {

    ArrayList<BasePipe> load(BaseLauncherView context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener);

    BasePipe load(PipeEntity entity, BaseLauncherView context, Console console, AbsTranslator translator, BasePipe.OnItemsLoadedListener listener);

}
