package com.shinado.piping.geek.header;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import indi.shinado.piping.download.Downloadable;

@Table(name = "THeadEntity")
public class HeadEntity extends Model implements Downloadable{

    public HeadEntity() {
        selected = 0;
    }

    public HeadEntity(int sid, String url, String name, String author, String pkg, String className) {
        this();
        this.sid = sid;
        this.url = url;
        this.name = name;
        this.author = author;
        this.pkg = pkg;
        this.className = className;
    }

    @Column(name = "cId")
    public int sid;

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

    @Column(name = "imageUrl")
    public String imgUrl;

    @Column(name = "selected")
    public int selected;

    @Column(name = "price")
    public float price;

    public boolean isLocal = false;

    public int downloadProgress = 0;

    public boolean isDownloading = false;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getFileName() {
        return name + ".dex";
    }
}
