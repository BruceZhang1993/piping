package indi.shinado.piping.pipes.entity;

import com.activeandroid.Model;

import indi.shinado.piping.download.Downloadable;

/**
 * Created by shinado on 2016/3/2.
 */
public class PipeEntity extends Model implements Downloadable{

    public int sid;

    public PipeEntity() {
    }

    public PipeEntity(int sid, String url, String name, String author, String pkg, String className) {
        this.sid = sid;
        this.url = url;
        this.name = name;
        this.author = author;
        this.pkg = pkg;
        this.className = className;
    }

    //e.g :
    // /storage/0/nix/execute/weather.dex
    public String url;

    public String name;

    public String author;

    public String pkg;

    public String className;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getFileName() {
        return name + ".dex";
    }
}
