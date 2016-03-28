package indi.shinado.piping.launcher.impl;

import com.shinado.annotation.TargetVersion;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.ConsoleInfo;
import indi.shinado.piping.pipes.entity.Pipe;

public interface DeviceConsole extends Console {

    void onSystemReady();

    void displayResult(Pipe pipe);

    void displayPrevious(Pipe pipe);

    void onEnter(Pipe pipe);

    void onShift(Pipe pipe);

    void onNothing();

}
