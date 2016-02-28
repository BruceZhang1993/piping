package shinado.indi.vender_lib;

import org.junit.Test;

import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.SearchVender;
import shinado.indi.lib.items.search.translator.AbsTranslator;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TestSearch extends SearchVender{

    public TestSearch(){
        super(1);
        for (VenderItem item : AppList.getFakeList()){
            putItemInMap(item);
        }
    }

    @Override
    public VenderItem getItem(String value) {
        return null;
    }

    @Override
    public int execute(VenderItem result) {
        return 0;
    }

    @Test
    public void testSequence1() throws Exception {
        int[] sequence = new int[]{1, 2, 4};
        TreeSet<VenderItem> result = forwardSearch("k");
        int i=0;
        for (VenderItem item : result){
            assertEquals(sequence[i++], item.getId());
        }
    }

    @Test
    public void testSequence2() throws Exception {
        int[] sequence = new int[]{1, 4};
        TreeSet<VenderItem> result = forwardSearch("ka");
        int i=0;
        for (VenderItem item : result){
            assertEquals(sequence[i++], item.getId());
        }
    }

    @Test
    public void testSequence3() throws Exception {
        int[] sequence = new int[]{6};
        TreeSet<VenderItem> some = forwardSearch("b");
        push(some);
        TreeSet<VenderItem> result = forwardSearch("br");
        int i=0;
        for (VenderItem item : result){
            assertEquals(sequence[i++], item.getId());
        }
    }

    @Override
    public void load(AbsTranslator translator) {

    }
}