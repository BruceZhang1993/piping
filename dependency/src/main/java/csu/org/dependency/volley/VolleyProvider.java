package csu.org.dependency.volley;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by shinado on 15/8/27.
 */
public class VolleyProvider {

    private final String TAG = "VolleyProvider";
    private Response.ErrorListener defaultError = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError e) {
            e.printStackTrace();
            Log.d(TAG, "error:" + e.getMessage());
        }

    };

    public void handleData(String uri, final Map<String, String> params, final Class cls,
                           final Listener.Response response, final Listener.Error error) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject json) {
                Log.d(TAG, json.toString());

                Gson gson = generateGson();
                Object obj = gson.fromJson(json.toString(), cls);

                response.onResponse(obj);
            }
        };

        JSONObjectRequest jsonObjReq = new JSONObjectRequest(uri, params, "utf-8",
                listener, error == null ? defaultError : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                error.onError(volleyError.getMessage());
            }
        });

        DefaultApplication.getInstance().addToRequestQueue(jsonObjReq);

    }

    private  Gson generateGson(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .create();
    }


}
