package indi.shinado.piping.pipes.impl.action;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.util.android.AppManager;

public class UninstallPipe extends DefaultInputActionPipe{

    private static final String NAME = "$uninstall";
    private static final String HELP = "Usage of " + NAME + "\n" +
            "[application]"+ Keys.PIPE +"uninstall to uninstall a certain application";

    public UninstallPipe(int id) {
        super(id);
        mResult.setIgnoreInput(true);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"un", "ins", "tall"});
    }

    @Override
    public void onEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void onParamsEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void onPreEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void onNoEmpty(Pipe rs, IInput input) {
        input.input(HELP);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous) {
        Pipe prev = previous.get();
        if (prev.getId() == PipesLoader.ID_APPLICATION) {
            AppManager.uninstall(context, input);
        } else {
            if (prev.getTypeIndex() == Pipe.TYPE_ACTION){
                boolean b = getPipeManager().removePipe(prev.getId());
                if (!b){
                    getConsole().input("Unable to uninstall pipe.");
                }
            }else {
                getConsole().input(prev.getDisplayName() + " is neither an application or an action");
            }
        }
    }
}
