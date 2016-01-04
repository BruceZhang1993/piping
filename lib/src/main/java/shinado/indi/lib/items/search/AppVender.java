package shinado.indi.lib.items.search;

import android.content.Context;

import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.launcher.SearchHelper;
import shinado.indi.lib.util.AppManager;


/**
 * value: "packageName,activityName"
 */
public class AppVender extends SearchVender {

    private AppManager appManager;

    public void load(final AbsTranslator translator) {
        new Thread() {
            public void run() {
                while (!translator.ready()) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                refreshAppMessage(translator);
            }
        }.start();
    }

    @Override
    public VenderItem getItem(String value) {
        return appManager.getResult(value);
    }

    @Override
    public int function(VenderItem rs) {
        String body = rs.getValue();
        if (body.equals("android")) {
            return -1;
        } else if (body.equals("search")) {
            return 1;
        }
        try {
            appManager.launch(body);
            addFrequency(rs);
        } catch (Exception e) {
            //if there is any trouble, remove it
            e.printStackTrace();
            removeFrequency(rs);
            removeItemInMap(rs);
        }
        return 2;
    }

    private void refreshAppMessage(AbsTranslator translator) {
        appManager = AppManager.getAppManager(context, translator);
        appManager.addOnAppChangeListener(new AppManager.OnAppChangeListener() {
            @Override
            public void onAppChange(int flag, VenderItem vo) {
                System.out.println("appChange:search:" + flag + "," + vo.getValue());
                if (flag == AppManager.OnAppChangeListener.FLAG_ADD) {
                    putItemInMap(vo);
                    addFrequency(vo);
                } else if (flag == AppManager.OnAppChangeListener.FLAG_REMOVE) {
                    removeItemInMap(vo);
                    removeFrequency(vo);
                }
            }
        });
        for (int i = 0; i < appManager.getAppCount(); i++) {
            VenderItem vo = appManager.getResult(i);
            fulfillResult(vo);
            putItemInMap(vo);
        }
        appManager.destroy();
        resultStack.push(frequentItems);
    }

}
