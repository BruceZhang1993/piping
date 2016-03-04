package indi.shinado.piping.launcher.impl;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.Pipe;

public interface DeviceConsole extends Console {

    public void onSystemReady();

    public void displayResult(Pipe pipe);

    public void displayPrevious(Pipe pipe);

    public void onEnter(Pipe pipe);

    public void onShift(Pipe pipe);

    public void onNothing();
}
