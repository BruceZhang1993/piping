package shinado.indi.lib.items.action;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.HashMap;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import shinado.indi.lib.download.DownloadImpl;
import shinado.indi.lib.download.Downloadable;
import shinado.indi.lib.items.Vender;
import shinado.indi.lib.items.VenderItem;

public class InstallVender extends ActionVender {

    private VenderItem mResult;
    private DownloadImpl mLoader;
    private static final String URL_LIST = "http://1.yilaunch.sinaapp.com/install/list.php";
    private static final String URL_INSTALL = "http://1.yilaunch.sinaapp.com/install/search.php";

    private static final String OPT_LS = "ls";
    private static final String OPT_F = "f";
    private static final String OPT_M = "m";

    private static final String HELP = "Usage of install:\n" +
            "[key].install [-option]\n" +
            "where option includes:\n" +
            VenderItem.INDICATOR + OPT_LS + " list all functions available\n" +
            VenderItem.INDICATOR + OPT_F + " find items\n" +
            VenderItem.INDICATOR + OPT_M + " install whatever matches";

    public InstallVender(int id) {
        super(id);
        mResult = new VenderItem();
        mResult.setId(id);
        mResult.setDisplayName(".install");
        mResult.setName(new String[]{".", "in", "s", "tall"});
        setupDownload();
    }

    @Override
    public void acceptInput(VenderItem result, String input) {
        input("install does not accept input");
    }

    @Override
    public void getOutput(VenderItem result, OutputCallback callback) {
        VenderItem.Value value = result.getValue();
        mSearchHelper.blockInput();
        input("loading...");

        HashMap<String, String> params = new HashMap<>();

        if (value.isEmpty()) {
            //.install
            callback.onOutput(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //trans.install
                params.put("name", value.pre);
            }else {
                //trans.install -m
                //.install -ls
                String option = value.params[0];
                params.put("option", option);
            }
            requestList(params, callback);
        }
    }

    private void setupDownload() {
        mLoader = new DownloadImpl();
        mLoader.setOnDownloadListener(new DownloadImpl.OnDownloadListener() {
            @Override
            public void onStart(Downloadable v) {
                mSearchHelper.blockInput();
                input("Start loading:" + ((Vender) v).name);
            }

            @Override
            public void onFinish(Downloadable v) {
                replace("Item installed.");
                mSearchHelper.releaseInput();
                mSearchHelper.addNewVender(context, (Vender) v);
            }

            @Override
            public void onFail(Downloadable v) {
                input("Failed");
                mSearchHelper.releaseInput();
            }

            @Override
            public void onProgress(Downloadable v, int prgs, int current) {
                StringBuilder sb = new StringBuilder(" ");
                int progressIn10 = prgs / 10;
                for (int i = 0; i < progressIn10; i++) {
                    sb.append("▅");
                }
                for (int i = progressIn10; i < 10; i++) {
                    sb.append("　");
                }
                sb.append("　");
                sb.append(prgs);
                sb.append("%");
                replace(sb.toString());
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
    public void execute(VenderItem result) {
        VenderItem.Value value = result.getValue();
        if (value.isEmpty()) {
            input(HELP);
        } else {
            handleWithOption(value);
        }
    }

    private void handleWithOption(VenderItem.Value value) {
        mSearchHelper.blockInput();
        input("loading...");

        HashMap<String, String> params = new HashMap<>();

        if (value.isEmpty()) {
            //.install
            input(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //trans.install
                params.put("name", value.pre);
                requestInstall(params);
            }else {
                //trans.install -m
                //.install -ls
                if (value.params.length > 1) {
                    input(".install only takes one param, ignoring the rest");
                }
                String option = value.params[0];
                params.put("option", option);
                params.put("name", value.pre);

                if (option.equals(OPT_LS) || option.equals(OPT_F)) {
                    requestList(params);
                } else if (option.equals(OPT_M)) {
                    requestInstall(params);
                }
            }
        }
    }

    private void requestInstall(HashMap<String, String> params) {
        new VolleyProvider().handleData(URL_INSTALL, params, Vender.class,
                new Listener.Response<Vender>() {

                    @Override
                    public void onResponse(Vender obj) {
                        mSearchHelper.releaseInput();
                        if (obj != null) {
                            if (!exist(obj)) {
                                mLoader.addToQueue(obj);
                                mLoader.start();
                            } else {
                                input("Item " + obj.name + " already exists.");
                            }

                        } else {
                            input("Item not found");
                        }
                    }

                    private boolean exist(Vender item) {
                        Vender search = new Select().from(Vender.class).where("cId = ?", item.sid).executeSingle();
                        return (search != null);
                    }
                },
                mDefaultError);
    }

    private void requestList(HashMap<String, String> params) {
        new VolleyProvider().handleData(URL_LIST, params, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        String msg = constructMessage(obj);
                        mSearchHelper.releaseInput();
                        input(msg);
                    }
                },
                mDefaultError);
    }

    private void requestList(HashMap<String, String> params, final OutputCallback callback) {
        new VolleyProvider().handleData(URL_LIST, params, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        String msg = constructMessage(obj);
                        mSearchHelper.releaseInput();
                        callback.onOutput(msg);
                    }
                },
                mDefaultError);
    }

    private Listener.Error mDefaultError = new Listener.Error() {
        @Override
        public void onError(String msg) {
            mSearchHelper.releaseInput();
            input(msg);
        }
    };

    private String constructMessage(Result rs) {
        StringBuilder sb = new StringBuilder();
        for (Vender f : rs.list) {
            sb.append(f.name);
            sb.append("-");
            sb.append(f.pkg);
            sb.append("\n");
        }
        return sb.toString();
    }

    public class Result {
        public ArrayList<Vender> list;
    }
}
