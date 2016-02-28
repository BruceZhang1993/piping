package shinado.indi.lib.items.search;


import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.items.search.translator.AbsTranslator;
import shinado.indi.lib.util.AppManager;


/**
 * value: "packageName,activityName"
 */
public class AppVender extends SearchVender {

    private AppManager appManager;

    public AppVender(int id) {
        super(id);
    }

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
    public int getType() {
        return VenderItem.TYPE_APPLICATION;
    }

    @Override
    public VenderItem getItem(String value) {
        return appManager.getResult(value);
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
        input("error, applications do not take input. \n input:"+input);
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {
        callback.onOutput(result.getDisplayName() + ":" + result.getValue().body);
    }

    @Override
    public void execute(VenderItem rs) {
        String body = rs.getValue().body;
        if (body.equals("android")) {
            return ;
        } else if (body.equals("search")) {
            return ;
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
    }

    private void refreshAppMessage(AbsTranslator translator) {
        appManager = AppManager.getAppManager(context, translator);
        appManager.addOnAppChangeListener(new AppManager.OnAppChangeListener() {
            @Override
            public void onAppChange(int flag, VenderItem vo) {
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
        resultStack.push(frequentItems);
    }

    public void destroy(){
        if (appManager == null){
            return;
        }
        appManager.destroy();
        appManager = null;
    }
}
