package indi.shinado.piping.pipes.impl.action;


import com.shinado.annotation.TargetVersion;

import java.util.HashMap;
import java.util.TreeSet;

import csu.org.dependency.volley.Listener;
import csu.org.dependency.volley.VolleyProvider;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;

/**
 * diary pipe updated targeting version 4, adding user
 */
@TargetVersion(4)
public class DiaryV4Pipe extends UserRequireAction {

    private static final String LS = "ls";
    private static final String URL = "http://1.yilaunch.sinaapp.com/diary";

    public DiaryV4Pipe(int id) {
        super(id);
    }

    @Override
    public String getDisplayName() {
        return "$diary4";
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
    public void onParamsNotEmpty(Pipe rs, final OutputCallback callback) {
        String[] params = rs.getInstruction().params;
        if (params.length > 1){
            callback.onOutput("$diary takes only one param, others ignored.");
        }

        if (params[0].equals(LS)){
            verifyUser(new OnUserCallback() {
                @Override
                public void onUser(String userName) {
                    list(userName, callback);
                }
            });
        }
    }

    @Override
    public void acceptInput(Pipe result, final String input, Pipe.PreviousPipes previous, final OutputCallback callback) {
        verifyUser(new OnUserCallback() {
            @Override
            public void onUser(String userName) {
                add(input, userName, callback);
            }
        });
    }

    private void list(String userName, final OutputCallback callback){
        getConsole().blockInput();

        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);

        new VolleyProvider().handleData(URL + "/list_v4.php", params, Result.class,
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

    private void add(String input, String userName, final OutputCallback callback) {
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
        params.put("userName", userName);

        new VolleyProvider().handleData(URL + "/add_v4.php", params, AddResult.class,
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
                return another.date.compareTo(date);
            }
        }
    }
}
