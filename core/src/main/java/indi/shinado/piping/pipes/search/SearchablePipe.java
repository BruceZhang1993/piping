package indi.shinado.piping.pipes.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;

public abstract class SearchablePipe extends BasePipe {

    private OnItemsLoadedListener mOnItemsLoadedListener;
//    protected HashMap<String, ArrayList<Pipe>> allItemMap = new HashMap<>();
    protected Stack<TreeSet<Pipe>> resultStack =
            new Stack<>();
    protected HashMap<String, TreeSet<Pipe>> resultMap = new HashMap<>();

    public SearchablePipe(int id) {
        super(id);
    }

    /**
     * when inputting characters, which is, length > 0, do the search and save results in the stack
     * when deleting characters, which is length < 0, pop the stack
     */
    @Override
    public void search(String input, int length, SearchResultCallback callback) {
        TreeSet<Pipe> result = null;

//        if (!value.isParamsEmpty()) {
//            callback.onSearchResult(null, input);
//            return;
//        }
//        if (value.isBodyEmpty()) {
//            callback.onSearchResult(null, input);
//            return;
//        }

        result = search(input);

        callback.onSearchResult(result, input);
    }

    /**
     * search when length > 0
     * when length == 1, fetch from map,
     * otherwise, get search from stack
     */
//    protected TreeSet<Pipe> search(String input, String body) {
//        TreeSet<Pipe> some;
//        if (body.length() == 1) {
//            ArrayList<Pipe> allItems = fetchItemsFromMap(body);
//            some = fulfill(allItems, input);
//        } else {
//            some = getResultFromStack(body);
//        }
//        return some;
//    }

//    protected ArrayList<Pipe> fetchItemsFromMap(String key) {
//        return allItemMap.get(key);
//    }

    /**
     * fulfill with key index and instruction
     */
    protected TreeSet<Pipe> fulfill(TreeSet<Pipe> list, String input) {
        TreeSet<Pipe> set = new TreeSet<>();
        if (list == null) {
            return set;
        }

        //set key index for each item
        for (Pipe item : list) {
            fulfill(item, input);
            set.add(item);
        }
        return set;
    }

    protected TreeSet<Pipe> search(String input){
        Instruction value = new Instruction(input);
        String body = value.body;
        String key = getKey(body);
        if (key == null){
            //get nothing
            return new TreeSet<>();
        }else{
            TreeSet<Pipe> result = search(key, body);
            result = fulfill(result, input);
            return result;
        }
    }

    private TreeSet<Pipe> search(String key, String body){
        TreeSet<Pipe> result = new TreeSet<>();
        TreeSet<Pipe> all = resultMap.get(key);
        if (body.equals(key)){
            return all;
        }

        //search
        for (Pipe pipe : all){
            if (pipe.getSearchableName().contains(body)){
                result.add(pipe);
            }
        }
        //put in the map
        resultMap.put(body, result);
        return result;
    }

    /**
     * get the key to get from map
     * @param body
     * @return null if get nothing
     */
    protected String getKey(String body) {
        TreeSet<Pipe> result = resultMap.get(body);
        if (result == null){
            if (body.length() > 1){
                body = body.substring(0, body.length() - 1);
                return getKey(body);
            }else {
                return null;
            }
        }else {
            return body;
        }
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

    //TODO
    protected void removeItemInMap(Pipe vo) {
        String[] name = vo.getSearchableName().getNames();
        for (String n : name) {
            for (int i = 0; i < n.length(); i++) {
                String c = n.charAt(i) + "";
                ArrayList<Pipe> list = null;//allItemMap.get(c);
                if (list == null) {
                    continue;
                }
                list.remove(vo);
            }
        }
    }

    /**
     * ["face", "book"] = > ["f" -> "facebook", "b" -> "facebook"]
     */
    protected void putItemInMap(Pipe vo) {
        String[] name = vo.getSearchableName().getNames();
        for (String n : name) {
            String c = n.charAt(0) + "";
            TreeSet<Pipe> list = resultMap.get(c);//allItemMap.get(c);
            if (list == null) {
                list = new TreeSet<>();
                resultMap.put(c, list);
//                allItemMap.put(c, list);
            }
            list.add(vo);
        }
    }

//    protected void push(TreeSet<Pipe> some) {
//        resultStack.push(some);
//    }
//
//    protected TreeSet<Pipe> peek() {
//        return resultStack.peek();
//    }
//
//    protected TreeSet<Pipe> pop() {
//        return resultStack.pop();
//    }


}
