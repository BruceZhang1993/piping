package shinado.indi.lib.items.action;

import java.util.ArrayList;
import java.util.HashMap;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import shinado.indi.lib.download.DownloadImpl;
import shinado.indi.lib.download.Downloadable;
import shinado.indi.lib.items.Vender;
import shinado.indi.lib.items.VenderItem;
import shinado.indi.lib.launcher.Searchable;

public class InstallVender extends ActionVender {

    private VenderItem mResult;
    private DownloadImpl mLoader;
    private static final String URL_LIST = "http://1.yilaunch.sinaapp.com/install/list.php";
    private static final String URL_INSTALL = "http://1.yilaunch.sinaapp.com/install/search.php";

    private static final String OPT_LS = "-ls";
    private static final String OPT_F  = "-f";
    private static final String OPT_M  = "-m";

    private static final String HELP = "Usage of install:\n" +
            "[key].install [-option]\n" +
            "where option includes:\n" +
            OPT_LS + " list all functions available\n" +
            OPT_F  + " find items\n" +
            OPT_M  + " install whatever matches";

    public InstallVender() {
        mResult = new VenderItem();
        mResult.setId(VenderItem.BUILD_IN_ID_INSTALL);
        mResult.setDisplayName(".install");
        mResult.setValue("");
        mResult.setName(new String[]{".", "in", "s", "tall"});
        setupDownload();
    }

    private void setupDownload(){
        mLoader = new DownloadImpl();
        mLoader.setOnDownloadListener(new DownloadImpl.OnDownloadListener() {
            @Override
            public void onStart(Downloadable v) {
                mSearchHelper.blockInput();
            }

            @Override
            public void onFinish(Downloadable v) {
                display("Installing...", Searchable.FLAG_INPUT);
                mSearchHelper.releaseInput();
                mSearchHelper.addNewVender(context, (Vender) v);
            }

            @Override
            public void onFail(Downloadable v) {
                display("Failed", Searchable.FLAG_INPUT);
                mSearchHelper.releaseInput();
            }

            @Override
            public void onProgress(Downloadable v, int prgs, int current) {
                StringBuilder sb = new StringBuilder(" ");
                int progressIn10 = prgs/10;
                for (int i=0; i<progressIn10; i++){
                    sb.append("▅");
                }
                for (int i=0; i<10; i++){
                    sb.append("　");
                }
                sb.append("　");
                sb.append(prgs);
                sb.append("%");
                display(sb.toString(), Searchable.FLAG_REPLACE);
            }
        });
    }

    @Override
    protected VenderItem getResult() {
        return mResult;
    }

    @Override
    protected VenderItem filter(VenderItem result) {
        return result;
    }

    @Override
    public int function(VenderItem result) {
        String value = result.getValue();
        if (value == null || value.trim().isEmpty()){
            display(HELP, Searchable.FLAG_INPUT);
        }else{
            handleWithOption(value);
        }
        return 0;
    }

    private void handleWithOption(String ins){
        mSearchHelper.blockInput();
        display("loading...", Searchable.FLAG_INPUT);

        HashMap<String, String> params = new HashMap<>();
        String[] split = ins.split(" ", 2);
        //no key provided
        if (split[0].contains("-")){
            params.put("option", ins);
            requestList(params);
        }
        //with key
        else {
            String name = split[0];
            params.put("name", name);
            if (split.length > 1){
                String option = split[1];
                params.put("option", option);
                if (option.contains(OPT_LS)){
                    requestList(params);
                    return;
                }
            }
            requestInstall(params);
        }
    }

    private void requestInstall(HashMap<String, String> params){
        new VolleyProvider().handleData(URL_INSTALL, params, Vender.class,
                new Listener.Response<Vender>() {

                    @Override
                    public void onResponse(Vender obj) {
                        mSearchHelper.releaseInput();
                        if (obj != null) {
                            mLoader.addToQueue(obj);
                            mLoader.start();
                        } else {
                            display("Item not found", Searchable.FLAG_INPUT);
                        }
                    }
                },
                mDefaultError);
    }

    private void requestList(HashMap<String, String> params){
        new VolleyProvider().handleData(URL_LIST, params, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        String msg = constructMessage(obj);
                        mSearchHelper.releaseInput();
                        display(msg, Searchable.FLAG_INPUT);
                    }
                },
                mDefaultError);
    }

    private Listener.Error mDefaultError = new Listener.Error() {
        @Override
        public void onError(String msg) {
            mSearchHelper.releaseInput();
            display(msg, Searchable.FLAG_INPUT);
        }
    };

    private String constructMessage(Result rs){
        StringBuilder sb = new StringBuilder();
        for (Vender f : rs.list){
            sb.append(f.name);
            sb.append("-");
            sb.append(f.pkg);
            sb.append("\n");
        }
        return sb.toString();
    }

    public class Result{
        public ArrayList<Vender> list;
    }
}
