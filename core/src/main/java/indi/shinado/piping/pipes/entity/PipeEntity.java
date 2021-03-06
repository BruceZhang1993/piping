package indi.shinado.piping.pipes.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import indi.shinado.piping.download.Downloadable;

@Table(name = "TPipeEntity")
public class PipeEntity extends Model implements Downloadable{

    @Column(name = "cId")
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
    @Column(name = "cUrl")
    public String url;

    @Column(name = "cName")
    public String name;

    @Column(name = "cAuthor")
    public String author;

    @Column(name = "cPackage")
    public String pkg;

    @Column(name = "cClassName")
    public String className;

    @Column(name = "cVersion")
    public int targetVersion;

    @Column(name = "cSize")
    public String size;

    @Column(name = "cIntro")
    public String introduction;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getFileName() {
        return name + ".dex";
    }
}
