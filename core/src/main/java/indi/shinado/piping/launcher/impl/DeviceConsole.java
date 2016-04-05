package indi.shinado.piping.launcher.impl;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.Pipe;

public interface DeviceConsole extends Console {

    void onSystemReady();

    void displayResult(Pipe pipe);

    void displayPrevious(Pipe pipe);

    void onEnter(Pipe pipe);

    void onShift(Pipe pipe);

    void onNothing();

}
