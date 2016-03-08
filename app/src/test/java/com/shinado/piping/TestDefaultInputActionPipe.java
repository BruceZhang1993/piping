package com.shinado.piping;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
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


}
