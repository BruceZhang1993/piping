package shinado.indi.lib.items;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by Administrator on 2015/12/6.
 */
public class VenderFactory {

    public static BaseVender getFunction(Context context, Vender f){
        File dexOutputDir = context.getDir("outdex", 0);// ???/data/data/packageName/app_myxxx
        DexClassLoader classLoader = new DexClassLoader(f.url,
                dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        BaseVender function = null;
        try {
            Class<?> loadClass = classLoader.loadClass(f.className);
            function = (BaseVender) loadClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return function;
    }

}
