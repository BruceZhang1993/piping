package shinado.indi.items;

import android.content.Context;
import android.test.AndroidTestCase;

import org.junit.Test;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

public class AnotherPipeTest extends AndroidTestCase{


    private VirtualInput vi;
    private SystemConsole console = new SystemConsole();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Context context = getContext();
        ConsoleHelper helper = new ConsoleHelper(context, console, new PipesLoader(), new EnglishTranslator(context));
        vi = new VirtualInput(helper);
    }

    @Test
    public void testInput() {
        console.log("--------------what.trans-------------");
        vi.inputString("what.trans");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("--------------trans-------------");
        vi.inputString("trans");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("---------------trans-ls------------");
        vi.inputString("trans-ls");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("--------------what.trans-ls-------------");
        vi.inputString("what.trans-ls");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("--------------nothing-------------");
        vi.inputString("");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
