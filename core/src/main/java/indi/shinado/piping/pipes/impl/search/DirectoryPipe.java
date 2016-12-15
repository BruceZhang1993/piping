package indi.shinado.piping.pipes.impl.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;
import indi.shinado.piping.util.IntentUtil;

public class DirectoryPipe extends SearchablePipe {

    private static final String OPT_ADD = "add";
    private static final String OPT_REMOVE = "rm";
    private AbsTranslator mTranslator;
    private HashMap<String, Pipe> mPipeMap = new HashMap<>();
    private String dir = null;

    public DirectoryPipe(int id) {
        super(id);
    }

    @Override
    public void destroy() {
        //do nothing
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
        callback.onOutput(result.getExecutable());
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
                    add(rs);
                    break;
                case OPT_REMOVE:
                    remove(rs);
                    break;
            }
        } else {
            open(rs);
        }
    }

    private void remove(Pipe rs) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            removeFiles(file);
        }
    }

    private void cd(String dir) {
        this.dir = dir;

        //clear all
        clear();
        //TODO how to cd ..?
        addFile(dir);
    }

    private void add(Pipe rs) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            addFiles(file);
        }
    }

    private void addFile(String path) {
        int index = path.lastIndexOf('/');
        String displayName = path.substring(index + 1, path.length());
        //TODO add Chinese Translator
        if (mTranslator == null) {
            mTranslator = new EnglishTranslator(getLauncher());
        }
        Pipe pipe = new Pipe(DirectoryPipe.this.getId(), "/" + displayName, mTranslator.getName(displayName), path);
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

    private void open(Pipe rs) {
        File file = new File(rs.getExecutable());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            StringBuilder sb = new StringBuilder();
            for (File f : files) {
                sb.append(f.getName()).append("\n");
            }
            cd(rs.getExecutable());
            getConsole().input(sb.toString());
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = IntentUtil.getMIMEType(rs.getExecutable());
            intent.setDataAndType(/*uri*/Uri.fromFile(new File(rs.getExecutable())), type);
            getLauncher().startActivity(intent);
        }
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                while (!translator.ready()) ;
                reset();
                listener.onItemsLoaded(DirectoryPipe.this.getId(), total);
            }
        }.start();
    }

    private void clear(){
        resultMap.clear();
        mPipeMap.clear();
    }

    private void reset() {
        ArrayList<String> all = getAllDirectoriesFromSDCard();
        for (String item : all) {
            addFile(item);
        }
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
