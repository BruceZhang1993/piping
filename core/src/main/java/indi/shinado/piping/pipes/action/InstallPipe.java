package indi.shinado.piping.pipes.action;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.HashMap;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.download.DownloadImpl;
import indi.shinado.piping.download.Downloadable;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.entity.SearchableName;

public class InstallPipe extends ActionPipe{

    private Pipe mResult;
    private DownloadImpl mLoader;
    private static final String URL_LIST = "http://1.yilaunch.sinaapp.com/install/list.php";
    private static final String URL_INSTALL = "http://1.yilaunch.sinaapp.com/install/search.php";

    private static final String OPT_LS = "ls";
    private static final String OPT_F = "f";
    private static final String OPT_M = "m";

    private static final String HELP = "Usage of install:\n" +
            "[key].install [-option]\n" +
            "where option includes:\n" +
            Pipe.INDICATOR + OPT_LS + " list all functions available\n" +
            Pipe.INDICATOR + OPT_F + " find items\n" +
            Pipe.INDICATOR + OPT_M + " install whatever matches\n";

    public InstallPipe(int id) {
        super(id);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$install");
        mResult.setSearchableName(new SearchableName(new String[]{"in", "s", "tall"}));
        mResult.setBasePipe(this);
        setupDownload();
    }

    private void setupDownload() {
        mLoader = new DownloadImpl();
        mLoader.setOnDownloadListener(new DownloadImpl.OnDownloadListener() {
            @Override
            public void onStart(Downloadable v) {
                getConsole().blockInput();
                getConsole().input("Start loading:" + v.getFileName());
            }

            @Override
            public void onFinish(Downloadable v) {
                getConsole().replaceCurrentLine("Item installed.");
                getConsole().releaseInput();
                //TODO
//                mSearchHelper.addNewVender(context, (Vender) v);
            }

            @Override
            public void onFail(Downloadable v) {
                getConsole().input("Failed");
                getConsole().releaseInput();
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
                getConsole().replaceCurrentLine(sb.toString());
            }
        });
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void acceptInput(Pipe result, String input) {
        getConsole().input("install does not accept input");
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        Instruction value = result.getInstruction();
        getConsole().blockInput();
        getConsole().input("loading...");

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

    private void requestList(HashMap<String, String> params) {
        new VolleyProvider().handleData(URL_LIST, params, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        String msg = constructMessage(obj);
                        getConsole().releaseInput();
                        getConsole().input(msg);
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
                        getConsole().releaseInput();
                        callback.onOutput(msg);
                    }
                },
                mDefaultError);
    }

    private Listener.Error mDefaultError = new Listener.Error() {
        @Override
        public void onError(String msg) {
            getConsole().releaseInput();
            getConsole().input(msg);
        }
    };



    private String constructMessage(Result rs) {
        StringBuilder sb = new StringBuilder();
        sb.append("List of pipes:\n");
        int i=1;
        for (PipeEntity f : rs.list) {
            sb.append(i + ". ");
            sb.append(f.name);
            sb.append(" -- by ");
            sb.append(f.author);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    protected void execute(Pipe rs) {
        Instruction value = rs.getInstruction();
        if (value.isEmpty()) {
            getConsole().input(HELP);
        } else {
            handleWithOption(value);
        }
    }

    private void handleWithOption(Instruction value) {
        getConsole().blockInput();
        getConsole().input("loading...");

        HashMap<String, String> params = new HashMap<>();

        if (value.isEmpty()) {
            //.install
            getConsole().input(HELP);
        } else {
            if (value.isParamsEmpty()) {
                //trans.install
                params.put("name", value.pre);
                requestInstall(params);
            }else {
                //trans.install -m
                //.install -ls
                if (value.params.length > 1) {
                    getConsole().input(".install only takes one param, ignoring the rest");
                }
                String option = value.params[0];
                params.put("option", option);

                if (option.equals(OPT_LS) || option.equals(OPT_F)) {
                    requestList(params);
                } else if (option.equals(OPT_M)) {
                    if(!value.isPreEmpty()){
                        params.put("name", value.pre);
                        requestInstall(params);
                    }else{
                        getConsole().input("The content cannot be null for -m option");
                    }
                }
            }
        }
    }

    private void requestInstall(HashMap<String, String> params) {
        new VolleyProvider().handleData(URL_INSTALL, params, PipeEntity.class,
                new Listener.Response<PipeEntity>() {

                    @Override
                    public void onResponse(PipeEntity obj) {
                        getConsole().releaseInput();
                        if (obj != null) {
                            if (!exist(obj)) {
                                mLoader.addToQueue(obj);
                                mLoader.start();
                            } else {
                                getConsole().input("Item " + obj.name + " already exists.");
                            }

                        } else {
                            getConsole().input("Item not found");
                        }
                    }

                    private boolean exist(PipeEntity item) {
                        PipeEntity search = new Select().from(PipeEntity.class).where("cId = ?", item.sid).executeSingle();
                        return (search != null);
                    }
                },
                mDefaultError);
    }


    public class Result {
        public ArrayList<PipeEntity> list;
    }
}

