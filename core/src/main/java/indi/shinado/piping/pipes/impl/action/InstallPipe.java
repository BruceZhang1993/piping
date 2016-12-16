package indi.shinado.piping.pipes.impl.action;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.HashMap;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.download.DownloadImpl;
import indi.shinado.piping.download.Downloadable;
import indi.shinado.piping.pipes.SystemInfo;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.entity.SearchableName;

public class InstallPipe extends DefaultInputActionPipe{

    private DownloadImpl mLoader;
    private static final String URL_LIST = "http://1.yilaunch.sinaapp.com/install/list.php";
    private static final String URL_INSTALL = "http://1.yilaunch.sinaapp.com/install/search.php";

    private static final String OPT_LS = "ls";
    private static final String OPT_M = "m";

    private static final String HELP = "Usage of install:\n" +
            "[key].install [-option]\n" +
            "where option includes:\n" +
            Keys.PARAMS + OPT_LS + " list all items\n" +
            Keys.PARAMS + OPT_M + " install whatever matches\n";

    public InstallPipe(int id) {
        super(id);
        mResult.setAcceptableParams(new Pipe(getId(), OPT_LS), new Pipe(getId(), OPT_M));
        setupDownload();
    }

    @Override
    public void acceptInput(Pipe rs, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (previous != null){
            Pipe prev = previous.get();
            callback.onOutput("Get input from " + prev.getDisplayName());
        }

        Instruction value = rs.getInstruction();
        HashMap<String, String> params = new HashMap<>();

        params.put("name", input);
        params.put("version", ""+new SystemInfo(getLauncher()).getVersionCode());
        if (!value.isParamsEmpty()){
            if (value.params.length > 1) {
                callback.onOutput("$install only takes one param, ignoring the rest");
            }
            String option = value.params[0];
            params.put("option", option);

            switch (option) {
                case OPT_LS:
                    requestList(params, callback);
                    break;
                case OPT_M:
                    requestInstall(params, callback);
                    break;
                default:
                    callback.onOutput(HELP);
                    break;
            }
        }else{
            requestInstall(params, callback);
        }
    }

    @Override
    public String getDisplayName() {
        return "$install";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"ins", "tall"});
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        Instruction value = rs.getInstruction();
        HashMap<String, String> params = new HashMap<>();

        if (value.params.length > 1) {
            callback.onOutput("$install only takes one param, ignoring the rest");
        }
        String option = value.params[0];
        params.put("option", option);
        params.put("version", ""+new SystemInfo(getLauncher()).getVersionCode());

        if (option.equals(OPT_M)){
            callback.onOutput("The content cannot be null for -m option");
        }else {
            if (option.equals(OPT_LS)) {
                requestList(params, callback);
            }else {
                callback.onOutput(HELP);
            }
        }
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput(HELP);
    }

    private void requestInstall(HashMap<String, String> params, final OutputCallback callback) {
        getConsole().blockInput();
        new VolleyProvider().handleData(URL_INSTALL, params, PipeEntity.class,
                new Listener.Response<PipeEntity>() {

                    @Override
                    public void onResponse(PipeEntity obj) {
                        getConsole().releaseInput();
                        if (obj != null) {
                            if (!exist(obj)) {
                                if (callback == getConsoleCallback()) {
                                    mLoader.addToQueue(obj);
                                    mLoader.start();
                                } else {
                                    callback.onOutput("Item " + obj.name + " found, not to be installed.");
                                }
                            } else {
                                callback.onOutput("Item " + obj.name + " already exists.");
                            }

                        } else {
                            callback.onOutput("Item not found");
                        }
                    }

                    private boolean exist(PipeEntity item) {
                        PipeEntity search = new Select().from(PipeEntity.class).where("cId = ?", item.sid).executeSingle();
                        return (search != null);
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        getConsole().releaseInput();
                        callback.onOutput(msg);
                    }
                });
    }

    private String constructMessage(Result rs) {
        StringBuilder sb = new StringBuilder();
        sb.append("List of pipes:\n");
        int i=0;
        for (PipeEntity f : rs.list) {
            sb.append("\n").append(i++);
            sb.append(". ").append(f.name);
            sb.append(" -- by ").append(f.author);
            sb.append(", ").append(f.size).append("\n");
            sb.append("--").append(f.introduction);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void requestList(HashMap<String, String> params, final OutputCallback callback) {
        getConsole().blockInput();
        new VolleyProvider().handleData(URL_LIST, params, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        String msg = constructMessage(obj);
                        getConsole().releaseInput();
                        callback.onOutput(msg);
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        getConsole().releaseInput();
                        callback.onOutput(msg);
                    }
                });
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
                getConsole().input("\n");
                getConsole().releaseInput();
                getPipeManager().addNewPipe((PipeEntity) v);
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

    public class Result {
        public ArrayList<PipeEntity> list;
    }
}

