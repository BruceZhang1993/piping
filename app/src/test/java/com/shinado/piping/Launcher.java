package com.shinado.piping;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import indi.shinado.piping.launcher.BaseLauncherView;
import indi.shinado.piping.launcher.CharacterInputCallback;
import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.launcher.impl.ConsoleHelper;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.ConsoleInfo;
import indi.shinado.piping.pipes.IPipeManager;
import indi.shinado.piping.pipes.PipeManager;
import indi.shinado.piping.pipes.entity.Keys;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

@RunWith(MockitoJUnitRunner.class)
public class Launcher implements DeviceConsole {

    public static final String KEY_SPACE = "space";
    public static final String KEY_BACKSPACE = "backspace";
    public static final String KEY_ENTER = "enter";
    public static final String KEY_SHIFT = "shift";

    @Mock
    private BaseLauncherView context;

    private ConsoleHelper helper;

    private String mCurrentInput = "";

    @Before
    public void setup() {
        IPipeManager pipeManager = new PipeManager();
        pipeManager.load(context, new TempPipesLoader(), this, new EnglishTranslator(context));
        helper = new ConsoleHelper(this, pipeManager);

    }

    @Test
    public void testSearch1() {
        inputString("ins");
        doInput("-");
    }

    @Test
    public void testSearch() {
//        inputString("test");
//        doClear();
//        inputString("qq" + Keys.PIPE + "test");
//        inputString(Keys.PARAMS + "ls");
//        doClear();
//        inputString("test" + Keys.PARAMS + "ls");
//        doClear();
//
//        pressKey("k");
//        pressKey("a");
//        pressKey(KEY_SHIFT);
//        doClear();
//
//        pressKey("k");
//        pressKey(KEY_BACKSPACE);
//        pressKey("k");
//        pressKey("a");
//        pressKey(Keys.PIPE);
//        pressKey("t");
//        pressKey("e");
//        pressKey("s");
//        pressKey("t");
//        pressKey(KEY_ENTER);
//
//        pressKey("k");
//        pressKey("a");
//        pressKey(Keys.PIPE);
//        pressKey("k");
//        pressKey("a");
//        pressKey(KEY_ENTER);
//
//        pressKey("k");
//        pressKey("a");
//        pressKey(KEY_SHIFT);
//        pressKey(KEY_SHIFT);
//
//        pressKey(Keys.PIPE);
//        pressKey("k");
//        pressKey("a");
//        pressKey(KEY_SHIFT);
//        pressKey(KEY_ENTER);
//
//        pressKey("k");
//        pressKey("a");
//        pressKey(" ");
//        pressKey(Keys.PIPE);
//        pressKey("k");
//        pressKey("a");
//        pressKey(Keys.PIPE);
//        pressKey("k");
//        pressKey(KEY_ENTER);

//        System.out.println("------ new test ------");
//        pressKey("k");
//        pressKey("a");
//        pressKey(Keys.PIPE);
//        pressKey("c");
//        pressKey(KEY_ENTER);
//
//        pressKey("k");
//        pressKey("a");
//        pressKey(KEY_SHIFT);
//        pressKey(Keys.PIPE);
//        pressKey("c");
//        pressKey(KEY_ENTER);
//
//        System.out.println("----------------------- test select -------------------------");
//        pressKey("k");
//        pressKey("a");
//        helper.select(2);
//        pressKey(Keys.PIPE);
//        pressKey("c");
//        pressKey(KEY_ENTER);
//
//
//        System.out.println("----------------------- test python -------------------------");
//        inputString("python");
//        pressKey(KEY_ENTER);
//
//        inputString("what");
//        pressKey("t");
//        pressKey("f");
//        pressKey(KEY_ENTER);
//        inputString("quit");
//        pressKey(KEY_ENTER);
//        pressKey("k");

        inputString("key");
        pressKey(Keys.PARAMS);
        inputString("dow");
        pressKey(KEY_ENTER);
    }

    @Test
    public void testHistory() {
        helper.onUserInput("kakao", 0, 5);
        doClear();
        helper.onUserInput("google", 0, 6);
    }

    private void inputString(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            pressKey("" + c);
        }
    }

    private void doClear() {
        helper.reset();
        mCurrentInput = "";
    }

    private void pressKey(String key) {
        System.out.println("press key: " + key);
        if (key.length() == 1) {
            doInput(key);
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
                    doInput(key);
                    break;
                case KEY_ENTER:
                    helper.onEnter();
                    doClear();
                    break;
            }
        }
    }

    private void doInput(String key) {
        int before = mCurrentInput.length();
        mCurrentInput += key;
        helper.onUserInput(mCurrentInput, before, mCurrentInput.length());
    }

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
    public void displayResult(Collection<Pipe> pipe) {
        StringBuilder sb = new StringBuilder();
        for (Pipe p : pipe) {
            sb.append(p.getDisplayName()).append(", ");
        }
        System.out.println("display result:" + sb.toString());
    }

    @Override
    public void displayPrevious(Pipe pipe) {
        System.out.println("display previous:" + pipe.getDisplayName());
    }

    @Override
    public void clear() {
        System.out.println("clear");

    }

    @Override
    public void intercept() {

    }

    @Override
    public String getLastInput() {
        return null;
    }

    @Override
    public void waitForSingleLineInput(SingleLineInputCallback inputCallback) {
        helper.waitForUserInput(inputCallback);
    }

    @Override
    public void waitForCharacterInput(final CharacterInputCallback inputCallback) {
        helper.addInputCallback(new InputCallback() {
            @Override
            public void onInput(String character) {
                inputCallback.onCharacterInput(character);
                helper.removeInputCallback(this);
                quitBlind();
            }
        });
    }

    @Override
    public void waitForKeyDown(KeyDownCallback inputCallback) {
    }

    @Override
    public void display(String string) {

    }

    @Override
    public ConsoleInfo getConsoleInfo() {
        return null;
    }

    @Override
    public void occupyMode() {
        helper.occupyMode();
    }

    @Override
    public void quitOccupy() {
        helper.quitOccupy();
    }

    @Override
    public void hideInitText() {

    }

    @Override
    public void showInitText() {

    }

    @Override
    public void blindMode() {
        helper.blindMode();
    }

    @Override
    public void quitBlind() {
        helper.quitBlind();
    }

    @Override
    public void notifyUI() {

    }

    @Override
    public BasePipe getPipeById(int id) {
        return null;
    }

    @Override
    public void startTutorial() {

    }

    @Override
    public void addInputCallback(InputCallback inputCallback) {
        helper.addInputCallback(inputCallback);
    }

    @Override
    public void removeInputCallback(InputCallback inputCallback) {
        helper.removeInputCallback(inputCallback);
    }

    @Override
    public void setIndicator(String indicator) {
        System.out.print("set indicator: " + indicator);
    }

    @Override
    public void onEnter(Pipe pipe) {
        System.out.println("enter:" + (pipe == null ? "" : pipe.getDisplayName()));
    }

    @Override
    public void onSelected(Pipe pipe) {
        System.out.println("shift:" + pipe.getDisplayName());
    }

    @Override
    public void onNothing() {
        System.out.println("nothing to show");
    }

}
