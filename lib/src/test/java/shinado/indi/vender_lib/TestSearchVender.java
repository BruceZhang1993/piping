package shinado.indi.vender_lib;

import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.SearchVender;
import shinado.indi.lib.items.search.translator.AbsTranslator;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TestSearchVender extends SearchVender {

    private ArrayList<VenderItem> testItems;
    private VenderItem phoneVender, actionVender;

    public TestSearchVender() {
        super(1);
        testItems = AppList.getFakeList();
        for (VenderItem item : testItems) {
            putItemInMap(item);
        }

        actionVender = new VenderItem("txt", "txt", new String[]{"txt"},  20);
        actionVender.setType(VenderItem.TYPE_ACTION);

        phoneVender = new VenderItem("123456", "maya", new String[]{"maya"}, VenderItem.BUILD_IN_ID_CONTACT);
        phoneVender.setType(VenderItem.TYPE_CONTACT);
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

    @Override
    public void load(AbsTranslator translator) {

    }

    @Override
    public int getType() {
        return VenderItem.TYPE_CONTACT;
    }

    @Test
    public void testFetchItemFromMap() throws Exception {
        testFetchItem1();
        testFetchItem2();
        testFetchItem3();
    }

    private void testFetchItem1() throws Exception{
        int[] sequence = new int[]{1, 2, 4};
        ArrayList<VenderItem> list = fetchItemsFromMap("k");
        int i = 0;
        for (VenderItem item : list) {
            assertEquals(sequence[i++], item.getId());
        }
    }

    private void testFetchItem2() throws Exception{
        ArrayList<VenderItem> list = fetchItemsFromMap("");
        assertEquals(null, list);
    }

    private void testFetchItem3() throws Exception{
        ArrayList<VenderItem> list = fetchItemsFromMap("ka");
        assertEquals(null, list);
    }

    @Test
    public void testForwardSearch() throws Exception {
        testSequence1();
        testSequence2();
        testSequence3();
    }

    private void testSequence1() throws Exception {
        int[] sequence = new int[]{2, 1, 4};
        TreeSet<VenderItem> result = search(null, new VenderItem.Value("k"));
        int i = 0;
        for (VenderItem item : result) {
            assertEquals(sequence[i++], item.getId());
        }
    }

    private void testSequence2() throws Exception {
        int[] sequence = new int[]{1, 4};
        TreeSet<VenderItem> result = search(null, new VenderItem.Value("ka"));
        int i = 0;
        for (VenderItem item : result) {
            assertEquals(sequence[i++], item.getId());
        }
    }

    private void testSequence3() throws Exception {
        int[] sequence = new int[]{6};
        TreeSet<VenderItem> some = search(null, new VenderItem.Value("b"));
        push(some);
        TreeSet<VenderItem> result = search(null, new VenderItem.Value("br"));
        int i = 0;
        for (VenderItem item : result) {
            assertEquals(sequence[i++], item.getId());
        }
    }

    @Test
    public void testGetKeyIndex() throws Exception {
        ArrayList<VenderItem> list = AppList.getFakeList();
        String key = "k";
        int[] result = AppList.getKeyIndex(key);
        int i = 0;
        for (VenderItem item : list) {
            assertEquals(result[i++], getKeyIndex(item, key));
        }
    }

    @Test
    public void testGetResultFromStack() throws Exception{
        testGetResult1();
        testGetResult2();
        testGetResult3();
    }

    private void testGetResult1() throws Exception{
        TreeSet<VenderItem> result = getResultFromStack("k");
        assertEquals(0, result.size());
    }

    private void testGetResult2() throws Exception{
        //push "k"
        TreeSet<VenderItem> some = new TreeSet<>();
        some.add(testItems.get(0));
        some.add(testItems.get(1));
        some.add(testItems.get(3));
        push(some);

        //search "ka"
        TreeSet<VenderItem> result = getResultFromStack("ka");
        assertEquals(2, result.size());
    }

    private void testGetResult3() throws Exception{
        TreeSet<VenderItem> result = getResultFromStack("");
        assertEquals(0, result.size());
    }

    @Test
    public void testSearch(){
        TreeSet<VenderItem> prev = new TreeSet<>();

        String key = "m";
        TreeSet<VenderItem> result = search(prev, key, 1);
        assertEquals(0, result.size());

        prev.add(phoneVender);
        key = "maya.";
        result = search(prev, key, 1);
        assertEquals(0, result.size());

        key = "maya.t";
        result = search(prev, key, 1);
        assertEquals(2, result.size());

        prev.clear();
        prev.add(actionVender);
        key = "maya.txt.";
        result = search(prev, key, 1);
        assertEquals(0, result.size());

        key = "maya.txt.k";
        result = search(prev, key, 1);
        assertEquals(3, result.size());
    }

    @Test
    public void testStartExecute(){
        //maya
        startExecution(phoneVender);
    }
}