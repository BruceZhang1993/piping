package com.shinado.piping;

import org.junit.Assert;
import org.junit.Test;

import indi.shinado.piping.pipes.BasePipe;
import indi.shinado.piping.pipes.action.ActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.util.android.FunctionUtil;

/**
 * Created by shinado on 2016/2/29.
 */
public class TestActionPipe extends ActionPipe{

    Pipe mResult;

    public TestActionPipe(){
        super(11);
        mResult = new Pipe();
        mResult.setId(id);
        mResult.setDisplayName("$copy");
        mResult.setSearchableName(new SearchableName(new String[]{"co", "py"}));
        mResult.setBasePipe(this);
    }

    @Override
    protected Pipe getResult() {
        return mResult;
    }

    @Override
    public void acceptInput(Pipe result, String input) {
        getConsole().input(result.getDisplayName() + " accepting input:" + input);
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        callback.onOutput("getting output from:" + result.getDisplayName());
    }

    @Override
    protected void execute(Pipe rs) {

    }

    @Test
    public void testSearch(){
        Pipe pipe = search("");
        Assert.assertEquals(true, pipe == null);

        pipe = search("c");
        Assert.assertEquals(false, pipe == null);

        pipe = search("cp");
        Assert.assertEquals(false, pipe == null);

        pipe = search("cp.");
        Assert.assertEquals(true, pipe == null);

        pipe = search("cp.k");
        Assert.assertEquals(true, pipe == null);

        pipe = search("cp.c");
        Assert.assertEquals(false, pipe == null);
    }
}
