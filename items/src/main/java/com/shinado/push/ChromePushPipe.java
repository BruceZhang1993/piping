package com.shinado.push;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class ChromePushPipe extends DefaultInputActionPipe {

    private static final String URL = "http://1.yilaunch.sinaapp.com/pipush/pull.php";

    public ChromePushPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$push";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"push"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        request(callback);
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        request(callback);
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        request(callback);
    }

    private void request(final OutputCallback callback) {
        getConsole().blockInput();

        new VolleyProvider().handleData(URL, null, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        if (obj != null) {
                            getConsole().releaseInput();
                            callback.onOutput(obj.item);
                        } else {
                            getConsole().releaseInput();
                            callback.onOutput("error");
                        }
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        getConsole().releaseInput();
                        callback.onOutput(msg);
                    }
                });
    }

    public class Result {
        public String item;

        public Result(String item) {
            this.item = item;
        }
    }
}
