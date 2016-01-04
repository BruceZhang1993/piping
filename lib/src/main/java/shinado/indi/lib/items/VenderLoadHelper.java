package shinado.indi.lib.items;

import android.content.Context;

import java.util.Hashtable;

import shinado.indi.lib.items.action.CopyVender;
import shinado.indi.lib.items.action.InstallVender;
import shinado.indi.lib.items.action.SearchActionVender;
import shinado.indi.lib.items.action.TaskVender;
import shinado.indi.lib.items.search.AppVender;
import shinado.indi.lib.items.search.ContactVender;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.items.search.translator.TranslatorFactory;
import shinado.indi.lib.launcher.SearchHelper;

/**
 * Created by Administrator on 2015/12/28.
 */
public class VenderLoadHelper {
    
    public static  Hashtable<Integer, BaseVender> load(SearchHelper helper, Context context){
        Hashtable<Integer, BaseVender> functionMap = new Hashtable<>();

        AbsTranslator translator = TranslatorFactory.getTranslator(context, 2);

        AppVender appFunction = new AppVender();
        appFunction.init(context, helper, BaseVender.TYPE_APP);
        appFunction.load(translator);
        functionMap.put(BaseVender.TYPE_APP, appFunction);

        ContactVender contactFunction = new ContactVender();
        contactFunction.init(context, helper, BaseVender.TYPE_CONTACT);
        contactFunction.load(translator);
        functionMap.put(BaseVender.TYPE_CONTACT, contactFunction);

        InstallVender insFunction = new InstallVender();
        insFunction.init(context, helper, VenderItem.BUILD_IN_ID_INSTALL);
        functionMap.put(VenderItem.BUILD_IN_ID_INSTALL, insFunction);

        CopyVender cpFunction = new CopyVender();
        cpFunction.init(context, helper, VenderItem.BUILD_IN_ID_COPY);
        functionMap.put(VenderItem.BUILD_IN_ID_COPY, cpFunction);

        SearchActionVender searchFunction = new SearchActionVender();
        searchFunction.init(context, helper, VenderItem.BUILD_IN_ID_SEARCH);
        functionMap.put(VenderItem.BUILD_IN_ID_SEARCH, searchFunction);

        TaskVender taskFunction = new TaskVender();
        taskFunction.init(context, helper, VenderItem.BUILD_IN_ID_TASKS);
        functionMap.put(VenderItem.BUILD_IN_ID_TASKS, taskFunction);

        return functionMap;
    }
    
}
