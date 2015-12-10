package csu.org.dependency.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class JSONObjectRequest extends Request<JSONObject> {

	private Listener<JSONObject> listener;
	private Map<String, String> params;
	private String charset = "utf-8";

	public JSONObjectRequest(String url, Map<String, String> params, String charset,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		Log.d(TAG, "url:"+url);
		this.listener = reponseListener;
		this.params = params;
		this.charset = charset;
	}

	public JSONObjectRequest(String url, Map<String, String> params,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		Log.d(TAG, "url:"+url);
		this.listener = reponseListener;
		this.params = params;
	}

	protected Map<String, String> getParams()
			throws AuthFailureError {
		Log.d(TAG, "params:");
		if(params != null){
			Object[] values = params.values().toArray();
			int i=0;
			for(String key:params.keySet()){
				Log.d(TAG, key + "," + values[i]);
				i++;
			}
		}
		return params;
	};

	private final String TAG = "HttpRequest-Provider";

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			Log.d(TAG, "charset:"+charset);
			String jsonString = new String(response.data, charset);
//					HttpHeaderParser.parseCharset(response.headers));
			Log.d(TAG, "result:"+jsonString);
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	/**
	 * Passing some request headers
	 * */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		return headers;
	}

	@Override
	public String getBodyContentType() {
		return "application/json; charset="+charset;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		// TODO Auto-generated method stub
		listener.onResponse(response);
	}
}