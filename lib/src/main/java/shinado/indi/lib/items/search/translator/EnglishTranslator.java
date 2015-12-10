package shinado.indi.lib.items.search.translator;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/28.
 */
public class EnglishTranslator extends AbsTranslator {

    public EnglishTranslator(Context context) {
        super(context);
    }

    @Override
    public String[] getName(String name) {
        return splitEnglish(name);
    }

    //all English
    //Kakao Talk -> {kakao, talk}
    //KakaoTalk  -> {kakao, talk}
    //iKakao Talk -> {i, kakao, talk}
    private String[] splitEnglish(String str){
        ArrayList<String> names = new ArrayList<String>();
        String splits[] = str.split(" ");
        for (String name : splits){
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<name.length(); i++){
                char c = name.charAt(i);
                if (c >= 'A' && c <= 'Z'){
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

}
