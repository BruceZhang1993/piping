package shinado.indi.lib.items;

/**
 * Created by shinado on 2016/2/24.
 */
public class CompareUtil {

    /**
     * if name contains key in a way that's friendly for searching
     * e.g.
     * contains(["google", "map"], "gm") -> true
     * contains(["face", "book"], "gom") -> true
     * contains(["face", "book"], "gma") -> true
     * contains(["face", "book"], "map") -> true
     * contains(["face", "book"], "gg") -> false
     */
    public static boolean contains(String name[], String key) {
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

    private static boolean contains(String name[], String key, int i, boolean firstTime) {
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
            return firstTime && contains(name, key, i + 1, true);
        }
    }

}
