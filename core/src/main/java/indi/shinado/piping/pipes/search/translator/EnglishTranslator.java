package indi.shinado.piping.pipes.search.translator;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.pipes.entity.SearchableName;

public class EnglishTranslator extends AbsTranslator {

    private final String[] AEIOU = new String[]{"a", "e", "i", "o", "u"};

    public EnglishTranslator(Context context) {
        super(context);
    }

    @Override
    public SearchableName getName(String name) {
        return new SearchableName(getSearchableName(name));
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean ready() {
        return true;
    }

    /**
     * split a name by rules. e.g.
     * Kakao Talk -> {ka, kao, ta, l, k}
     * KakaoTalk  -> {ka, kao, ta, l, k}
     * iKakao Talk -> {i, ka, kao, ta, l, k}
     */
    public String[] getSearchableName(String name){
        if (name == null){
            return new String[0];
        }
        ArrayList<String> names = new ArrayList<>();
        String splits[] = name.split(" ");
        for (String str : splits){
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<str.length(); i++){
                char c = str.charAt(i);
                if ((c >= 'A' && c <= 'Z') ||
                        isConsonant(c)){
                    if (sb.length() != 0){
                        names.add(sb.toString().toLowerCase());
                    }
                    sb = new StringBuilder();
                }
                sb.append((""+c).toLowerCase());
            }
            names.add(sb.toString());
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * make KakaoTalk  -> {ka, kao, ta, l, k}
     * otherwise, KakaoTalk -> {kakao, talk}
     */
    private boolean isConsonant(char c){
        return false;

//        String str = (""+c).toLowerCase();
//        for (String a : AEIOU){
//            if (str.equals(a)){
//                return false;
//            }
//        }
//        return true;
    }

}
