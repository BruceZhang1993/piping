package shinado.indi.vender_lib;

import android.content.Context;

import com.shinado.piping.TestAppPipe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.TreeSet;

import indi.shinado.piping.keyboard.KeyCode;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.Console;
import indi.shinado.piping.pipes.PipeSearcher;
import indi.shinado.piping.pipes.PipesLoader;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

@RunWith(MockitoJUnitRunner.class)
public class Launcher {

    @Mock
    private Context context;

    private PipeSearcher mSearcher;

    @Before
    public void setup(){
        SearchablePipe.OnItemsLoadedListener onItemsLoadedListener = new SearchablePipe.OnItemsLoadedListener() {
            @Override
            public void onItemsLoaded(int id) {
                System.out.println("items loaded");
            }
        };
        PipesLoader loader = new PipesLoader(context, new StaticConsole(), new EnglishTranslator(context), onItemsLoadedListener);
        ArrayList<BasePipe> pipes = loader.loadFromLocal();

        TestAppPipe appPipe = new TestAppPipe(100);
        pipes.add(appPipe);
        loader.register(appPipe);

        mSearcher = new PipeSearcher();
        mSearcher.addPipes(pipes);
        mSearcher.setOnResultChangeListener(new PipeSearcher.OnResultChangeListener() {
            @Override
            public void onResultChange(TreeSet<Pipe> results) {
                System.out.println("get results, size:" + results.size());
            }
        });
        mSearcher.setOnKeyDownListener(new PipeSearcher.OnKeyDownListener() {
            @Override
            public void onKeyDown(int keyCode) {
                System.out.println("onKeyDown:" + keyCode);
            }
        });
    }

    @Test
    public void testSearch(){
        mSearcher.search("", 0, 0);
        mSearcher.search("k", 0, 1);
        mSearcher.search("ka", 1, 2);
        mSearcher.onKeyDown(KeyCode.KEY_SHIFT);
        mSearcher.search("k", 2, 1);
        mSearcher.search("", 1, 0);
    }

    public class StaticConsole implements Console {

        @Override
        public void input(String string) {
            System.out.println(string);
        }

    }
}
