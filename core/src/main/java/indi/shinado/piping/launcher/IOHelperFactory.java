package indi.shinado.piping.launcher;

public class IOHelperFactory {

    public IOHelper getInstance(){
        return new InputMethodIOHelper();
    }

}
