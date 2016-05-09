package indi.shinado.piping.pipes.impl;

import android.content.Context;

import com.activeandroid.query.Select;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.IPipesLoader;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.impl.action.AppInfoPipe;
import indi.shinado.piping.pipes.impl.action.ClearPipe;
import indi.shinado.piping.pipes.impl.action.CopyPipe;
import indi.shinado.piping.pipes.impl.action.DiaryV4Pipe;
import indi.shinado.piping.pipes.impl.action.HelpPipe;
import indi.shinado.piping.pipes.impl.action.InstallPipe;
import indi.shinado.piping.pipes.impl.action.LastInputPipe;
import indi.shinado.piping.pipes.impl.action.NotePipe;
import indi.shinado.piping.pipes.impl.action.SearchItemPipe;
import indi.shinado.piping.pipes.impl.action.SettingPipe;
import indi.shinado.piping.pipes.impl.action.TaskPipe;
import indi.shinado.piping.pipes.impl.action.UninstallPipe;
import indi.shinado.piping.pipes.impl.action.snake.SnakePipe;
import indi.shinado.piping.pipes.impl.search.ApplicationPipe;
import indi.shinado.piping.pipes.impl.search.ContactPipe;
import indi.shinado.piping.pipes.impl.search.DirectoryPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class PipesLoader implements IPipesLoader {

    public static final int ID_APPLICATION = 2;
    public static final int ID_CONTACT = 1;

    public ArrayList<BasePipe> loadFromLocal(Context context){
        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.add(new ContactPipe(ID_CONTACT));
        pipes.add(new ApplicationPipe(ID_APPLICATION));
        pipes.add(new InstallPipe(3));
        pipes.add(new CopyPipe(4));
        pipes.add(new AppInfoPipe(5));
        pipes.add(new UninstallPipe(6));
        pipes.add(new NotePipe(7));
        pipes.add(new TaskPipe(8));
        pipes.add(new SearchItemPipe(9));
        pipes.add(new DirectoryPipe(10));
        pipes.add(new HelpPipe(11));
        pipes.add(new ClearPipe(12));
        pipes.add(new SettingPipe(13));
        pipes.add(new LastInputPipe(14));
        pipes.add(new UserPipe(15));
        pipes.add(new DiaryV4Pipe(16));
        pipes.add(new SnakePipe(17));
        pipes.add(new ShellPipe(18));

        return pipes;
    }

    public void register(BasePipe basePipe, BaseLauncherView context, Console console, AbsTranslator translator,
                         SearchablePipe.OnItemsLoadedListener listener, int total){
        if (basePipe == null){
            return;
        }
        basePipe.setConsole(console);
        basePipe.setContext(context);
        basePipe.load(translator, listener, total);
    }

    public ArrayList<BasePipe> loadFromStorage(Context context){
        ArrayList<BasePipe> pipes = new ArrayList<>();
        List<PipeEntity> entities = new Select().all().from(PipeEntity.class).execute();
        for (PipeEntity p : entities) {
            BasePipe pipe = loadFromStorage(context, p);
            pipes.add(pipe);
        }
        return pipes;
    }

    @Override
    public ArrayList<BasePipe> load(BaseLauncherView context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener) {
        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.addAll(loadFromLocal(context));
        pipes.addAll(loadFromStorage(context));

        for (BasePipe basePipe : pipes){
            register(basePipe, context, console, translator, listener, pipes.size());
        }
        return pipes;
    }

    @Override
    public BasePipe load(PipeEntity entity, BaseLauncherView context, Console console, AbsTranslator translator, BasePipe.OnItemsLoadedListener listener) {
        BasePipe pipe = loadFromStorage(context, entity);
        register(pipe, context, console, translator, listener, -1);
        return pipe;
    }

    private BasePipe loadFromStorage(Context context, PipeEntity entity) {
        File dexOutputDir = context.getDir("outdex", 0);
        DexClassLoader classLoader = new DexClassLoader(GlobalDefs.PATH_HOME + entity.getFileName(),
                dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        BasePipe pipe = null;
        try {
            Class<?> loadClass = classLoader.loadClass(entity.className);
            Constructor ctr = loadClass.getConstructor(int.class);
            pipe = (BasePipe) ctr.newInstance(entity.sid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pipe;
    }
}
