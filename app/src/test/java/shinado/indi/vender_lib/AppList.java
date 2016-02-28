package shinado.indi.vender_lib;

import java.util.ArrayList;

import shinado.indi.lib.items.VenderItem;

/**
 * Created by shinado on 2016/2/24.
 */
public class AppList {

    public static ArrayList<VenderItem> getFakeList(){
        ArrayList<VenderItem> list = new ArrayList<>();
        list.add(new VenderItem(null, "", new String[]{"Kakao", "Talk"}, 1));
        list.add(new VenderItem(null, "", new String[]{"KT", "Player"}, 2));
        list.add(new VenderItem(null, "", new String[]{"Talk", "Box"}, 3));
        list.add(new VenderItem(null, "", new String[]{"wk", "kat"}, 4));
        list.add(new VenderItem(null, "", new String[]{"wka", "wka"}, 5));
        list.add(new VenderItem(null, "", new String[]{"X", "Browser"}, 6));
        return list;
    }

}
