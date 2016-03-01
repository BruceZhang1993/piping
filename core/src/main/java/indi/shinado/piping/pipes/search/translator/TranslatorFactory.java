package indi.shinado.piping.pipes.search.translator;

import android.content.Context;

//import java.util.Locale;

public class TranslatorFactory {

    /**
     *
     * @param count the count of usage
     * @return
     */
    public static AbsTranslator getTranslator(Context context, int count){
        if (context == null){
            //for test
            return new EnglishTranslator(null);
        }else {

//        Locale current = context.getResources().getConfiguration().locale;
//        String displayLanguage = current.getDisplayLanguage();
//        if (displayLanguage.equals(Locale.CHINESE.getDisplayLanguage())){
            return new ChineseTranslator(context, count);
//        }
//        return new EnglishTranslator(context);
        }
    }

}
