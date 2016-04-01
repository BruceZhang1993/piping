package indi.shinado.piping.launcher;

import com.shinado.annotation.TargetVersion;

@TargetVersion(3)
public interface InputCallback {
    void onInput(String character);
}
