package indi.shinado.piping.pipes;

import android.content.Context;

import java.util.ArrayList;

import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.PipeEntity;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

public abstract class IPipeManager {

    private OnPipeChangeListener mOnPipeChangeListener;
    private AbsTranslator mTranslator;
    private Context mContext;
    private IPipesLoader mLoader;
    private DeviceConsole console;
    private ArrayList<BasePipe> mPipes = new ArrayList<>();
    private PipeSearcher pipeSearcher;

    public void addNewPipe(PipeEntity entity) {
        entity.save();
        console.blockInput();
        BasePipe pipe = mLoader.load(entity, mContext, console, mTranslator, new BasePipe.OnItemsLoadedListener() {

            public void onItemsLoaded(int id, int total) {
                console.releaseInput();
            }
        });
        pipeSearcher.addPipe(pipe);
        pipe.setPipeManager(this);
        if (mOnPipeChangeListener != null) {
            mOnPipeChangeListener.onPipeAdded(entity);
        }
    }

    public boolean removePipe(int id) {
        pipeSearcher.removePipe(id);
        if (inDatabase(id)) {
            if (mOnPipeChangeListener != null) {
                mOnPipeChangeListener.onPipeRemoved(id);
            }
            delete(id);
            return true;
        } else {
            return false;
        }
    }

    public Pipe getPipe(String pkg) {
        for (BasePipe pipe : mPipes) {
            if (pipe instanceof SearchablePipe) {
                Pipe item = ((SearchablePipe) pipe).getByValue(pkg);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    public void load(Context context, IPipesLoader loader, final DeviceConsole console, AbsTranslator translator) {
        this.mContext = context;
        this.console = console;
        this.pipeSearcher = new PipeSearcher();
        this.mLoader = loader;
        this.mTranslator = translator;
        mPipes.addAll(loader.load(context, console, translator, new SearchablePipe.OnItemsLoadedListener() {

            private int loadedItemsCount = 0;

            public void onItemsLoaded(int id, int total) {
                if (++loadedItemsCount == total) {
                    console.onSystemReady();
                }
            }
        }));

        for (BasePipe pipe : mPipes) {
            pipe.setPipeManager(this);
        }
        pipeSearcher.addPipes(mPipes);
    }

    public PipeSearcher getSearcher() {
        return pipeSearcher;
    }


    public ArrayList<BasePipe> getAllPipes() {
        return mPipes;
    }


    public void destroy() {
        for (BasePipe pipe : mPipes) {
            if (pipe instanceof SearchablePipe) {
                ((SearchablePipe) pipe).destroy();
            }
        }
    }

    private boolean inDatabase(int id) {
        return search(id) != null;
    }

    public void setOnPipeChangeListener(OnPipeChangeListener listener) {
        this.mOnPipeChangeListener = listener;
    }


    public interface OnPipeChangeListener {
        public void onPipeAdded(PipeEntity pipe);

        public void onPipeRemoved(int id);
    }

    protected abstract PipeEntity search(int id);
    protected abstract void delete(int id);
}
