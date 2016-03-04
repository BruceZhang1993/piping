package com.shinado.piping;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.IPipesLoader;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class TempPipesLoader implements IPipesLoader {
    @Override
    public ArrayList<BasePipe> load(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener) {
        ArrayList<BasePipe> pipes = new ArrayList<>();

        TestActionPipe actionPipe = new TestActionPipe();
        pipes.add(actionPipe);
        register(actionPipe, context, console, translator, listener, 2);

        TestDefaultInputActionPipe defaultPipe = new TestDefaultInputActionPipe();
        pipes.add(defaultPipe);
        register(defaultPipe, context, console, translator, listener, 2);

        TestAppPipe appPipe = new TestAppPipe();
        pipes.add(appPipe);
        register(appPipe, context, console, translator, listener, 2);

        TestContactPipe contactPipe  = new TestContactPipe();
        pipes.add(contactPipe);
        register(contactPipe, context, console, translator, listener, 2);

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
