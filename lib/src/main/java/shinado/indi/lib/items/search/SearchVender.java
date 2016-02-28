package shinado.indi.lib.items.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.CompareUtil;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.VenderItem.Value;
import shinado.indi.lib.items.search.translator.AbsTranslator;

public abstract class SearchVender extends BaseVender {

    protected HashMap<String, ArrayList<VenderItem>> allItemMap = new HashMap<>();
    protected Stack<TreeSet<VenderItem>> resultStack =
            new Stack<>();

    public SearchVender(int id) {
        super(id);
    }

    /*
     maya.txt.play
     */
    @Override
    public TreeSet<VenderItem> search(TreeSet<VenderItem> prev, String key, int length) {
        TreeSet<VenderItem> result = null;
        Value value = getValue(key);
        //contains params
        if (!value.isParamsEmpty()){
            return null;
        }

        if (length > 0) {
            result = search(prev, value);
            push(result);
        } else if (length < 0) {
            for (int i = length; i < 0; i++) {
                if (!resultStack.isEmpty()){
                    pop();
                }
            }
            if (!resultStack.empty()) {
                result = peek();
            }
        } else {
            if (!resultStack.empty()) {
                result = peek();
            }
        }
        return result;
    }

    protected TreeSet<VenderItem> search(TreeSet<VenderItem> prev, Value value) {
        TreeSet<VenderItem> some = new TreeSet<>();
        String key = value.body;
        if (key.length() == 1) {
            if (!key.equals(".")) {
                ArrayList<VenderItem> allItems = fetchItemsFromMap(key);
                some = fulfill(allItems, key, value, prev);
            }
        } else {
            some = getResultFromStack(key);
        }
        return some;
    }

    protected ArrayList<VenderItem> fetchItemsFromMap(String key) {
        return allItemMap.get(key);
    }

    /**
     * fulfill with key index and value which contains the full info of the instruction
     * @param list
     * @param key
     * @param value
     * @return
     */
    protected TreeSet<VenderItem> fulfill(ArrayList<VenderItem> list, String key, Value value, TreeSet<VenderItem> prev) {
        TreeSet<VenderItem> set = new TreeSet<>();
        if (list == null) {
            return set;
        }
        //set key index for each item
        for (VenderItem item : list) {
            int keyIndex = getKeyIndex(item, key);
            item.setKeyIndex(keyIndex);
            item.getValue().pre = value.pre;
            item.getValue().params = value.params;
            item.setSuccessors(prev);
            set.add(item);
        }
        return set;
    }

    /**
     * return the key index to be searched
     * e.g. input "k"
     * ["kakao", "talk"] -> 0
     * ["we", "kite"] -> 1
     * ["we", "chat"] -> 2
     * so that "kakao talk" will come first
     */
    protected int getKeyIndex(VenderItem item, String key) {
        int i = 0;
        //set key index
        for (String name : item.getName()) {
            if (name.startsWith(key)) {
                break;
            }
            i++;
        }

        return i;
    }

    protected TreeSet<VenderItem> getResultFromStack(String key) {
        TreeSet<VenderItem> some = new TreeSet<>();
        if (!resultStack.empty()) {
            TreeSet<VenderItem> list = resultStack.peek();
            Object[] array = list.toArray();
            for (int i = 0; i < list.size(); i++) {
                VenderItem res = (VenderItem) array[i];
                String[] name = res.getName();
                if (CompareUtil.contains(name, key)) {
                    some.add(res);
                }
            }
        }
        return some;
    }

    protected void removeItemInMap(VenderItem vo) {
        String[] name = vo.getName();
        for (String n : name) {
            for (int i = 0; i < n.length(); i++) {
                String c = n.charAt(i) + "";
                ArrayList<VenderItem> list = allItemMap.get(c);
                if (list == null) {
                    continue;
                }
                list.remove(vo);
            }
        }
    }

    /**
     * ["face", "book"] = > ["f" -> "face", "b" -> "book"]
     */
    protected void putItemInMap(VenderItem vo) {
        String[] name = vo.getName();
        for (String n : name) {
            String c = n.charAt(0) + "";
            ArrayList<VenderItem> list = allItemMap.get(c);
            if (list == null) {
                list = new ArrayList<>();
                allItemMap.put(c, list);
            }
            list.add(vo);
        }
    }

    public abstract void load(AbsTranslator translator);

    public abstract int getType();

    protected void push(TreeSet<VenderItem> some) {
        resultStack.push(some);
    }

    protected TreeSet<VenderItem> peek() {
        return resultStack.peek();
    }

    protected TreeSet<VenderItem> pop() {
        return resultStack.pop();
    }

}
