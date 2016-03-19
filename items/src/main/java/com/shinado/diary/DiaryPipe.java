package com.shinado.diary;


import java.util.HashMap;
import java.util.TreeSet;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.pipes.action.DefaultInputActionPipe;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

public class DiaryPipe extends DefaultInputActionPipe {

    private static final String LS = "ls";
    private static final String URL = "http://1.yilaunch.sinaapp.com/diary";

    public DiaryPipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$diary";
    }

    @Override
    public SearchableName getSearchable() {
        return new SearchableName(new String[]{"diary"});
    }

    @Override
    public void onParamsEmpty(Pipe rs, OutputCallback callback) {
        callback.onOutput("Diary is empty!");
    }

    @Override
    public void onParamsNotEmpty(Pipe rs, OutputCallback callback) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1){
            callback.onOutput("$diary takes only one param, others ignored.");
        }

        if (params[0].equals(LS)){
            list(callback);
        }
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, OutputCallback callback) {
        add(input, callback);
    }

    private void list(final OutputCallback callback){
        getConsole().blockInput();

        new VolleyProvider().handleData(URL + "/list.php", null, Result.class,
                new Listener.Response<Result>() {

                    @Override
                    public void onResponse(Result obj) {
                        getConsole().releaseInput();
                        StringBuilder sb = new StringBuilder();
                        int i=0;
                        for (Result.Diary item : obj.list){
                            sb.append("------").append(item.date.substring(0, 10)).append("------").append("\n");
                            sb.append(item.memo);
                            sb.append("\n");
                            sb.append("Rating: ").append(item.rating).append("\n");
                        }
                        callback.onOutput(sb.toString());
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

    private void add(String input, final OutputCallback callback) {
        if (input.isEmpty()){
            callback.onOutput("Diary is empty!");
            return;
        }

        getConsole().blockInput();
        String[] split = input.split("@");
        int rating = 1024;
        String memo = split[0];
        if (split.length > 1){
            try {
                rating = Integer.parseInt(split[1]);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("memo", memo);
        params.put("rating", ""+rating);

        new VolleyProvider().handleData(URL + "/add.php", params, AddResult.class,
                new Listener.Response<AddResult>() {

                    @Override
                    public void onResponse(AddResult obj) {
                        getConsole().releaseInput();

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

    public class AddResult{
        public int result;
    }

    public class Result{

        public TreeSet<Diary> list;

        public class Diary implements Comparable<Diary>{
            public String memo;
            public int id;
            public int rating;
            public String date;

            @Override
            public int compareTo(Diary another) {
                return date.compareTo(another.date);
            }
        }
    }
}
