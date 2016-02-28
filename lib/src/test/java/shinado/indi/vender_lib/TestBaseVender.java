package shinado.indi.vender_lib;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.VenderItem;

/**
 * Created by shinado on 2016/2/25.
 */
public class TestBaseVender extends BaseVender {

    public TestBaseVender(){
        super(2);
    }

    @Override
    public TreeSet<VenderItem> search(TreeSet<VenderItem> prev, String key, int length) {
        return null;
    }

    @Override
    public VenderItem getItem(String value) {
        return null;
    }

    @Override
    public void acceptInput(VenderItem result, String input) {

    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {

    }

    @Override
    public void execute(VenderItem result) {

    }

    @Test
    public void testGetValue() throws Exception {
        VenderItem.Value value = getValue(".trans");
        Assert.assertEquals(true, value.isEmpty());

        value = getValue("what.trans");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(true, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("what", value.pre);
        Assert.assertEquals("trans", value.body);

        value = getValue("what.trans -ls");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(false, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("what", value.pre);
        Assert.assertEquals("trans", value.body);
        Assert.assertEquals("ls", value.params[0]);

        value = getValue("what.trans -ls -a");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(false, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("what", value.pre);
        Assert.assertEquals("trans", value.body);
        Assert.assertEquals("ls", value.params[0]);
        Assert.assertEquals("a", value.params[1]);

        value = getValue(".trans-ls");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(false, value.isParamsEmpty());
        Assert.assertEquals(true, value.isPreEmpty());
        Assert.assertEquals(null, value.pre);
        Assert.assertEquals("trans", value.body);
        Assert.assertEquals("ls", value.params[0]);

        value = getValue("what the.trans -ls-a");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(false, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("what the", value.pre);
        Assert.assertEquals("trans", value.body);
        Assert.assertEquals("ls", value.params[0]);
        Assert.assertEquals("a", value.params[1]);

        value = getValue("maya.txt.play -ls-a");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(false, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("maya.txt", value.pre);
        Assert.assertEquals("play", value.body);
        Assert.assertEquals("ls", value.params[0]);
        Assert.assertEquals("a", value.params[1]);

        value = getValue("maya.");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(true, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("maya", value.pre);
        Assert.assertEquals("", value.body);

        value = getValue("maya");
        Assert.assertEquals(true, value.isEmpty());
        Assert.assertEquals(true, value.isParamsEmpty());
        Assert.assertEquals(true, value.isPreEmpty());
        Assert.assertEquals("maya", value.body);


        value = getValue("maya.txt.");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(true, value.isParamsEmpty());
        Assert.assertEquals(false, value.isPreEmpty());
        Assert.assertEquals("maya.txt", value.pre);
        Assert.assertEquals("", value.body);

        value = getValue("-a");
        Assert.assertEquals(false, value.isEmpty());
        Assert.assertEquals(true, value.isParamsEmpty());
        Assert.assertEquals(true, value.isPreEmpty());
        Assert.assertEquals(1, value.params.length);
    }
}
