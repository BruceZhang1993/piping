package indi.shinado.piping.util;

import java.io.File;

/**
 * Created by Administrator on 2015/11/9.
 */
public class FileUtil {

    public static void checkDir(String dir){
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()){
            file.mkdir();
        }
    }

}
