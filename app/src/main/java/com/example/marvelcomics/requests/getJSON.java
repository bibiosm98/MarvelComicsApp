package com.example.marvelcomics.requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.marvelcomics.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.net.Proxy.Type.HTTP;

public class getJSON {
    final static String TAG = "Comics request";
    final static String API = "https://gateway.marvel.com/v1/public/comics";

    private static String createGetWithParams(String url, Map<String, Object> params)
    {
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet())
        {
            Object value = params.get(key);
            if (value != null)
            {
                if (builder.length() > 0)
                    builder.append("&");
                builder.append(key).append("=").append(value);
            }
        }

        return (url += "?" + builder.toString());
    }
    public static void getComics(final Context context, final String link, final VolleyCallback callback){

//        final ProgressDialog loading = new ProgressDialog(context);
//        loading.setMessage("Please Wait...");
//        loading.setCanceledOnTouchOutside(false);
//        loading.show();

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> params2 = new HashMap<String, Object>();
        params.put("ts", "1");
        params.put("apikey", "080a502746c8a60aeab043387a56eef0");
        params.put("hash", "6edc18ab1a954d230c1f03c590d469d2");
        params.put("limit", "20");
        params.put("offset", "0");

        String link2 = createGetWithParams(API, params);
//        Toast.makeText(context, link2+link , Toast.LENGTH_LONG).show();
        Log.i("TAG", ""+link2+link);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, link2+link, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(context, API+link , Toast.LENGTH_LONG).show();
                JSONArray js;
                try {
                    Log.i(TAG, response.toString(3));
                    js = response.getJSONObject("data").getJSONArray("results");
                    if(js.length()>0) Log.i(TAG, ((JSONObject) js.get(0)).toString(3));
                    callback.onSuccess(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                loading.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
//        request.setShouldCache(false);
        queue.add(request);

    }
    public interface VolleyCallback{
        void onSuccess(JSONArray result);
    }
}
