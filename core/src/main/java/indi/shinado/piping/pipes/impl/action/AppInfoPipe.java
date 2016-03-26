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
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput(HELP);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput(HELP);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (!result.getInstruction().isParamsEmpty()){
            callback.onOutput("Parameters ignored.");
        }
        if (previous == null){
            callback.onOutput("Application not found.");
        }else{
            Pipe prev = previous.get();
            if (prev.getId() == PipesLoader.ID_APPLICATION) {
                AppManager.info(getLauncher(), input);
            } else {
                callback.onOutput(prev.getDisplayName() + " is not an application");
            }
        }
    }
}
