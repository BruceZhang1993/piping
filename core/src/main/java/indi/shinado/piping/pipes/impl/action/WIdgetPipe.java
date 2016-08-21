package indi.shinado.piping.pipes.impl.action;

import android.app.Activity;
import android.content.Intent;

import com.shinado.annotation.TargetVersion;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

@TargetVersion(4)
public class WidgetPipe extends DefaultInputActionPipe{

    private static final String NAME = "$widget-store";

    public WidgetPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"widget", "store"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        Activity activity = getLauncher();
        activity.startActivityForResult(
                new Intent("com.shinado.piping.shopping"),
                10
        );
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }
}
