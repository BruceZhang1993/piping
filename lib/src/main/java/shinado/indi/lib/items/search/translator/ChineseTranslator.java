package shinado.indi.lib.items.search.translator;

import android.content.Context;

import shinado.indi.lib.items.search.translator.chinese.PinyinHelper;

public class ChineseTranslator extends EnglishTranslator{

    PinyinHelper helper;

    public ChineseTranslator(Context context){
        super(context);
        helper = PinyinHelper.getInstance(context);
    }

    @Override
    public String[] getName(String name) {
        String[] result = helper.convertToPinyinArray(name);
        return result == null ? super.getName(name) : result;
    }

}
