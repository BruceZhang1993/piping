package indi.shinado.piping.pipes.impl.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.animation.Animation;

import java.util.HashMap;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.UserInputCallback;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Instruction;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.settings.ConsoleAnimation;
import indi.shinado.piping.settings.Preferences;

public class SettingPipe extends DefaultInputActionPipe {

    private static final String NAME = "$Setting";
    private static final String OPT_W = "w";
    private static final String OPT_C = "c";
    private static final String OPT_S = "s";
    private static final String OPT_B = "b";
    private static final String OPT_I = "i";
    private static final String OPT_LS = "ls";
    private static final String OPT_R = "-reset";
    private static final String LIST_PREFIX = ".";

    private static final String HELP = "Usage of " + NAME + "\n" +
            "Setting " + Keys.PARAMS + OPT_W + " to set wallpaper\n" +
            "Setting " + Keys.PARAMS + OPT_C + " to set color\n" +
            "Setting " + Keys.PARAMS + OPT_S + " to set text size\n" +
            "[width]" + Keys.PIPE + "Setting " + Keys.PARAMS + OPT_B + " to set boundary width\n" +
            "[text]" + Keys.PIPE + "Setting " + Keys.PARAMS + OPT_I + " to set initiating text\n" +
            "Setting " + Keys.PARAMS + OPT_I + Keys.PARAMS + OPT_R + " to reset initiating text\n" +
            "Setting " + Keys.PARAMS + OPT_LS + " to list your current setting\n" +
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
        String help = "Please select an item. Enter any other keys to quit. \n" +
                "0.set wallpaper\n" +
                "1.set text color\n" +
                "2.set text size\n" +
                "3.set head text\n" +
                "4.set boundary width\n" +
                "5.set shift key";
//                "5.set text animation";
        callback.onOutput(help);
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                try {
                    int selection = Integer.parseInt(userInput);
                    switch (selection) {
                        case 0:
                            getLauncher().selectWallpaper();
                            break;
                        case 1:
                            getLauncher().selectColor();
                            break;
                        case 2:
                            getConsole().input("Please enter text size(e.g. 12)");
                            setTextSize();
                            break;
                        case 3:
                            getConsole().input("Please enter head text, using \"" + OPT_R + "\" to reset");
                            setHeadText();
                            break;
                        case 4:
                            getConsole().input("Please enter boundary width(e.g. 4)");
                            setBoundaryWidth();
                            break;
//                        case 5:
//                            getConsole().input("please enter animation parameters.\n" +
//                                    "(" + ConsoleAnimation.TYPE_TRANS +", float startX, float endX, float startY, float endY, int duration) to set translate animation\n" +
//                                    "(" + ConsoleAnimation.TYPE_SCALE +", float fromScale, float toScale, int duration) to set scale animation\n" +
//                                    "(" + ConsoleAnimation.TYPE_ROTATE + ", float fromDegree, float toDegree, int duration) to set rotate animation\n" +
//                                    "(" + ConsoleAnimation.TYPE_ALPHA + ", float fromAlpha, float toAlpha, int duration) to set alpha animation\n");
//
//                            setAnimation();
//                            break;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAnimation(){
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                parserAnimation(userInput);
            }
        });
    }

    private void parserAnimation(String userInput){
        userInput = userInput.replace("(", "");
        userInput = userInput.replace(")", "");
        String[] split = userInput.split(",");
        if (split.length > 0){
            int type = 0;
            try {
                type = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                getConsole().input("Parameters wrong. ");
                return;
            }
            switch (type){
                case ConsoleAnimation.TYPE_TRANS:
                    if (split.length == 6){
                        try {
                            float startX = Float.parseFloat(split[1]);
                            float endX = Float.parseFloat(split[2]);
                            float startY = Float.parseFloat(split[3]);
                            float endY = Float.parseFloat(split[4]);
                            int duration = Integer.parseInt(split[5]);
                            ConsoleAnimation animation = new ConsoleAnimation(
                                    type, Animation.RELATIVE_TO_PARENT, startX, endX, startY, endY, duration);
                            animation.clearTable();
                            animation.save();
                            getLauncher().setAnimation(animation);
                        } catch (NumberFormatException e) {
                            getConsole().input("Parameters wrong.");
                        }
                    }else{
                        getConsole().input("Parameters length wrong.");
                    }
                    break;
                default:
                    if (!ConsoleAnimation.isTypeCorrect(type)){
                        getConsole().input("Type wrong.");
                        return;
                    }
                    if (split.length == 4){
                        float startX = Float.parseFloat(split[1]);
                        float endX = Float.parseFloat(split[2]);
                        int duration = Integer.parseInt(split[3]);
                        ConsoleAnimation animation = new ConsoleAnimation(
                                type, Animation.RELATIVE_TO_PARENT, startX, endX, 0, 0, duration);
                        animation.clearTable();
                        animation.save();
                        getLauncher().setAnimation(animation);
                    }else{
                        getConsole().input("Parameters length wrong.");
                    }

            }
        }
    }

    private void setTextSize() {
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                try {
                    int size = Integer.parseInt(userInput);
                    Preferences preferences = new Preferences(context);
                    preferences.setTextSize(size);
                    getLauncher().setTextSize(size);
                } catch (NumberFormatException e) {
                    getConsole().input("Input is not a number");
                }
            }
        });
    }

    private void setBoundaryWidth() {
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                try {
                    int width = Integer.parseInt(userInput);
                    Preferences preferences = new Preferences(context);
                    preferences.setBoundaryWidth(width);
                    getLauncher().setBoundaryWidth(width);
                } catch (NumberFormatException e) {
                    getConsole().input("Input is not a number");
                }
            }
        });
    }

    private void setHeadText() {
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                Preferences preferences = new Preferences(context);
                if (userInput.equals(OPT_R)){
                    userInput = Preferences.DEFAULT_INIT_TEXT;
                }
                preferences.setInitText(userInput);
                getLauncher().setInitText(userInput);
            }
        });
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        Preferences preferences = new Preferences(context);
        String[] params = rs.getInstruction().params;
        if (params.length > 1) {
            if (params[0].equals(OPT_I)) {
                if (params[1].equals(OPT_R)) {
                    preferences.setInitText(Preferences.DEFAULT_INIT_TEXT);
                    getLauncher().setInitText(Preferences.DEFAULT_INIT_TEXT);
                    return;
                } else {
                    callback.onOutput(HELP);
                }
            } else {
                callback.onOutput(NAME + " only takes one param, rests ignored.");
            }
        }
        switch (params[0]) {
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
                sb.append("Animation: ").append(ConsoleAnimation.get().toString()).append("\n");
                callback.onOutput(sb.toString());
                break;
            default:
                callback.onOutput(HELP);
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (input.startsWith(LIST_PREFIX)) {
            final Preferences preferences = new Preferences(context);
            String[] settings = input.split("\n");
            int i = 0;
            for (String setting : settings) {
                String item = setting.split(": ", 2)[1].trim();
                switch (i++) {
                    case 0:
                        int width = Integer.parseInt(item);
                        preferences.setBoundaryWidth(width);
                        getLauncher().setBoundaryWidth(width);
                        break;
                    case 1:
                        int color = Integer.parseInt(item);
                        preferences.setColor(color);
                        getLauncher().setColor(color);
                        break;
                    case 2:
                        preferences.setInitText(item);
                        getLauncher().setInitText(item);
                        break;
                    case 3:
                        float size = Float.parseFloat(item);
                        preferences.setTextSize(size);
                        getLauncher().setTextSize(size);
                        break;
                    case 4:
                        ConsoleAnimation animation = new ConsoleAnimation(item);
                        animation.clearTable();
                        animation.save();
                        getLauncher().setAnimation(animation);
                        break;
                    case 5:
                        getConsole().waitForKeyDown(new KeyDownCallback() {
                            @Override
                            public void onKeyDown(int keyCode) {
                                if (keyCode == KeyEvent.KEYCODE_BACK ||
                                        keyCode == KeyEvent.KEYCODE_HOME){
                                    getConsole().input("Error, can not set this key");
                                    return;
                                }
                                preferences.setShiftKey(keyCode);
                            }
                        });
                        break;
                }
            }
            return;
        }

        Instruction instruction = result.getInstruction();
        if (!instruction.isParamsEmpty()) {
            String[] params = result.getInstruction().params;

            Preferences preferences = new Preferences(context);
            switch (params[0]) {
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
        } else {
            callback.onOutput(HELP);
        }
    }

    private void setValues(Context context, HashMap<String, String> values){
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (String key : values.keySet()){
            String value = values.get(key);
            editor.putString(key, value);
        }
        editor.apply();
    }

    public class SettingStorage{

        private SharedPreferences mStorage;

        public SettingStorage(Context context){
            mStorage = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }


    }
}
