package indi.shinado.piping.pipes.search;


import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;

public abstract class SearchableActionPipe extends SearchablePipe {

    protected boolean hasStarted = false;

    public SearchableActionPipe(int id) {
        super(id);
    }

    public abstract String getKeyword();

    public void start() {
        hasStarted = true;
    }

    public void quit() {
        pipeManager.getSearcher().searchAll();
        hasStarted = false;
    }

    @Override
    protected TreeSet<Pipe> search(String input) {
        if (hasStarted) {
            return super.search(input);
        } else {
            TreeSet<Pipe> result = new TreeSet<>();
            if (getKeyword().contains(input) || input.equals(getKeyword() + Keys.PARAMS)){
                Pipe pipe = new Pipe(getId(), getKeyword());
                pipe.setBasePipe(this);
                result.add(pipe);

                if (input.equals(getKeyword() + Keys.PARAMS)) {
                    pipeManager.getSearcher().searchAction(this);
                    start();
                }
            }
            return result;
        }
    }

}
