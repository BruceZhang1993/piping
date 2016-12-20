package indi.shinado.piping.pipes.search;


import java.util.TreeSet;

import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;

public abstract class SearchableActionPipe extends SearchablePipe {

    protected boolean hasStarted = false;

    public SearchableActionPipe(int id) {
        super(id);
    }

    public abstract String getKeyword();

    @Override
    public Pipe getByValue(String value) {
        return null;
    }

    public void start() {
        pipeManager.getSearcher().searchAction(this);
        hasStarted = true;
    }

    public void quit() {
        pipeManager.getSearcher().reenableSearchAll();
        hasStarted = false;
    }

    @Override
    protected void execute(Pipe rs) {
        quit();
    }

    @Override
    public void reset() {
        super.reset();
//        quit();
    }

    @Override
    protected TreeSet<Pipe> search(Instruction input) {
        if (hasStarted) {
            return super.search(input);
        } else {
            TreeSet<Pipe> result = new TreeSet<>();
            if (getKeyword().equals(input.body) && input.input.endsWith(Keys.PARAMS)){
                Pipe pipe = new Pipe(getId(), getKeyword());
                pipe.setBasePipe(this);
                result.add(pipe);

                if (input.endsWith(Keys.PARAMS)) {
                    start();
                }
            }
            return result;
        }
    }

}
