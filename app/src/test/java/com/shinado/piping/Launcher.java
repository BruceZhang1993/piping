package com.shinado.piping;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

@RunWith(MockitoJUnitRunner.class)
public class Launcher {

    public static final String KEY_SPACE = "space";
    public static final String KEY_BACKSPACE = "backspace";
    public static final String KEY_ENTER = "enter";
    public static final String KEY_SHIFT = "shift";

    @Mock
    private Context context;

    private DeviceConsole console = new StaticConsole();

    private ConsoleHelper helper;

    private String mCurrentInput = "";

    @Before
    public void setup() {
        helper = new ConsoleHelper(context, console, new TempPipesLoader(), new EnglishTranslator(context));
    }

    @Test
    public void testSearch() {
        inputString("test");
        clear();
        inputString("qq"+ Keys.PIPE +"test");
        inputString(Keys.PARAMS + "ls");
        clear();
        inputString("test"+ Keys.PARAMS +"ls");
        clear();

        pressKey("k");
        pressKey("a");
        pressKey(KEY_SHIFT);
        clear();

        pressKey("k");
        pressKey(KEY_BACKSPACE);
        pressKey("k");
        pressKey("a");
        pressKey(Keys.PIPE);
        pressKey("t");
        pressKey("e");
        pressKey("s");
        pressKey("t");
        pressKey(KEY_ENTER);

        pressKey("k");
        pressKey("a");
        pressKey(Keys.PIPE);
        pressKey("k");
        pressKey("a");
        pressKey(KEY_ENTER);

        pressKey("k");
        pressKey("a");
        pressKey(KEY_SHIFT);
        pressKey(KEY_SHIFT);

        pressKey(Keys.PIPE);
        pressKey("k");
        pressKey("a");
        pressKey(KEY_SHIFT);
        pressKey(KEY_ENTER);

        pressKey("k");
        pressKey("a");
        pressKey(" ");
        pressKey(Keys.PIPE);
        pressKey("k");
        pressKey("a");
        pressKey(Keys.PIPE);
        pressKey("k");
        pressKey(KEY_ENTER);
    }

    @Test
    public void testHistory(){
        helper.onUserInput("kakao", 0, 5);
        clear();
        helper.onUserInput("google", 0, 6);
    }

    private void inputString(String string){
        for (int i=0; i<string.length(); i++){
            char c = string.charAt(i);
            pressKey(""+c);
        }
    }

    private void clear(){
        helper.reset();
        mCurrentInput = "";
    }

    private void pressKey(String key){
        if (key.length() == 1) {
            input(key);
        } else {
            switch (key) {
                case KEY_BACKSPACE:
                    //EditView must have something
                    if (mCurrentInput.length() > 0) {
                        mCurrentInput = mCurrentInput.substring(0, mCurrentInput.length() - 1);
                    }
                    break;
                case KEY_SHIFT:
                    helper.onShift();
                    break;
                case KEY_SPACE:
                    input(key);
                    break;
                case KEY_ENTER:
                    helper.onEnter();
                    clear();
                    break;
            }
        }
    }

    private void input(String key){
        int before = mCurrentInput.length();
        mCurrentInput += key;
        helper.onUserInput(mCurrentInput, before, mCurrentInput.length());
    }

    public class StaticConsole implements DeviceConsole {

        @Override
        public void input(String string) {
            System.out.println("console input:" + string);
        }

        @Override
        public void replaceCurrentLine(String line) {
             System.out.println("replace:" + line);
        }

        @Override
        public void blockInput() {
            System.out.println("block input");
        }

        @Override
        public void releaseInput() {
            System.out.println("release input");
        }

        @Override
        public void onSystemReady() {
            System.out.println("onSystemReady");
        }

        @Override
        public void displayResult(Pipe pipe) {
            System.out.println("display result:" + pipe.getDisplayName());
        }

        @Override
        public void displayPrevious(Pipe pipe) {
            System.out.println("display previous:" + pipe.getDisplayName());
        }

        @Override
        public void onEnter(Pipe pipe) {
            System.out.println("enter:" + pipe.getDisplayName());
        }

        @Override
        public void onShift(Pipe pipe) {
            System.out.println("shift:" + pipe.getDisplayName());
        }

        @Override
        public void onNothing() {
            System.out.println("nothing to show");
        }
    }
}
