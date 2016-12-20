package indi.shinado.piping.launcher.impl;

import java.util.Collection;
import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.entity.Pipe;

public interface DeviceConsole extends Console {

    void onSystemReady();

    void displayResult(Collection<Pipe> pipe);

    void displayPrevious(Pipe pipe);

    void onEnter(Pipe pipe);

    void onSelected(Pipe pipe);

    void onNothing();

}
