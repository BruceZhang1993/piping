package com.shinado.piping;

import indi.shinado.piping.launcher.SingleLineInputCallback;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class TestPythonActionPipe extends DefaultInputActionPipe{

    public TestPythonActionPipe() {
        super(25);
    }

    @Override
    public String getDisplayName() {
        return "$python";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"python"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        getConsole().waitForSingleLineInput(mCallback);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {

    }

    private SingleLineInputCallback mCallback = new SingleLineInputCallback() {
        @Override
        public void onUserInput(String userInput) {
            System.out.println("python get user input: " + userInput);
            if (userInput.equals("quit") || userInput.equals("exit")){
                getConsole().input("You've quit");
            }else {
                getConsole().waitForSingleLineInput(mCallback);
            }
        }
    };
}
