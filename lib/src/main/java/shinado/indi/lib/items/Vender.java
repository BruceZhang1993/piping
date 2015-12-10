package shinado.indi.lib.items;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import shinado.indi.lib.download.Downloadable;

/**
 * Created by Administrator on 2015/12/6.
 */
@Table(name = "Tvender")
public class Vender extends Model implements Downloadable{

    @Column(name = "cId")
    public int sid;

    public Vender() {
    }

    public Vender(int sid, String url, String name, String author, String pkg, String className) {
        this.sid = sid;
        this.url = url;
        this.name = name;
        this.author = author;
        this.pkg = pkg;
        this.className = className;
    }

    //e.g :
    // /storage/0/nix/function/weather.jar
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

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getFileName() {
        return pkg + ".apk";
    }
}
