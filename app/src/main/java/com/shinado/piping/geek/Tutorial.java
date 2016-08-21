package com.shinado.piping.geek;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;

import indi.shinado.piping.launcher.CharacterInputCallback;
import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.UserInputCallback;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.entity.Keys;
import shinado.indi.vender.base.HackerLauncher;

/**
 * Created by shinado on 16/5/13.
 */
public class Tutorial {

    private DeviceConsole console;
    private SharedPreferences preferences;
    private boolean inTutorial = false;
    private int phase = 0;

    public Tutorial(Context context, DeviceConsole console) {
        this.console = console;
        preferences = context.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
    }

    public void start() {
        final String key = "firstTime";
        boolean firstTime = preferences.getBoolean(key, true);
        if (firstTime) {
            inTutorial = true;
            console.input("Hi, welcome to Piping. Would you like to take a tutorial? Type 'y' to proceed. \n" +
                    "(Note: if you find keyboard missing, simply press 'HOME' button)");
            console.waitForCharacterInput(new CharacterInputCallback() {
                @Override
                public void onCharacterInput(String character) {
                    if (character.equals("y")){
                        illustration1();
                    }else{
                        console.input("If you want to watch this tutorial, kindly enter 'tutorial'" +
                                " and I will be waiting. Have fine with Piping :)");
                    }

                }
            });
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, false).apply();
    }

    private void pause(){

    }

    public void resume(){
        if (inTutorial){
            switch (phase){
                case 1:
                    illustration2();
                    break;
                case 2:
                    illustration3();
                    break;
                case 3:
                    illustration4();
                    break;
            }
        }
    }

    //launch applications
    private void illustration1() {
        phase = 1;

        console.input("By typing any key, you will get the result displaying on the left, like this:\n" +
                "Facebook: f\n" +
                "So that when you hit 'ENTER' key, Facebook will be launched\n" +
                "The searching results also include your contacts, \n" +
                "by press 'enter' key, you will directly dial this contact.\n\n" +
                "Would you like to try it out? (Type 'y' to proceed)");

        console.waitForCharacterInput(new CharacterInputCallback() {
            @Override
            public void onCharacterInput(String character) {

                if (character.equals("y")) {
                    pause();
                }else{
                    illustration2();
                }
            }
        });
    }

    //pipes
    private void illustration2(){
        phase = 2;

        console.input("Great! Now you've understood the basic usage of Piping. Here's more:\n" +
                "Suppose you have two contact in your phone, Ken and Dan\n" +
                "Now you want to send Ken's number to Dan, just try type 'ken" + Keys.PIPE +"dan' and press ENTER\n" +
                "Try it out.");

        pause();
    }

    private void illustration3(){
        phase = 3;

        console.input("This is Piping. You can pretty much put every thing together to see what magic would happen.\n" +
                "Piping is way more than searching applications and contacts. \n" +
                "We have Pipes Store where you can download Pipes to gear up Piping.\n" +
                "Try enter 'install-ls' to check what are available in Pipe Store");

        pause();
    }

    private void illustration4(){
        phase = 4;

        console.input("The tutorial is over. Thank you for your attention. For further help, please try enter 'help'.");
    }
}
