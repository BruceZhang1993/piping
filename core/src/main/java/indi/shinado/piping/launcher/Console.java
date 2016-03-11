package indi.shinado.piping.launcher;

public interface Console {

    void input(String string);

    void replaceCurrentLine(String line);

    void blockInput();

    void releaseInput();

    void clear();

}
