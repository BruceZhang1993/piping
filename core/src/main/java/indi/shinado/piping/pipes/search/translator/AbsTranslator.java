package indi.shinado.piping.pipes.search.translator;

import android.content.Context;

import indi.shinado.piping.pipes.entity.SearchableName;

/**
 * Created by Administrator on 2015/10/28.
 */
public abstract class AbsTranslator {

    public AbsTranslator(Context context){
    }

    /**
     * get the split name for name in lower case
     * e.g.
     * Kakao       -> {kakao}
     * KakaoTalk   -> {kakao, talk}
     * reKakao     -> {re, kakao}
     * iKakao Talk -> {i,kakao, talk}
     * @param name
     * @return
     */
    public abstract SearchableName getName(String name);
    public abstract void destroy();
    public abstract boolean ready();
}
