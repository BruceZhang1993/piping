package shinado.indi.lib.items.search;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.VenderItem;

/**
 * Created by Administrator on 2015/11/17.
 */
public abstract class SearchVender extends BaseVender {

    protected HashMap<String, TreeSet<VenderItem>> allItemMap = new HashMap<>();
    protected Stack<TreeSet<VenderItem>> resultStack =
            new Stack<TreeSet<VenderItem>>();
    private FunctionHandler mHandler;

    public SearchVender() {
        mHandler = new FunctionHandler(this);
    }

    public void search(String key, int length) {
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

    protected TreeSet<VenderItem> forwardSearch(String key){
        TreeSet<VenderItem> some = new TreeSet<VenderItem>();
        if(key.length() == 1){
            if(!key.equals(".")){
                some.addAll(allItemMap.get(key));
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
                TreeSet<VenderItem> set = allItemMap.get(c);
                if (set == null){
                    continue;
                }
                set.remove(vo);
            }
        }
    }

    protected void putItemInMap(VenderItem vo){
        String[] name = vo.getName();
        for (String n:name){
            for (int i=0; i<n.length(); i++){
                String c = n.charAt(i)+"";
                TreeSet<VenderItem> set = allItemMap.get(c);
                if (set == null){
                    set = new TreeSet<VenderItem>();
                    allItemMap.put(c, set);
                }
                set.add(vo);
            }
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
                ifc.resultStack.push(some);
            }else if(msg.what < 0){
                for(int i=msg.what; i<0; i++){
                    ifc.resultStack.pop();
                }
                if(!ifc.resultStack.empty()){
                    result = ifc.resultStack.peek();
                }
            }else{
                if(!ifc.resultStack.empty()){
                    result = ifc.resultStack.peek();
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

    protected void pop(int length){
        mHandler.sendEmptyMessage(length);
    }

    protected void peek(){
        mHandler.sendEmptyMessage(0);
    }

}
