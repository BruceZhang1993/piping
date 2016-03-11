package indi.shinado.piping.launcher;

import android.content.Context;
import android.view.View;

import indi.shinado.piping.launcher.impl.ConsoleHelper;

public interface IOHelper {

    /**
     * dang dang dang~ dang~dang~~dang~~~dang dang dang dang dang dang dang dang dang dang dang dang~~dangdangdangdangdangdangdang~~~
     */
    void connect(Context context, View view, ConsoleHelper helper);

    void blockInput();

    void releaseInput();

    void clearInput();

    String getCurrentUserInput();

    void startInput();

    void restartInput();
}
