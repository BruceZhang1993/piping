package shinado.indi.items;

import android.content.Context;
import android.test.AndroidTestCase;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

public class PipeTestCase extends AndroidTestCase{

    protected VirtualInput vi;
    protected SystemConsole console = new SystemConsole();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Context context = getContext();
        ConsoleHelper helper = new ConsoleHelper(context, console, new PipesLoader(), new EnglishTranslator(context));
        vi = new VirtualInput(helper);
    }


}
