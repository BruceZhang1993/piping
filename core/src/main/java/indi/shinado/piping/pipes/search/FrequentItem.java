package indi.shinado.piping.pipes.search;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Tfrequent")
public class FrequentItem extends Model{

    public FrequentItem(){
        super();
    }

    public FrequentItem(String key, int val){
        this.key = key;
        this.launchedTimes = val;
    }

    @Column(name = "ColKey")
    public String key;

    @Column(name = "ColValue")
    public int launchedTimes;

}
