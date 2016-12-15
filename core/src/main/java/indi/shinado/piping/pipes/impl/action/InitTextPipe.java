package indi.shinado.piping.pipes.impl.action;

import android.content.Context;

import indi.shinado.piping.launcher.functionality.IText;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class InitTextPipe extends DefaultInputActionPipe{

    public InitTextPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$Keep";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName("keep");
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        Context launcher = getLauncher();
        if (launcher instanceof IText){
            ((IText) launcher).setInitText(input);
        }else {
            getConsole().input("Not supported. ");
        }
    }

}
