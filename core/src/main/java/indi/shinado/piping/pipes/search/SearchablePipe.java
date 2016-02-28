package indi.shinado.piping.pipes.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.Value;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public abstract class SearchablePipe extends BasePipe{

    private OnItemsLoadedListener mOnItemsLoadedListener;
    protected HashMap<String, ArrayList<Pipe>> allItemMap = new HashMap<>();
    protected Stack<TreeSet<Pipe>> resultStack =
            new Stack<>();

    public SearchablePipe(int id) {
        super(id);
    }

    /*
     maya.txt.play
     */
    @Override
    public void search(TreeSet<Pipe> prev, String input, int length, SearchResultCallback callback) {
        TreeSet<Pipe> result = null;
        Value value = new Value(input);
        //contains params
        if (!value.isParamsEmpty()){
            callback.onSearchResult(null);
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
        callback.onSearchResult(result);
    }

    protected TreeSet<Pipe> search(TreeSet<Pipe> prev, Value value) {
        TreeSet<Pipe> some = new TreeSet<>();
        String key = value.body;
        if (key.length() == 1) {
            if (!key.equals(".")) {
                ArrayList<Pipe> allItems = fetchItemsFromMap(key);
                some = fulfill(allItems, key, value, prev);
            }
        } else {
            some = getResultFromStack(key);
        }
        return some;
    }

    protected ArrayList<Pipe> fetchItemsFromMap(String key) {
        return allItemMap.get(key);
    }

    /**
     * fulfill with key index and value which contains the full info of the instruction
     * @param list
     * @param key
     * @param value
     * @return
     */
    protected TreeSet<Pipe> fulfill(ArrayList<Pipe> list, String key, Value value, TreeSet<Pipe> prev) {
        TreeSet<Pipe> set = new TreeSet<>();
        if (list == null) {
            return set;
        }
        //set key index for each item
        for (Pipe item : list) {
            int keyIndex = getKeyIndex(item, key);
            item.setKeyIndex(keyIndex);
            item.getValue().pre = value.pre;
            item.getValue().params = value.params;
            item.setPrevious(prev);
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
    protected int getKeyIndex(Pipe item, String key) {
        int i = 0;
        //set key index
        for (String str : item.getSearchableName().getNames()) {
            if (str.startsWith(key)) {
                break;
            }
            i++;
        }

        return i;
    }

    protected TreeSet<Pipe> getResultFromStack(String key) {
        TreeSet<Pipe> some = new TreeSet<>();
        if (!resultStack.empty()) {
            TreeSet<Pipe> list = resultStack.peek();
            Object[] array = list.toArray();
            for (int i = 0; i < list.size(); i++) {
                Pipe res = (Pipe) array[i];
                if (res.getSearchableName().contains(key)) {
                    some.add(res);
                }
            }
        }
        return some;
    }

    protected void removeItemInMap(Pipe vo) {
        String[] name = vo.getSearchableName().getNames();
        for (String n : name) {
            for (int i = 0; i < n.length(); i++) {
                String c = n.charAt(i) + "";
                ArrayList<Pipe> list = allItemMap.get(c);
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
    protected void putItemInMap(Pipe vo) {
        String[] name = vo.getSearchableName().getNames();
        for (String n : name) {
            String c = n.charAt(0) + "";
            ArrayList<Pipe> list = allItemMap.get(c);
            if (list == null) {
                list = new ArrayList<>();
                allItemMap.put(c, list);
            }
            list.add(vo);
        }
    }

    public abstract void load(AbsTranslator translator, OnItemsLoadedListener listener);

    protected void push(TreeSet<Pipe> some) {
        resultStack.push(some);
    }

    protected TreeSet<Pipe> peek() {
        return resultStack.peek();
    }

    protected TreeSet<Pipe> pop() {
        return resultStack.pop();
    }

    public void setOnItemsLoadedListener(OnItemsLoadedListener listener){
        this.mOnItemsLoadedListener = listener;
    }

    public interface OnItemsLoadedListener{
        public void onItemsLoaded(int id);
    }
}
