package indi.shinado.piping.pipes;

import java.util.ArrayList;

public class SearchableName {

    private final String[] AEIOU = new String[]{"a", "e", "i", "o", "u"};
    private String[] name;

    /**
     * accepting no languages other than English
     */
    public SearchableName(String name){
        this.name = getNames(name);
    }

    /**
     * accepting no languages other than English
     * however, accepting pronunciation of other languages such as Chinese
     */
    public SearchableName(String[] name){
        this.name = name;
    }

    /**
     * split a name by rules. e.g.
     * Kakao Talk -> {ka, kao, ta, l, k}
     * KakaoTalk  -> {ka, kao, ta, l, k}
     * iKakao Talk -> {i, ka, kao, ta, l, k}
     */
    public String[] getNames(String name){
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

    private boolean isConsonant(char c){
        String str = (""+c).toLowerCase();
        for (String a : AEIOU){
            if (str.equals(a)){
                return false;
            }
        }
        return true;
    }

    public String toString(){
        return name.toString();
    }

    /**
     * if name contains key in a way that"s friendly for searching
     * e.g.
     * contains(["google", "map"], "gm") -> true
     * contains(["google", "map"], "gom") -> true
     * contains(["google", "map"], "gma") -> true
     * contains(["google", "map"], "map") -> true
     * contains(["google", "map"], "gg") -> false
     */
    public boolean contains(String key) {
        key = removeSpace(key);
        for (int i = 0; i < name.length; i++) {
            String str = name[i];
            char c = str.charAt(0);
            //key start with the first character of name
            //e.g. ["face", "book"], "boo" => true
            if (key.startsWith(c + "")) {
                if (contains(name, key, i, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String removeSpace(String key){
        return key.replace(" ", "");
    }

    private boolean contains(String name[], String key, int i, boolean firstTime) {
        if (i >= name.length) {
            return false;
        }
        String str = name[i];
        char c = str.charAt(0);
        if (key.startsWith(c + "")) {
            for (int j = 1; j < key.length() && j < str.length(); j++) {
                //not matched, find next
                if (key.charAt(j) != str.charAt(j)) {
                    String sub = key.substring(j, key.length());
                    return contains(name, sub, i + 1, false);
                }
            }
            if (key.length() <= str.length()) {
                return true;
            } else {
                String sub = key.substring(str.length(), key.length());
                return contains(name, sub, i + 1, false);
            }
        } else {
            return
                    //removing the comment below makes searching constant
                    //ok I know you are expecting an "e.g.", then let's make an "e.g.":
                    //for item "facebook", since it's split as ["fa", "ce", "boo", "k"]
                    //by removing the comment, contains("fb") returns false, contains("fc") returns true
                    //otherwise, they both return true
//                    firstTime &&
                            contains(name, key, i + 1, true);

        }
    }

}
