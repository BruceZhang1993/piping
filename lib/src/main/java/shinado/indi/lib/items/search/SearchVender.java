package shinado.indi.lib.items.search;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.VenderItem;

/**
 * Created by Administrator on 2015/11/17.
 */
public abstract class SearchVender extends BaseVender {

    protected HashMap<String, ArrayList<VenderItem>> allItemMap = new HashMap<>();
    protected Stack<TreeSet<VenderItem>> resultStack =
            new Stack<>();
    private FunctionHandler mHandler;

    public SearchVender() {
        mHandler = new FunctionHandler(this);
    }

    @Override
    public void search(VenderItem prev, String key, int length) {
        Log.d("IFC", "start searching");
        if(length > 0){
            push(forwardSearch(key), length);
        }else if(length < 0){
            pop(length);
        }else {
            peek();
        }
        Log.d("IFC", "end searching");
    }

    private TreeSet<VenderItem> getVenderSet(String key){
        TreeSet<VenderItem> set = new TreeSet<>();
        ArrayList<VenderItem> list = allItemMap.get(key);

        for (VenderItem item : list){
            int i=0;
            for (String name : item.getName()){
                if (name.startsWith(key)){
                    item.setKeyIndex(i);
                    break;
                }
                i++;
            }
            item.setKeyIndex(i);
            set.add(item);
        }
        return set;
    }

    protected TreeSet<VenderItem> forwardSearch(String key){
        TreeSet<VenderItem> some = new TreeSet<>();
        if(key.length() == 1){
            if(!key.equals(".")){
                some = getVenderSet(key);
            }
        }else{
            if(!resultStack.empty()){
                TreeSet<VenderItem> list = resultStack.peek();
                Object[] array = list.toArray();
                for(int i=0; i<list.size(); i++){
                    VenderItem res = (VenderItem) array[i];
                    String[] name = res.getName();
                    if(contains(name, key)){
                        some.add(res);
                    }
                }
            }
        }
        return some;
    }

    protected void removeItemInMap(VenderItem vo){
        String[] name = vo.getName();
        for (String n:name){
            for (int i=0; i<n.length(); i++){
                String c = n.charAt(i)+"";
                ArrayList<VenderItem> list = allItemMap.get(c);
                if (list == null){
                    continue;
                }
                list.remove(vo);
            }
        }
    }

    /**
     * ["face", "book"] = > ["f" -> "face", "b" -> "book"]
     */
    protected void putItemInMap(VenderItem vo){
        vo.setType(VenderItem.TYPE_SEARCH);
        String[] name = vo.getName();
        for (String n:name){
            String c = n.charAt(0)+"";
            ArrayList<VenderItem> list = allItemMap.get(c);
            if (list == null){
                list = new ArrayList<>();
                allItemMap.put(c, list);
            }
            list.add(vo);
        }
    }

    static class FunctionHandler extends Handler {

        private WeakReference<SearchVender> handlerWeakReference;

        public FunctionHandler(SearchVender ifc){
            handlerWeakReference = new WeakReference<SearchVender>(ifc);
        }

        @Override
        public void handleMessage(Message msg){
            Log.d("IFC", "handleMessage");
            SearchVender ifc = handlerWeakReference.get();
            TreeSet<VenderItem> result = null;
            if(msg.what > 0){
                TreeSet<VenderItem> some = (TreeSet<VenderItem>) msg.obj;
                result = some;
                ifc.doPush(some);
            }else if(msg.what < 0){
                for(int i=msg.what; i<0; i++){
                    ifc.doPop();
                }
                if(!ifc.resultStack.empty()){
                    result = ifc.doPeek();
                }
            }else{
                if(!ifc.resultStack.empty()){
                    result = ifc.doPeek();
                }
            }
            if(ifc.mOnResultChangedListener != null){
                if(result != null){
                    ifc.mOnResultChangedListener.onResultChange(result, ifc.resultStack.size() == 1);
                }
            }
        }
    }

    protected void push(TreeSet<VenderItem> some, int length){
        Message msg = new Message();
        msg.what = length;
        msg.obj = some;
        mHandler.sendMessage(msg);
    }

    protected void doPush(TreeSet<VenderItem> some){
        resultStack.push(some);
    }

    protected TreeSet<VenderItem> doPeek(){
        return resultStack.peek();
    }

    protected TreeSet<VenderItem> doPop(){
        return resultStack.pop();
    }

    protected void pop(int length){
        mHandler.sendEmptyMessage(length);
    }

    protected void peek(){
        mHandler.sendEmptyMessage(0);
    }

}
