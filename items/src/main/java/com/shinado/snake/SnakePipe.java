package com.shinado.snake;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class SnakePipe extends DefaultInputActionPipe implements Console{


    public SnakePipe(int id) {
        super(id);
    }

    @Override
    public void draw(byte[][] matrix) {

    }

    @Override
    public String getDisplayName() {
        return "$snake";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"snake"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }
}
