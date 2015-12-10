package shinado.indi.lib.items;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.List;

public class FrequentMap{

    private HashMap<String, Item> mMap = new HashMap<>();

    public HashMap<String, Item> getFrequentMap(int limit){
        List<Item> list = new Select().all().from(Item.class).limit(limit).execute();
        for (Item item : list){
            mMap.put(item.key, item);
        }
        return mMap;
    }

    /**
     *
     * @param key
     * @return true if exists
     */
    public boolean addFrequency(String key){
        Item val = mMap.get(key);
        if (val == null){
            new Item(key, 1).save();
            return false;
        }else{
            val.launchedTimes++;
            val.save();
            return true;
        }
    }

    public void remove(String key){
        Item val = mMap.get(key);
        if (val != null){
            val.delete();
        }
    }

    public Integer get(String key){
        Item item = mMap.get(key);
        return item == null ? null : item.launchedTimes;
    }

    @Table(name = "Tfrequent")
    public class Item extends Model{

        public Item(){}

        public Item(String key, int val){
            this.key = key;
            this.launchedTimes = val;
        }

        @Column(name = "Ckey")
        public String key;

        @Column(name = "ClaunchedTime")
        public int launchedTimes;

    }
}
