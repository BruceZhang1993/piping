package indi.shinado.piping.pipes.impl.search;

import android.content.ComponentName;
import android.content.Intent;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.FrequentPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.util.android.AppManager;

public class ApplicationPipe extends FrequentPipe{

    private AppManager appManager;

    public ApplicationPipe(int id) {
        super(id);
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                while (!translator.ready()) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                refreshAppMessage(translator, listener, total);
            }
        }.start();
    }

    @Override
    public void acceptInput(Pipe result, String input) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, input);
        String[] split = result.getExecutable().split(",");
        sendIntent.setComponent(new ComponentName(split[0], split[1]));
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput(result.getExecutable());
    }

    @Override
    protected void execute(Pipe rs) {
        String body = rs.getExecutable();
        if (body.equals("android")) {
            return;
        } else if (body.equals("search")) {
            return;
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

    private void refreshAppMessage(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                appManager = AppManager.getAppManager(context, translator);
                appManager.addOnAppChangeListener(new AppManager.OnAppChangeListener() {
                    @Override
                    public void onAppChange(int flag, Pipe vo) {
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
                    Pipe vo = appManager.getResult(i);
                    vo.setBasePipe(ApplicationPipe.this);
                    putItemInMap(vo);
                }
                listener.onItemsLoaded(ApplicationPipe.this.getId(), total);
            }
        }.start();

    }

    public void destroy() {
        if (appManager == null) {
            return;
        }
        appManager.destroy();
        appManager = null;
    }
}
