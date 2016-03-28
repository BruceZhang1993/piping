package indi.shinado.piping.launcher;

import com.shinado.annotation.TargetVersion;

public interface Console {

    void input(String string);

    void replaceCurrentLine(String line);

    void blockInput();

    void releaseInput();

    void clear();

    @TargetVersion(3)
    void intercept();

    @TargetVersion(4)
    String getLastInput();

    @TargetVersion(4)
    void waitForUserInput(UserInputCallback inputCallback);
}
