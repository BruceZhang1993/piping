package com.shinado.example;

import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class SamplePipe extends DefaultInputActionPipe{

    public SamplePipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$sample";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"sam", "ple"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        //e.g. sample
        callback.onOutput("both pre and param are null");
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, final OutputCallback callback) {
        //e.g. sample -ls
        //an example for loading in another thread

        //do call this to block user input
        getConsole().blockInput();

        new Thread(){
            public void run(){
                //do something for long time
                //do remember to call releaseInput() after blockInput()
                if (callback == getConsoleCallback()){
                    getConsole().releaseInput();
                    callback.onOutput("This will be displayed in the terminal");
                }else{
                    getConsole().releaseInput();
                    callback.onOutput("This will be taken as input for next pipe");
                }
            }
        }.start();

    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        if (previous == null){
            callback.onOutput("accept input from user input:"+input);
        }else {
            callback.onOutput("accept input from previous pipe:"+input);
        }
    }
}
