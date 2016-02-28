package shinado.indi.vender_lib;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import shinado.indi.lib.items.BaseVender;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.action.ActionVender;
import shinado.indi.lib.items.action.NoteVender;
import shinado.indi.lib.items.search.SearchVender;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.items.search.translator.TranslatorFactory;
import shinado.indi.lib.launcher.SearchHelper;

/**
 * Created by shinado on 2016/1/15.
 * TODO
 */
@RunWith(MockitoJUnitRunner.class)
public class TestActionVender {


    private NoteVender vender;

    @Before
    public void setup() {

        vender = new NoteVender(10);
    }

    @Test
    public void testGetValue(){
        String[] name = new String[]{".", "mt"};
        Assert.assertEquals(vender.getValue("", null), "");
    }
}
