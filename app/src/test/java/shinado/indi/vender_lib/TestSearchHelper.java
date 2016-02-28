package shinado.indi.vender_lib;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import shinado.indi.lib.items.FrequentMap;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.translator.EnglishTranslator;
import shinado.indi.lib.items.search.translator.TranslatorFactory;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.util.AppManager;

import static org.junit.Assert.assertEquals;

/**
 * Created by shinado on 2016/2/24.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSearchHelper {

    SearchHelper helper;

    Context context = null;

    @Before
    public void setup(){
        FrequentMap frequentMap = Mockito.mock(FrequentMap.class);
        Mockito.when(frequentMap.getFrequentMap(Mockito.anyInt())).thenReturn(new HashMap<String, FrequentMap.Item>());
        frequentMap.getFrequentMap(10);
        AppManager appManager = Mockito.mock(AppManager.class);
        ArrayList<VenderItem> list = AppList.getFakeList();
        Mockito.when(appManager.getAppCount()).thenReturn(list.size());
        int i = 0;
        for (VenderItem item : list){
            Mockito.when(appManager.getResult(i++)).thenReturn(item);
        }
        helper = new SearchHelper(context, null);
    }

    @Test
    public void testSearch(){
        TreeSet<VenderItem> list = helper.search("k", 0, 1);
        assertEquals(list.size(), 1);
    }
}
