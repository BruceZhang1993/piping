package com.shinado.piping;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public abstract class TestDefaultInputActionPipe extends DefaultInputActionPipe{

    public TestDefaultInputActionPipe() {
        super(24);
    }

    @Override
    public String getDisplayName() {
        return "$test";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"test"});
    }

    @Override
    public void onEmpty(Pipe rs, IInput input) {
        input.input("empty");
    }

    @Override
    public void onParamsEmpty(Pipe rs, IInput input) {
        input.input("params empty");
    }

    @Override
    public void onPreEmpty(Pipe rs, IInput input) {
        input.input("pre empty");
    }

    @Override
    public void onNoEmpty(Pipe rs, IInput input) {
        input.input("full");
    }

}
