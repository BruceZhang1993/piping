package shinado.indi.lib.items.search.translator;

import android.content.Context;

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
    public abstract String[] getName(String name);

}
