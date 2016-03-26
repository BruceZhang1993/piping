package indi.shinado.piping.pipes.impl.action;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class SettingPipe extends DefaultInputActionPipe {

    private static final String NAME = "$Setting";
    private static final String OPT_W = "w";
    private static final String OPT_C = "c";
    private static final String HELP = "Usage of " + NAME + "\n" +
            "Setting " + Keys.PARAMS + OPT_W + " to set wallpaper\n" +
            "Setting " + Keys.PARAMS + OPT_C + " to set text color\n";

    public SettingPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"setting"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput(HELP);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1){
            callback.onOutput(NAME + " only takes one param, rests ignored.");
        }
        switch (params[0]){
            case OPT_C:
                getLauncher().selectColor(0xff83f352);
                break;
            case OPT_W:
                getLauncher().selectWallpaper();
                break;
            default:
                callback.onOutput(HELP);
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        callback.onOutput(HELP);
    }

}
