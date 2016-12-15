package indi.shinado.piping.pipes.entity;

import java.util.Arrays;

public class SearchableName {

    private String[] name;

    /**
     * accepting no languages other than English
     * however, accepting pronunciation of other languages such as Chinese
     */
    public SearchableName(String... name){
        this.name = name;
    }

    public String[] getNames(){
        return name;
    }

    public String toString(){
        return Arrays.toString(name);
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
        key = removeSpace(key).toLowerCase();
        for (int i = 0; i < name.length; i++) {
            String str = name[i].toLowerCase();
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
        String str = name[i].toLowerCase();
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
                    //commenting firstTime&& below makes searching constant
                    //ok I know you are expecting an "e.g.", then let's make an "e.g.":
                    //for item "facebook", since it's split as ["fa", "ce", "boo", "k"]
                    //by commenting firstTime&&, contains("fb") returns false, contains("fc") returns true
                    //otherwise, they both return true
//                    firstTime &&
                            contains(name, key, i + 1, true);

        }
    }

}
