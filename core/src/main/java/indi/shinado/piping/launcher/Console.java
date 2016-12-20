package indi.shinado.piping.launcher;

import com.shinado.annotation.TargetVersion;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.ConsoleInfo;
import indi.shinado.piping.pipes.entity.Pipe;

public interface Console {

    /**
     * to TYPE in a string, blocking input
     */
    void input(String string);

    void replaceCurrentLine(String line);

    void blockInput();

    void releaseInput();

    void clear();

    @TargetVersion(4)
    void intercept();

    @TargetVersion(4)
    String getLastInput();

    /**
     * after user press ENTER key
     */
    @TargetVersion(4)
    void waitForSingleLineInput(SingleLineInputCallback inputCallback);

    /**
     * for single character input
     */
    @TargetVersion(4)
    void waitForCharacterInput(CharacterInputCallback inputCallback);

    /**
     * for system key such as BACK, MENU, etc.
     */
    @TargetVersion(4)
    void waitForKeyDown(KeyDownCallback inputCallback);

    /**
     * to display a string immediately, without blocking input
     */
    @TargetVersion(4)
    void display(String string);

    /**
     * under this mode, you won't get any result from system
     * However, you will still find your input in the console
     */
    @TargetVersion(4)
    void occupyMode();

    @TargetVersion(4)
    void quitOccupy();

    /**
     * under this mode, any input will not be received
     * whatever you type will not be displayed
     */
    @TargetVersion(4)
    void blindMode();

    @TargetVersion(4)
    void quitBlind();

    @TargetVersion(4)
    void notifyUI();

    @TargetVersion(4)
    void addInputCallback(InputCallback inputCallback);

    @TargetVersion(4)
    void removeInputCallback(InputCallback inputCallback);

    @TargetVersion(4)
    void setIndicator(String indicator);

}
