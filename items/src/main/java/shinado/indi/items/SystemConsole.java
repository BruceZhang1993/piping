package shinado.indi.items;

import android.util.Log;

import java.util.Collection;

import indi.shinado.piping.launcher.CharacterInputCallback;
import indi.shinado.piping.launcher.InputCallback;
import indi.shinado.piping.launcher.KeyDownCallback;
import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.launcher.impl.DeviceConsole;
import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.ConsoleInfo;
import indi.shinado.piping.pipes.entity.Pipe;

public class SystemConsole  implements DeviceConsole {

    public static final String TAG = "SystemConsole";

    @Override
    public void input(String string) {
        log("console input:" + string);
    }

    @Override
    public void replaceCurrentLine(String line) {
        log("replace:" + line);
    }

    @Override
    public void blockInput() {
        log("block input");
    }

    @Override
    public void releaseInput() {
        log("release input");
    }

    @Override
    public void clear() {
        log("clear");
    }

    @Override
    public void intercept() {

    }

    @Override
    public String getLastInput() {
        return null;
    }

    @Override
    public void waitForSingleLineInput(SingleLineInputCallback inputCallback) {

    }

    @Override
    public void waitForCharacterInput(CharacterInputCallback inputCallback) {

    }

    @Override
    public void waitForKeyDown(KeyDownCallback inputCallback) {

    }

    @Override
    public void display(String string) {

    }

    @Override
    public ConsoleInfo getConsoleInfo() {
        return null;
    }

    @Override
    public void occupyMode() {

    }

    @Override
    public void quitOccupy() {

    }

    @Override
    public void hideInitText() {

    }

    @Override
    public void showInitText() {

    }

    @Override
    public void blindMode() {

    }

    @Override
    public void quitBlind() {

    }

    @Override
    public void notifyUI() {

    }

    @Override
    public BasePipe getPipeById(int id) {
        return null;
    }

    @Override
    public void startTutorial() {

    }

    @Override
    public void addInputCallback(InputCallback inputCallback) {

    }

    @Override
    public void removeInputCallback(InputCallback inputCallback) {

    }

    @Override
    public void onSystemReady(Collection<BasePipe> pipes) {

    }

    @Override
    public void displayResult(Collection<Pipe> pipe) {

    }

    @Override
    public void displayPrevious(Pipe pipe) {
        log("display previous:" + pipe.getDisplayName());
    }

    @Override
    public void onEnter(Pipe pipe) {
        log("enter:" + pipe.getDisplayName());
    }

    @Override
    public void onSelected(Pipe pipe) {
        log("shift:" + pipe.getDisplayName());
    }

    @Override
    public void onNothing() {
        log("nothing to show");
    }

    public void log(String msg){
        Log.d(TAG, msg);
    }
}
