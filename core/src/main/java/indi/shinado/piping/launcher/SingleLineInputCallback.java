package indi.shinado.piping.launcher;

import com.shinado.annotation.TargetVersion;

@TargetVersion(4)
public interface SingleLineInputCallback {
    void onUserInput(String userInput);
}
