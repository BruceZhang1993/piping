package indi.shinado.piping.pipes.impl.search;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.FrequentPipe;
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
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

        PackageManager pm = context.getPackageManager();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, input);

        //looking for shareable apps
        List<ResolveInfo> list = pm.queryIntentActivities(sharingIntent, 0);
        String[] split = result.getExecutable().split(",");
        String pkg = split[0];
        for (ResolveInfo info : list){
            if (pkg.equals(info.activityInfo.packageName)){
                sharingIntent.setComponent(new ComponentName(split[0], info.activityInfo.name));
                context.startActivity(sharingIntent);
                return;
            }
        }

        sharingIntent.setComponent(new ComponentName(split[0], split[1]));
        context.startActivity(sharingIntent);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput(result.getExecutable());
    }

    @Override
    protected void execute(Pipe rs) {
        String body = rs.getExecutable();
        if ("android".equals(body)) {
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
        appManager = AppManager.getInstance(context, translator);
        appManager.register();
        new Thread() {
            public void run() {
                appManager.loadApps();
                appManager.addOnAppChangeListener(new AppManager.OnAppChangeListener() {
                    @Override
                    public void onAppChange(int flag, Pipe vo) {
                        if (flag == AppManager.OnAppChangeListener.FLAG_ADD) {
                            vo.setBasePipe(ApplicationPipe.this);
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

    @Override
    public void destroy() {
        if (appManager != null){
            appManager.destroy();
            appManager = null;
        }
    }

    public Pipe getByPackageName(String pkg){
        Pipe item = appManager.getResult(pkg + ",", true);
        item.setBasePipe(this);
        item.setInstruction(new Instruction(""));
        return item;
    }
}
