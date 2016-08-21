package com.shinado.shell;

import indi.shinado.piping.launcher.UserInputCallback;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.impl.ShellUtil;

/**
 * Created by shinado on 16/5/7.
 */
public class ShellPipe extends DefaultInputActionPipe{

    private static final String NAME = "$shell";
    private static final String HELP = "Please use shell to enter shell mode";

    public ShellPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"shell"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        getConsole().input("You will enter shell mode, enter exit to quit");
        loopForUserInput();
    }

    private void loopForUserInput(){
        getConsole().waitForUserInput(new UserInputCallback() {
            @Override
            public void onUserInput(String userInput) {
                if (!userInput.equals("exit")){
                    ShellUtil.CommandResult result = ShellUtil.execCommand(userInput, false);
                    if (result.errorMsg == null || result.errorMsg.isEmpty()){
                        getConsole().input(result.errorMsg);
                    }else{
                        getConsole().input(result.successMsg);
                    }
                    loopForUserInput();
                }
            }
        });
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        getConsole().input(HELP);
    }

    @Override
    public void acceptInput(Pipe rs, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        getConsole().input(HELP);
    }

}
