package com.shinado.piping.geek;

import android.content.Context;
import android.view.LayoutInflater;

import com.activeandroid.query.Select;
import com.shinado.piping.geek.header.HeadEntity;
import com.shinado.piping.geek.header.IHeadView;

import java.io.File;
import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;
import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.pipes.BasePipe;
import shinado.indi.vender.R;

/**
 * Created by shinado on 16/5/11.
 */
public class HeadLoadder {

    public static IHeadView load(Context context) {
        return null;

//        HeadEntity entity = new Select().from(HeadEntity.class).where("selected = 1").executeSingle();
//        if (entity == null){
//            return new ShoppingHeadView();
//        }
//
//        File dexOutputDir = context.getDir("outdex", 0);
//        DexClassLoader classLoader = new DexClassLoader(GlobalDefs.PATH_HOME + entity.getFileName(),
//                dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
//        IHeadView headView = null;
//        try {
//            Class<?> loadClass = classLoader.loadClass(entity.className);
//            Constructor ctr = loadClass.getConstructor();
//            headView = (IHeadView) ctr.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return headView;
    }

}
