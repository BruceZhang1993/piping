package indi.shinado.piping.pipes.impl;

import com.google.gson.Gson;

import java.util.HashMap;

public class ShareIntent {
    public String type;
    public HashMap<String, String> extras = new HashMap<>();

    public ShareIntent(String type) {
        this.type = type;
    }

    public ShareIntent() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
