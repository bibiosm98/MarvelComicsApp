package com.example.marvelcomics.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.marvelcomics.BitmapOperation;
import com.example.marvelcomics.MainActivity;
import com.example.marvelcomics.R;
import com.example.marvelcomics.requests.getImage;
import com.example.marvelcomics.requests.getJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Context ct;
    Bitmap bitmapaa;
    Map<String, Bitmap> mBitmapCache = new HashMap<>();
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        addTestComics();
        ct = getContext();
        getComicsAPI();
        return root;
    }

    private void addTestComics() {
        ConstraintLayout comic;
        LinearLayout comicsList = root.findViewById(R.id.comicsList);
        for(int i=0; i<5; i++){
            comic =  (ConstraintLayout) getLayoutInflater().inflate(R.layout.comic_on_list, null);
            comicsList.addView(comic);
        }
    }

    public void getComicsAPI() {
        getJSON.getComics(ct , "", new getJSON.VolleyCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                for(int i=0; i<result.length(); i++){
                    JSONObject comicOnList;
                    JSONObject data = new JSONObject();
                    String title = "",
                            writer = "written by ",
                            description = "",
                            thumbnail = "";
                    try {
                        comicOnList = (JSONObject) result.get(i);
                        title = comicOnList.getString("title");
                        JSONArray jsonArray = comicOnList.getJSONObject("creators").getJSONArray("items");
                        for (int y=0; y<jsonArray.length(); y++) {
                            String wr = ((JSONObject)jsonArray.get(y)).getString("role");
                            if(wr.equals("writer")) {
                                if(writer.length()>"written by ".length()){
                                    writer += ", " + ((JSONObject)jsonArray.get(y)).getString("name");
                                }else{
                                    writer += ((JSONObject)jsonArray.get(y)).getString("name");
                                }
                            }
                        }
                        description = comicOnList.getString("description");
                        if(writer.equals("written by ")) writer = "unknown";
                        if(description.equals("null")) description = "unknown";

                        thumbnail = ((JSONObject)comicOnList.getJSONObject("thumbnail")).getString("path");

                        data.put("title", title);
                        data.put("writer", writer);
                        data.put("description", description);
                        data.put("thumbnail", thumbnail);

//                      if(title.startsWith("Ant")){
                       if (title.length() < 30) {
//                       if (true) {
                            getComicImage(data, i);
//                            addComicToList(data, null); // wihtout image
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getComicImage(final JSONObject data, final int count) {
        String url = null;
        try {
            url = data.getString("thumbnail");
            url += "/portrait_xlarge.jpg";
//            portrait_small 	50x75px
//            portrait_medium 	100x150px
//            portrait_xlarge 	150x225px
//            portrait_fantastic 	168x252px
//            portrait_uncanny 	300x450px
//            portrait_incredible 	216x324px

            getImage.getImage(ct, url, new getImage.VolleyCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    mBitmapCache.put(String.valueOf(count), bitmap);
                    bitmapaa = bitmap;
                    addComicToList(data, count);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addComicToList(JSONObject data, int count) {
        LinearLayout comicsList = root.findViewById(R.id.comicsList);
        ConstraintLayout comic;
        comic = (ConstraintLayout) getLayoutInflater().inflate(R.layout.comic_on_list, null);

        try {
            String
                    title="",
                    writer="",
                    description="";
            title = data.getString("title");
            writer = data.getString("writer");
            description = data.getString("description");


            ConstraintLayout textConstraint = ((ConstraintLayout) ((ConstraintLayout) comic.getChildAt(0)).getChildAt(1));
            TextView tv = (TextView) textConstraint.getChildAt(0);
            tv.setText(title);

            tv = (TextView) textConstraint.getChildAt(1);
            tv.setText(writer);

            tv = (TextView) textConstraint.getChildAt(2);

            if (description.length() > 100) description = description.substring(0, 100);
            tv.setText(description);
//            Bitmap bitmap = mBitmapCache.get(""+count);
            Bitmap bitmap = bitmapaa;
            if(bitmap!= null)
            ((ConstraintLayout)comic.getChildAt(0)).getChildAt(0).setBackground(BitmapOperation.scaleImage(ct, bitmap));
            comicsList.addView(comic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}