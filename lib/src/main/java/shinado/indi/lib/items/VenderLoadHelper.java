package shinado.indi.lib.items;

import android.content.Context;

import java.util.ArrayList;
import java.util.Hashtable;

import shinado.indi.lib.items.action.CopyVender;
import shinado.indi.lib.items.action.InstallVender;
import shinado.indi.lib.items.action.MightyVender;
import shinado.indi.lib.items.action.NoteVender;
import shinado.indi.lib.items.action.SearchActionVender;
import shinado.indi.lib.items.search.AppVender;
import shinado.indi.lib.items.search.ContactVender;
import shinado.indi.lib.items.search.SearchVender;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.items.search.translator.TranslatorFactory;
import shinado.indi.lib.launcher.SearchHelper;

/**
 * Created by Administrator on 2015/12/28.
 */
public class VenderLoadHelper {

    private ArrayList<BaseVender> venders;

    public VenderLoadHelper() {
        venders = new ArrayList<>();
        venders.add(new AppVender(VenderItem.BUILD_IN_ID_APP));
        venders.add(new ContactVender(VenderItem.BUILD_IN_ID_CONTACT));
        venders.add(new InstallVender(VenderItem.BUILD_IN_ID_INSTALL));
        venders.add(new CopyVender(VenderItem.BUILD_IN_ID_COPY));
        venders.add(new SearchActionVender(VenderItem.BUILD_IN_ID_SEARCH));
        venders.add(new MightyVender(VenderItem.BUILD_IN_ID_MIGHTY));
        venders.add(new NoteVender(10));
    }
    
    public Hashtable<Integer, BaseVender> load(SearchHelper helper, Context context){
        Hashtable<Integer, BaseVender> functionMap = new Hashtable<>();

        AbsTranslator translator = TranslatorFactory.getTranslator(context, 2);

        for (BaseVender vender : venders){
            vender.init(context, helper);
            if (vender instanceof SearchVender){
                ((SearchVender)vender).load(translator);
            }
            functionMap.put(vender.getId(), vender);
        }

        return functionMap;
    }
    
}
