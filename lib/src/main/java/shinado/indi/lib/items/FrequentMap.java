package shinado.indi.lib.items;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.List;

public class FrequentMap{

    private HashMap<String, Item> mMap = new HashMap<>();

    public FrequentMap(){
        mMap = getFrequentMap(10);
    }

    public HashMap<String, Item> getFrequentMap(int limit){
        HashMap<String, Item> map = new HashMap<>();
        List<Item> list = new Select().all().from(Item.class).execute();
        for (Item item : list){
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
        Item val = mMap.get(key);
        if (val == null){
            Item item = new Item(key, 1);
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

        public Item(){
            super();
        }

        public Item(String key, int val){
            this.key = key;
            this.launchedTimes = val;
        }

        @Column(name = "ColKey")
        public String key;

        @Column(name = "ColValue")
        public int launchedTimes;

    }
}
