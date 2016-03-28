package indi.shinado.piping.pipes.impl.action;

import java.util.ArrayList;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class HelpPipe extends DefaultInputActionPipe{

    public HelpPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$help";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"help"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        help(callback);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        help(callback);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        help(callback);
    }

    private void help(OutputCallback callback){
        StringBuilder sb = new StringBuilder("Simply search your application or contact.\n")
                .append("To switch results, hig MENU button.\n")
                .append("To display history or intercept an action, hit BACK button.\n")
                .append("To display notifications on the terminal, please enable Piping in Accessibility\n")
                .append("You can also type these instructions below to find more information:\n");
        ArrayList<BasePipe> pipes = getPipeManager().getAllPipes();
        for (BasePipe pipe : pipes){
            if (pipe instanceof ActionPipe){
                ActionPipe actionPipe = (ActionPipe) pipe;
                sb.append(actionPipe.getDisplayName().replace("$", ""));
                sb.append("\n");
            }
        }
        callback.onOutput(sb.toString());
    }
}
