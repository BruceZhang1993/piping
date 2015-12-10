package shinado.indi.lib.items.search.translator;

import android.content.Context;

import java.util.Locale;

/**
 * Created by Administrator on 2015/10/28.
 */
public class TranslatorFactory {

    public static AbsTranslator getTranslator(Context context){
        Locale current = context.getResources().getConfiguration().locale;
        String displayLanguage = current.getDisplayLanguage();
        if (displayLanguage.equals(Locale.CHINESE.getDisplayLanguage())){
            return new ChineseTranslator(context);
        }
        return new EnglishTranslator(context);
    }

}
