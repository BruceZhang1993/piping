package indi.shinado.piping.launcher;

import android.content.Context;
import android.view.View;

import indi.shinado.piping.launcher.impl.ConsoleHelper;

public interface IOHelper {

    /**
     * dang dang dang~ dang~dang~~dang~~~dang dang dang dang dang dang dang dang dang dang dang dang~~dangdangdangdangdangdangdang~~~
     */
    public void connect(Context context, View view, ConsoleHelper helper);

    public void blockInput();

    public void releaseInput();

    public void clearInput();

    public String getCurrentUserInput();

    public void startInput();

    public void restartInput();
}
