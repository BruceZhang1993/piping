package com.shinado.piping;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import indi.shinado.piping.launcher.DeviceConsole;
import indi.shinado.piping.launcher.ConsoleHelper;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.search.translator.EnglishTranslator;

@RunWith(MockitoJUnitRunner.class)
public class Launcher {

    @Mock
    private Context context;

    private DeviceConsole console = new StaticConsole();

    private ConsoleHelper helper;

    @Before
    public void setup() {
        helper = new ConsoleHelper(context, console, new TempPipesLoader(), new EnglishTranslator(context));
    }

    @Test
    public void testSearch() {
        helper.onUserInput("", 0, 0);
        helper.onUserInput("k", 0, 1);
        helper.onUserInput("ka", 1, 2);

        helper.onShift();

        helper.onUserInput("k", 2, 1);
        helper.onUserInput("", 1, 0);
        helper.onUserInput("k", 0, 1);
        helper.onUserInput("ka", 1, 2);
        helper.onUserInput("ka.", 2, 3);
        helper.onUserInput("ka.c", 3, 4);
        helper.onUserInput("ka.cp", 4, 5);

        helper.onEnter();

        helper.onUserInput("", 0, 5);
        helper.onUserInput("k", 1, 0);
        helper.onUserInput("ka", 2, 1);
        helper.onUserInput("ka.", 3, 2);
        helper.onUserInput("ka.k", 3, 4);
        helper.onUserInput("ka.ka", 4, 5);
        helper.onEnter();

        helper.onUserInput("", 0, 5);
        helper.onUserInput("k", 1, 0);
        helper.onUserInput("ka", 2, 1);
        helper.onShift();
        helper.onShift();
        helper.onUserInput("ka.", 3, 2);
        helper.onUserInput("ka.k", 3, 4);
        helper.onUserInput("ka.ka", 4, 5);
        helper.onShift();
        helper.onEnter();

    }

    public class StaticConsole implements DeviceConsole {

        @Override
        public void input(String string) {
            System.out.println("console input:" + string);
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
