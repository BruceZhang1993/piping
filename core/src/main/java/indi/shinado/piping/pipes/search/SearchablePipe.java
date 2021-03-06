package indi.shinado.piping.pipes.search;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;

public abstract class SearchablePipe extends BasePipe {

    private static final String TAG = "SearchablePipe";
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
        TreeSet<Pipe> result;

        Instruction instruction = new Instruction(input);
        result = search(instruction);

        callback.onSearchResult(result, instruction);
    }

    /**
     * fulfill with key index and instruction
     */
    protected TreeSet<Pipe> fulfill(TreeSet<Pipe> list, Instruction input) {
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
        return search(new Instruction(input));
    }

    protected TreeSet<Pipe> search(Instruction value){
        String body = value.body;
        String key = getKey(body);
        if (key == null){
            //get nothing
            return new TreeSet<>();
        }else{
            TreeSet<Pipe> result = search(key, body);
            result = fulfill(result, value);
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

    protected void removeItemInMap(Pipe vo) {
        for (TreeSet<Pipe> results : resultMap.values()){
            for (Pipe result : results){
                if (result.equals(vo)){
                    Log.d("Searchable", "remove item:" + vo.getExecutable());
                    results.remove(result);
                    break;
                }
            }
        }
    }

    /**
     * ["face", "book"] = > ["f" -> "facebook", "b" -> "facebook"]
     */
    protected void putItemInMap(Pipe vo) {
        vo.setBasePipe(this);
        String[] name = vo.getSearchableName().getNames();
        for (String n : name) {
            String c = n.charAt(0) + "";
            TreeSet<Pipe> list = resultMap.get(c);//allItemMap.get(c);
            if (list == null) {
                list = new TreeSet<>();
                resultMap.put(c, list);
            }
            list.add(vo);
        }
    }

    public abstract void destroy();

    public abstract Pipe getByValue(String value);
}
