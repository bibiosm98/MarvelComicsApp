package com.example.marvelcomics.requests;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class getImage {
    public static void getImage(final Context context, String url, final VolleyCallback callback){
//        Log.i("image", url);
        com.android.volley.toolbox.ImageRequest request = new com.android.volley.toolbox.ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        callback.onSuccess(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Image error", error.toString());
                    }
                }){
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }
    public interface VolleyCallback{
        void onSuccess(Bitmap bitmap);
    }
}
