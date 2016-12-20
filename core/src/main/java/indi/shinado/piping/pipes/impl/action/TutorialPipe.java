package indi.shinado.piping.pipes.impl.action;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

/**
 * Created by shinado on 16/5/14.
 */
public class TutorialPipe extends DefaultInputActionPipe{

    private static final String NAME = "$tutorial";

    public TutorialPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"tutorial"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
//        getConsole().startTutorial();
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
//        getConsole().startTutorial();

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
//        getConsole().startTutorial();

    }
}
