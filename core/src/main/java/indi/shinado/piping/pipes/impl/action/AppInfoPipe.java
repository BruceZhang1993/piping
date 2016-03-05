package indi.shinado.piping.pipes.impl.action;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.PipesLoader;
import indi.shinado.piping.util.android.AppManager;

public class AppInfoPipe extends DefaultInputActionPipe{

    private static final String NAME = "$info";
    private static final String HELP = "Usage of " + NAME + "\n" +
            "[application] + "+ Keys.PIPE +"info to display information of a certain application";

    public AppInfoPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"in", "fo"});
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
    public void acceptInput(Pipe result, String input) {
        Pipe prev = result.getPrevious().get();
        if (prev.getId() == PipesLoader.ID_APPLICATION) {
            AppManager.info(context, input);
        } else {
            getConsole().input(prev.getDisplayName() + " is not an application");
        }
    }
}
