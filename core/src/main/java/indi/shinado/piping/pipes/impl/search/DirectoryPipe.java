package indi.shinado.piping.pipes.impl.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.ShareIntent;
import indi.shinado.piping.pipes.search.SearchableActionPipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;
import indi.shinado.piping.util.IntentUtil;

public class DirectoryPipe extends SearchableActionPipe {

    private static final String TAG = "DirectoryPipe";
    private static final String OPT_ADD = "add";
    private static final String OPT_REMOVE = "rm";
    private AbsTranslator mTranslator;
    private Pipe cdPipe;
    private Pipe exitPipe;
    private HashMap<String, Pipe> mPipeMap = new HashMap<>();
    private FileObserver mFileObserver;
    private OnQuitSearchActionListener mListener;

    public DirectoryPipe(int id) {
        super(id);
        cdPipe = new Pipe(getId(), "$cd", new SearchableName("cd"), "$#cd");
        exitPipe = new Pipe(getId(), "$exit", new SearchableName("exit"), "$#exit");

        mFileObserver = new FileObserver(Environment.getExternalStorageState()) {
            @Override
            public void onEvent(int i, String s) {
                Log.d(TAG, "onEvent: " + s);
                addFile(s);
            }
        };
        mFileObserver.startWatching();
    }

    @Override
    public String getKeyword() {
        return "cd";
    }

    @Override
    public void start(OnQuitSearchActionListener listener) {
        this.mListener = listener;
        File root = Environment.getExternalStorageDirectory();
        add(new Pipe(getId(), "/", new SearchableName(""), root.getAbsolutePath()), true);
    }

    @Override
    public void destroy() {
        mFileObserver.stopWatching();
    }

    @Override
    public Pipe getByValue(String value) {
        //not used
        return null;
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        callback.onOutput("Directory does not take any input");
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        File file = new File(result.getExecutable());
        if (file.isDirectory()) {
            ls(result.getExecutable(), callback);
        } else {
            String type = IntentUtil.getMIMEType(result.getExecutable());
            ShareIntent shareIntent = new ShareIntent(type);
            shareIntent.extras.put(Intent.EXTRA_STREAM, result.getExecutable());
            callback.onOutput(shareIntent.toString());
        }
    }

    @Override
    protected void putItemInMap(Pipe vo) {
        super.putItemInMap(vo);
        mPipeMap.put(vo.getExecutable(), vo);
    }

    @Override
    protected void removeItemInMap(Pipe vo) {
        super.removeItemInMap(vo);
        mPipeMap.remove(vo.getExecutable());
    }

    @Override
    protected void execute(Pipe rs) {
        Instruction instruction = rs.getInstruction();
        String params[] = instruction.params;
        if (params != null && params.length != 0) {
            if (params.length > 1) {

            }
            switch (params[0]) {
                case OPT_ADD:
                    add(rs, false);
                    break;
                case OPT_REMOVE:
                    remove(rs);
                    break;
            }
        } else {
            open(rs, getConsoleCallback());
        }
    }

    private void remove(Pipe rs) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            removeFiles(file);
        }
    }

    private void cd(Pipe rs, OutputCallback callback) {
        //clear all
        clear();
        add(rs, false);
        getConsole().setIndicator(rs.getDisplayName());
        ls(rs.getExecutable(), callback);
    }

    private void ls(String dir, OutputCallback callback) {
        File file = new File(dir);
        File[] files = file.listFiles();
        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            sb.append(f.getName()).append("\n");
        }
        callback.onOutput(sb.toString());
    }

    private void add(Pipe rs, boolean isRoot) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            addFiles(file);
        }

        if (!isRoot) {
            cdPipe.setExecutable(file.getParent());
            putItemInMap(cdPipe);
        } else {
            removeItemInMap(cdPipe);
        }
    }

    private void addFile(String path) {
        int index = path.lastIndexOf('/');
        String displayName = path.substring(index + 1, path.length());
        //TODO add Chinese Translator
        if (mTranslator == null) {
            mTranslator = new EnglishTranslator(getLauncher());
        }
        Pipe pipe = new Pipe(getId(), "/" + displayName, mTranslator.getName(displayName), path);
        pipe.setBasePipe(DirectoryPipe.this);
        putItemInMap(pipe);
    }

    private void addFiles(File dir) {
        File files[] = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                addFile(file.getPath());
            } else if (file.isDirectory()) {
                addFiles(file);
            }
        }
    }

    private void removeFiles(File dir) {
        File files[] = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                removeFile(file.getPath());
            } else if (file.isDirectory()) {
                removeFiles(file);
            }
        }
    }

    private void removeFile(String path) {
        Pipe pipe = mPipeMap.get(path);
        removeItemInMap(pipe);
    }

    private void open(Pipe rs, OutputCallback callback) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            cd(rs, callback);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = IntentUtil.getMIMEType(rs.getExecutable());
            intent.setDataAndType(/*uri*/Uri.fromFile(new File(rs.getExecutable())), type);
            getLauncher().startActivity(intent);
            clear();
            quit();
        }
    }

    private void quit() {
        if (mListener != null) {
            mListener.onQuit();
        }
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        //nothing to search unless cd is handled
        putItemInMap(cdPipe);
        listener.onItemsLoaded(DirectoryPipe.this.getId(), total);
    }

    private void clear() {
        resultMap.clear();
        mPipeMap.clear();
    }

    public ArrayList<String> getAllDirectoriesFromSDCard() {
        ArrayList<String> absolutePathOfImageList = new ArrayList<>();
        File file[] = Environment.getExternalStorageDirectory().listFiles();

        for (File f : file) {
            if (f.isDirectory()) {
                absolutePathOfImageList.add(f.getAbsolutePath());
            }
        }

        return absolutePathOfImageList;
    }
}
