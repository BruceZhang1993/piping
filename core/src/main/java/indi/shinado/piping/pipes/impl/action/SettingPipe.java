package indi.shinado.piping.pipes.impl.action;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.settings.Preferences;

public class SettingPipe extends DefaultInputActionPipe {

    private static final String NAME = "$Setting";
    private static final String OPT_W = "w";
    private static final String OPT_C = "c";
    private static final String OPT_S = "s";
    private static final String OPT_B = "b";
    private static final String OPT_I = "i";
    private static final String OPT_LS = "ls";
    private static final String OPT_R = "reset";
    private static final String LIST_PREFIX = ".";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "Setting " + Keys.PARAMS + OPT_W + " to set wallpaper\n" +
            "Setting " + Keys.PARAMS + OPT_C + " to set color\n" +
            "Setting " + Keys.PARAMS + OPT_S + " to set text size\n" +
            "[width]" + Keys.PIPE + "Setting " + Keys.PARAMS + OPT_B + " to set boundary width\n" +
            "[text]" + Keys.PIPE + "Setting " + Keys.PARAMS + OPT_I + " to set initiating text\n" +
            "Setting " + Keys.PARAMS + OPT_I + Keys.PARAMS + OPT_R  + " to reset initiating text\n" +
            "Setting " + Keys.PARAMS + OPT_B + " to list your current setting\n" +
            "[setting]" + Keys.PIPE + "Setting " + Keys.PARAMS + OPT_B + " to set from setting\n";

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
        Preferences preferences = new Preferences(context);
        String[] params = rs.getInstruction().params;
        if (params.length > 1){
            if (params[0].equals(OPT_I)){
                if (params[1].equals(OPT_R)){
                    preferences.setInitText(Preferences.DEFAULT_INIT_TEXT);
                    getLauncher().setInitText(Preferences.DEFAULT_INIT_TEXT);
                    return;
                }else{
                    callback.onOutput(HELP);
                }
            }else {
                callback.onOutput(NAME + " only takes one param, rests ignored.");
            }
        }
        switch (params[0]){
            case OPT_C:
                getLauncher().selectColor();
                break;
            case OPT_W:
                getLauncher().selectWallpaper();
                break;
            case OPT_LS:
                StringBuilder sb = new StringBuilder();
                sb.append(LIST_PREFIX);
                sb.append("Boundary size: ").append(preferences.getBoundaryWidth()).append("\n");
                sb.append("Color: ").append(preferences.getColor()).append("\n");
                sb.append("Initiating text: ").append(preferences.getInitText()).append("\n");
                sb.append("Text size: ").append(preferences.getTextSize()).append("\n");
                callback.onOutput(sb.toString());
                break;
            default:
                callback.onOutput(HELP);
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (input.startsWith(LIST_PREFIX)){
            Preferences preferences = new Preferences(context);
            String[] settings = input.split("\n");
            int i=0;
            for (String setting : settings){
                String item = setting.split(": ")[1].trim();
                switch (i++){
                    case 0:
                        int width = Integer.parseInt(item);
                        preferences.setBoundaryWidth(width);
                        break;
                    case 1:
                        int color = Integer.parseInt(item);
                        preferences.setColor(color);
                        break;
                    case 2:
                        preferences.setInitText(item);
                        break;
                    case 3:
                        float size = Float.parseFloat(item);
                        preferences.setTextSize(size);
                        break;
                }
            }
            return;
        }

        Instruction instruction = result.getInstruction();
        if (!instruction.isParamsEmpty()){
            String[] params = result.getInstruction().params;

            Preferences preferences = new Preferences(context);
            switch (params[0]){
                case OPT_B:
                    try {
                        int width = Integer.parseInt(input);
                        preferences.setBoundaryWidth(width);
                        getLauncher().setBoundaryWidth(width);
                    } catch (NumberFormatException e) {
                        callback.onOutput("Please input a number for width");
                    }
                    break;
                case OPT_I:
                    preferences.setInitText(input);
                    getLauncher().setInitText(input);
                    break;
                case OPT_S:
                    try {
                        int size = Integer.parseInt(input);
                        preferences.setTextSize(size);
                        getLauncher().setTextSize(size);
                    } catch (NumberFormatException e) {
                        callback.onOutput("Please input a number for size");
                    }
                    break;
                default:
                    callback.onOutput(HELP);
                    break;
            }
        }else {
            callback.onOutput(HELP);
        }
    }

}
