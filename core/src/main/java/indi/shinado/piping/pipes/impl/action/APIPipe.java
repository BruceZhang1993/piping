package indi.shinado.piping.pipes.impl.action;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import csu.org.dependency.volley.DefaultApplication;
import csu.org.dependency.volley.JSONObjectRequest;
import indi.shinado.piping.pipes.entity.Pipe;
import indi.shinado.piping.pipes.entity.SearchableName;
import indi.shinado.piping.pipes.search.SearchablePipe;
import indi.shinado.piping.pipes.search.translator.AbsTranslator;

/**
 * format
 * {}
 * name(key1:value, key2:?...)=[url]: [filter...]
 */
public class APIPipe extends SearchablePipe {

    private int ID = 0;
    private static String TAG = "APIPipe";
    private DatabaseReference mDatabase;

    public APIPipe(int id) {
        super(id);
    }

    @Override
    public void destroy() {

    }

    @Override
    public Pipe getByValue(String value) {
        return null;
    }

    @Override
    public void acceptInput(Pipe result, String input, Pipe.PreviousPipes previous, final OutputCallback callback) {
        if (result.getId() == 0){
            addAPI(input);
        }else {
            callAPI(input, result.getExecutable(), callback);
        }
    }

    @Override
    public void getOutput(Pipe result, OutputCallback callback) {
        if (result.getId() == 0){
            callback.onOutput("Should take input");
        }else {
            callAPI("", result.getExecutable(), callback);
        }
    }

    private void addAPI(String input) {
        API api = new Gson().fromJson(input, API.class);
        mDatabase.push().setValue(api);
    }

    private void callAPI(String input, String exe, final OutputCallback callback) {
        final API api;
        try {
            api = new Gson().fromJson(exe, API.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();

            callback.onOutput("Format wrong. ");
            return;
        }

        String[] p = input.split(",");
        int i = 0;
        Map<String, String> params = new HashMap<>();
        for (String key : api.params.keySet()) {
            String value = api.params.get(key);
            if (value.equals("?")) {
                if (i < p.length) {
                    params.put(key, p[i]);
                } else {
                    //error
                }
            } else {
                params.put(key, value);
            }
            i++;
        }

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject json) {
                Log.d(TAG, json.toString());

                if (api.filter != null) {
                    JSONObject newJson = new JSONObject();
                    for (String key : api.filter){
                        try {
                            newJson.put(key, json.get(key));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onOutput("Error. json key not found:" + key);
                        }
                    }
                    callback.onOutput(newJson.toString());
                } else {
                    callback.onOutput(json.toString());
                }
            }
        };

        JSONObjectRequest jsonObjReq = new JSONObjectRequest(api.url, params, "utf-8",
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onOutput(volleyError.getMessage());
            }
        });

        DefaultApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    protected void execute(Pipe rs) {
        if (rs.getId() == 0){
            //do nothing
            getConsole().input("Should take input");
        }else {
            callAPI("", rs.getExecutable(), getConsoleCallback());
        }
    }

    @Override
    public void load(AbsTranslator translator, OnItemsLoadedListener listener, int total) {
        WifiManager manager = (WifiManager) getLauncher().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        Pipe pipe = new Pipe(ID++, "$addAPI", new SearchableName("add", "api"), "");
        putItemInMap(pipe);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("local").child(address).child("Apis");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    API item = dataSnapshot.getValue(API.class);
                    Pipe pipe = new Pipe(ID++, "#" + item.name, new SearchableName(item.name), new Gson().toJson(item));
                    putItemInMap(pipe);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listener.onItemsLoaded(getId(), total);
    }

    class API {
        String name;
        HashMap<String, String> params;
        String url;
        String[] filter;
    }
}
