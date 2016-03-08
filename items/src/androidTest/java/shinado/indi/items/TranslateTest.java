package shinado.indi.items;

import android.content.Context;
import android.test.AndroidTestCase;

import org.junit.Test;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

public class TranslateTest extends PipeTestCase{

    @Test
    public void testInput() {
        console.log("--------------what|trans-------------");
        vi.inputString("what"+ Keys.PIPE +"trans");
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
        vi.inputString("trans"+ Keys.PARAMS + "ls");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("--------------what|trans-ls-------------");
        vi.inputString("what"+ Keys.PIPE +"trans"+ Keys.PARAMS +"ls");
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
