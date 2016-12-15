package indi.shinado.piping.pipes.search.translator;

import android.content.Context;
import android.util.Log;

import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.translator.chinese.PinyinHelper;


public class ChineseTranslator extends EnglishTranslator{

    PinyinHelper helper;

    public ChineseTranslator(final Context context){
        super(context);
        new Thread(){
            @Override
            public void run() {
                helper = new PinyinHelper(context);
            }
        }.start();
    }

    @Override
    public SearchableName getName(String name) {
        if (helper == null){
            return null;
        }
        String[] result = helper.convertToPinyinArray(name);
        return result == null ? super.getName(name) : new SearchableName(result);
    }

    @Override
    public void destroy() {
        super.destroy();
            if (helper != null){
                helper.destroy();
                helper = null;
            }
    }

    @Override
    public boolean ready(){
        return helper != null;
    }
}
