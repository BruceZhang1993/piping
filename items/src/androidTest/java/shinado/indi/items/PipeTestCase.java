package shinado.indi.items;

import android.test.AndroidTestCase;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.pipes.PipeManager;

public class PipeTestCase extends AndroidTestCase{

    protected VirtualInput vi;
    protected SystemConsole console = new SystemConsole();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ConsoleHelper helper = new ConsoleHelper(console, new PipeManager());
        vi = new VirtualInput(helper);
    }


}
