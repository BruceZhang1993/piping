package indi.shinado.piping.pipes.impl.search;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public class DirectoryPipe extends SearchablePipe {

    public DirectoryPipe(int id) {
        super(id);
    }

    @Override
    public void destroy() {
        //do nothing
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
    protected void execute(Pipe rs) {
        //TODO help me, it's not working
//        File root = new File(rs.getExecutable());
//        Uri uri = Uri.fromFile(root);
//
//        Intent intent = new Intent();
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setData(uri);
//        intent.setType("resource/folder");
//        baseLauncherView.startActivity(intent);
    }

    @Override
    public void load(final AbsTranslator translator, final OnItemsLoadedListener listener, final int total) {
        new Thread() {
            public void run() {
                while (!translator.ready());
                ArrayList<String> all = getAllDirectoriesFromSDCard();
                for (String item : all) {
                    int index = item.lastIndexOf('/');
                    String displayName = item.substring(index + 1, item.length());
                    Pipe pipe = new Pipe(DirectoryPipe.this.getId(), displayName + ".dir", translator.getName(displayName), item);
                    pipe.setBasePipe(DirectoryPipe.this);
                    putItemInMap(pipe);
                }
                listener.onItemsLoaded(DirectoryPipe.this.getId(), total);
            }
        }.start();
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
