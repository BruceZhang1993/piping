package shinado.indi.lib.items;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;
import shinado.indi.lib.GlobalDefs;

public class VenderFactory {

    public static BaseVender getFunction(Context context, Vender f){
        File dexOutputDir = context.getDir("outdex", 0);
        DexClassLoader classLoader = new DexClassLoader(GlobalDefs.PATH_HOME + f.getFileName(),
                dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        BaseVender function = null;
        try {
            Class<?> loadClass = classLoader.loadClass(f.className);
            Constructor ctr = loadClass.getConstructor(Integer.class);
            function = (BaseVender) ctr.newInstance(f.sid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return function;
    }

}
