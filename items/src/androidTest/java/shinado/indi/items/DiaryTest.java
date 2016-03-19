package shinado.indi.items;

import org.junit.Test;

import indi.shinado.piping.pipes.entity.Keys;

public class DiaryTest extends PipeTestCase{

    @Test
    public void testInput() {
        console.log("--------------what a good day@9|diary-------------");
        vi.inputString("what a good day@9"+ Keys.PIPE +"diary");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("--------------no rating day|diary-------------");
        vi.inputString("no rating day"+ Keys.PIPE +"diary");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("--------------diary-------------");
        vi.inputString("diary");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        console.log("---------------diary-ls------------");
        vi.inputString("diary"+ Keys.PARAMS + "ls");
        vi.pressKey(VirtualInput.KEY_ENTER);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
