package indi.shinado.piping.pipes.search;

import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.List;

public class FrequentMap {

    private HashMap<String, FrequentItem> mMap = new HashMap<>();

    public FrequentMap(){
        mMap = getFrequentMap(10);
    }

    public HashMap<String, FrequentItem> getFrequentMap(int limit){
        HashMap<String, FrequentItem> map = new HashMap<>();
        List<FrequentItem> list = new Select().all().from(FrequentItem.class).execute();
        for (FrequentItem item : list){
            map.put(item.key, item);
        }
        return map;
    }

    /**
     *
     * @param key
     * @return true if exists
     */
    public boolean addFrequency(String key){
        FrequentItem val = mMap.get(key);
        if (val == null){
            FrequentItem item = new FrequentItem(key, 1);
            mMap.put(key, item);
            item.save();
            return false;
        }else{
            val.launchedTimes++;
            val.save();
            return true;
        }
    }

    public void remove(String key){
        FrequentItem val = mMap.get(key);
        if (val != null){
            val.delete();
        }
    }

    public Integer get(String key){
        FrequentItem item = mMap.get(key);
        return item == null ? null : item.launchedTimes;
    }

}
