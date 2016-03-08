package indi.shinado.piping.launcher;

public interface Console {

    public void input(String string);

    public void replaceCurrentLine(String line);

    public void blockInput();

    public void releaseInput();

}