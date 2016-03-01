package indi.shinado.piping.pipes.search.translator;

import android.content.Context;
import android.util.Log;

import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.translator.chinese.PinyinHelper;


public class ChineseTranslator extends EnglishTranslator{

    PinyinHelper helper;
    private int mCount = 0;

    public ChineseTranslator(final Context context, int count){
        super(context);
        mCount = count;
        new Thread(){
            @Override
            public void run() {
                Log.d("ChineseTranslator", "get translator end");
                helper = new PinyinHelper(context);
                Log.d("ChineseTranslator", "get translator end");
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
        if (--mCount == 0){
            if (helper != null){
                helper.destroy();
                helper = null;
            }
        }
    }

    @Override
    public boolean ready(){
        return helper != null;
    }
}
