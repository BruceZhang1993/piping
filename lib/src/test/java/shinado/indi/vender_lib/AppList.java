package shinado.indi.vender_lib;

import java.util.ArrayList;

import shinado.indi.lib.items.VenderItem;

/**
 * Created by shinado on 2016/2/24.
 */
public class AppList {

    public static ArrayList<VenderItem> getFakeList(){
        ArrayList<VenderItem> list = new ArrayList<>();
        list.add(new VenderItem(null, "kakao talk", new String[]{"kakao", "talk"}, 1, VenderItem.TYPE_APPLICATION));
        list.add(new VenderItem(null, "kt player", new String[]{"kt", "player"}, 2, VenderItem.TYPE_APPLICATION));
        list.add(new VenderItem(null, "talk box", new String[]{"talk", "box"}, 3, VenderItem.TYPE_APPLICATION));
        list.add(new VenderItem(null, "wk kat", new String[]{"wk", "kat"}, 4, VenderItem.TYPE_APPLICATION));
        list.add(new VenderItem(null, "wka wka", new String[]{"wka", "wka"}, 5, VenderItem.TYPE_APPLICATION));
        list.add(new VenderItem(null, "XBrowser", new String[]{"x", "browser"}, 6, VenderItem.TYPE_APPLICATION));
        return list;
    }

    public static int[] getKeyIndex(String key){
        switch (key){
            case "k":
                return new int[]{0, 0, 2, 1, 2, 2};
        }
        return new int[]{};
    }
}
