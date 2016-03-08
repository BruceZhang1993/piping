package shinado.indi.items;

import android.content.Context;

import com.shinado.app.info.AppInfoPipe;
import com.shinado.app.task.TaskPipe;
import com.shinado.app.uninstall.UninstallPipe;
import com.shinado.copy.CopyPipe;
import com.shinado.example.SamplePipe;
import com.shinado.note.NotePipe;
import com.shinado.search.SearchItemPipe;
import com.shinado.translate.TranslatingPipe;

import java.util.ArrayList;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.IPipesLoader;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.impl.search.ApplicationPipe;
import indi.shinado.piping.pipes.impl.search.ContactPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class PipesLoader implements IPipesLoader {

    @Override
    public ArrayList<BasePipe> load(Context context, Console console, AbsTranslator translator, SearchablePipe.OnItemsLoadedListener listener) {
        ArrayList<BasePipe> pipes = new ArrayList<>();
        pipes.add(new ContactPipe(indi.shinado.piping.pipes.impl.PipesLoader.ID_CONTACT));
        pipes.add(new ApplicationPipe(indi.shinado.piping.pipes.impl.PipesLoader.ID_APPLICATION));

        // TODO
        // add your pipe here and add to pipes
        pipes.add(new SamplePipe(101));

        pipes.add(new CopyPipe(4));
        pipes.add(new AppInfoPipe(5));
        pipes.add(new UninstallPipe(6));
        pipes.add(new NotePipe(7));
        pipes.add(new TaskPipe(8));
        pipes.add(new SearchItemPipe(9));
        pipes.add(new TranslatingPipe(100));

        for (BasePipe basePipe : pipes){
            register(basePipe, context, console, translator, listener, pipes.size());
        }

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
