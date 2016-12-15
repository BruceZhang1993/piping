package indi.shinado.piping.storage;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseStorage {

    private SharedPreferences spf;

    public BaseStorage(Context context) {
        spf = context.getSharedPreferences("base", Context.MODE_PRIVATE);
    }

    public void put(String key, String value){
        spf.edit().putString(key, value).apply();
    }

    public void get(String key, String defaultValue){
        spf.getString(key, defaultValue);
    }

}
