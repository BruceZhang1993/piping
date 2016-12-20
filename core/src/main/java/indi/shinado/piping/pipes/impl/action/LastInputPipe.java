package indi.shinado.piping.pipes.impl.action;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class LastInputPipe extends DefaultInputActionPipe{

    private final String NAME = "$input";

    public LastInputPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName("input");
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        lastInput(callback);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        lastInput(callback);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        callback.onOutput(NAME + "does not accept input.");
    }

    private void lastInput(OutputCallback callback){
        String lastInput = getConsole().getLastInput();
        if (lastInput != null){
            callback.onOutput(lastInput);
        }else {
            callback.onOutput("Last input is null. ");
        }
    }
}
