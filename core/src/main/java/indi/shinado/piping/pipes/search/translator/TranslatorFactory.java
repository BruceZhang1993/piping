package indi.shinado.piping.pipes.search.translator;

import android.content.Context;

//import java.util.Locale;

public class TranslatorFactory {

    public static AbsTranslator getTranslator(Context context){
        if (context == null){
            //for test
            return new EnglishTranslator(null);
        }else {

//        Locale current = baseLauncherView.getResources().getConfiguration().locale;
//        String displayLanguage = current.getDisplayLanguage();
//        if (displayLanguage.equals(Locale.CHINESE.getDisplayLanguage())){
            return new ChineseTranslator(context);
//        }
//        return new EnglishTranslator(baseLauncherView);
        }
    }

}
