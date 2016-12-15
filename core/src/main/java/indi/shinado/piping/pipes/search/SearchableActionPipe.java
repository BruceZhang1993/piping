package indi.shinado.piping.pipes.search;


public abstract class SearchableActionPipe extends SearchablePipe{

    public SearchableActionPipe(int id) {
        super(id);
    }

    public abstract String getKeyword();

    public abstract void start(OnQuitSearchActionListener listener);

    public interface OnQuitSearchActionListener{
        public void onQuit();
    }
}
