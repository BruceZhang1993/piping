package shinado.indi.lib.items;

import android.content.Context;

import java.util.ArrayList;
import java.util.TreeSet;

import shinado.indi.lib.launcher.SearchHelper;

/**
 * the basic
 */
public abstract class BaseVender {

    public static final int TYPE_APP = VenderItem.BUILD_IN_ID_APP;
    public static final int TYPE_CONTACT = VenderItem.BUILD_IN_ID_CONTACT;

    protected int id;
    protected Context context;
    protected TreeSet<VenderItem> frequentItems = new TreeSet<>();
    protected OnResultChangedListener mOnResultChangedListener;
    protected SearchHelper mSearchHelper;
    protected FrequentMap mFrequentMap;

    public BaseVender(){
    }

    public void init(Context context, SearchHelper s, int id){
        this.context = context;
        mFrequentMap = new FrequentMap();
        this.mSearchHelper = s;
        this.id = id;
    }

    public void removeFrequency(VenderItem vo){
        mFrequentMap.remove(vo.getValue());
        frequentItems.remove(vo);
    }

    public void addFrequency(VenderItem item){
        item.addFrequency();
        boolean bExist = mFrequentMap.addFrequency(item.getValue());
        if(!bExist){
            frequentItems.add(item);
        }
    }

    /**
     * fire when input text is changed
     * used by a TextWatcher
     *
     * @param prev the previous result before entering ".", for action vender only
     * @param key  the text input
     * @param length count-before
     *               which means when
     *               length > 0 input
     *               length < 0 delete
     */
    public abstract void search(VenderItem prev, String key, int length);

    /**
     * fulfill result with frequency
     */
    protected void fulfillResult(VenderItem vo){
        Integer freq = mFrequentMap.get(vo.getValue());
        if(freq != null){
            vo.setFreq(freq);
            frequentItems.add(vo);
        }
    }

    /**
     * if name contains key in a way that's friendly for searching
     * e.g.
     * contains(["google", "map"], "gm") -> true
     * contains(["face", "book"], "gom") -> true
     * contains(["face", "book"], "gma") -> true
     * contains(["face", "book"], "map") -> true
     * contains(["face", "book"], "gg") -> false
     */
    protected boolean contains(String name[], String key){
        for(int i=0; i<name.length; i++){
            String str = name[i];
            char c = str.charAt(0);
            //key start with the first character of name
            //e.g. ["face", "book"], "boo" => true
            if(key.startsWith(c+"")){
                if(contains(name, key, i, true)){
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean contains(String name[], String key, int i, boolean firstTime){
        if(i >= name.length){
            return false;
        }
        String str = name[i];
        char c = str.charAt(0);
        if(key.startsWith(c+"")){
            for(int j=1; j<key.length() && j<str.length(); j++){
                //not matched, find next
                if(key.charAt(j) != str.charAt(j)){
                    String sub = key.substring(j, key.length());
                    return contains(name, sub, i+1, false);
                }
            }
            if(key.length() <= str.length()){
                return true;
            }else{
                String sub = key.substring(str.length(), key.length());
                return contains(name, sub, i+1, false);
            }
        }else{
            if(firstTime){
                return contains(name, key, i+1, true);
            }else{
                return false;
            }
        }
    }


    public void setOnResultChangedListener(OnResultChangedListener l){
        this.mOnResultChangedListener = l;
    }
    public interface OnResultChangedListener{
        public void onResultChange(TreeSet<VenderItem> list, boolean flag);
    }

    public abstract int function(VenderItem result);

    /**
     * display something in the view
     * @param msg the message to display
     */
    protected void display(String msg, int flag){
        mSearchHelper.getSearchable().onDisplay(msg + "\n", flag);
    }

    protected void setInputLock(boolean b){

    }
}
