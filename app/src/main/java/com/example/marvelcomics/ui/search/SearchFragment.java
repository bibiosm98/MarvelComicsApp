package com.example.marvelcomics.ui.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.marvelcomics.BitmapOperation;
import com.example.marvelcomics.ui.details.DetailsFragment;
import com.example.marvelcomics.R;
import com.example.marvelcomics.requests.getImage;
import com.example.marvelcomics.requests.getJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {

    private SearchViewModel dashboardViewModel;
    private Handler mHandler = new Handler();
    private Bitmap bitmapaa;
    Map<String, Bitmap> mBitmapCache = new HashMap<>();
    private Context ct;
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
//        final View root = inflater.inflate(R.layout.fragment_search, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        ct = getContext();

        root = inflater.inflate(R.layout.fragment_search, container, false);
        root.findViewById(R.id.searchview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView sv =  root.findViewById(R.id.searchview);
//                Log.i("SEARCH",  "" + sv.getChildCount());

                TextView tv = (TextView) root.findViewById(R.id.searchTextView);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.5f
                );
                tv.setLayoutParams(param);
                tv.setText(R.string.cancelBtn);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SearchView sv =  root.findViewById(R.id.searchview);
//                        Log.i("SEARCH",  "" + sv.getChildCount());
                        TextView tv = (TextView) root.findViewById(R.id.searchTextView);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0.0f
                        );
                        tv.setLayoutParams(param);
                        tv.setText("");
                    }
                });
            }
        });

        ((SearchView)root.findViewById(R.id.searchview)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                mHandler.removeCallbacksAndMessages(null);
                getComicsAPI(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(newText.length()>0){
                            hideViews(true, "");
                        }else{
                            hideViews(false, newText);
                        }
                        getComicsAPI(newText);
                    }
                }, 1500);
                return true;
            }
        });

        return root;
    }

    private void hideViews(boolean hide, String book) {
        if(hide){
            root.findViewById(R.id.icon_book).setVisibility(View.INVISIBLE);
            root.findViewById(R.id.icon_book_text).setVisibility(View.INVISIBLE);
//            root.findViewById(R.id.searchScroll).setVisibility(View.VISIBLE);
            root.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        }else{
            root.findViewById(R.id.icon_book).setVisibility(View.VISIBLE);
            root.findViewById(R.id.icon_book_text).setVisibility(View.VISIBLE);
            book = getString(R.string.no_results) + book + getString(R.string.no_results2);
            ((TextView)root.findViewById(R.id.icon_book_text)).setText(book);
            root.findViewById(R.id.searchScroll).setVisibility(View.INVISIBLE);
            root.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        }
    }

    public void getComicsAPI(final String data) {
        LinearLayout comicsList = root.findViewById(R.id.comicsList);
        comicsList.removeAllViews();

        ((Activity)ct).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        InputMethodManager imm = (InputMethodManager) ct.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = ((Activity)ct).getCurrentFocus();
        if (view == null) {
            view = new View(ct);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        getJSON.getComics(ct , "&titleStartsWith=" + data, new getJSON.VolleyCallback() {
            @Override
            public void onSuccess(JSONArray result) {

                hideViews(result.length()!=0, data);
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
                            getComicImage(data, i, comicOnList);
//                            addComicToList(data, null); // wihtout image
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getComicImage(final JSONObject data, final int count, final JSONObject comicOnList) {
        String url = null;
        try {
            url = data.getString("thumbnail");
            url += "/portrait_xlarge.jpg";
            getImage.getImage(ct, url, new getImage.VolleyCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    root.findViewById(R.id.searchScroll).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                    mBitmapCache.put(String.valueOf(count), bitmap);
                    bitmapaa = bitmap;
                    addComicToList(data, count, comicOnList);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addComicToList(final JSONObject data, int count, final JSONObject comicOnList) {
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

            comic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                        Toast.makeText(ct, data.getString("title"), Toast.LENGTH_SHORT).show();
                    Bundle bundle=new Bundle();
                    bundle.putString("comic", comicOnList.toString());
                    Fragment newFragment = new DetailsFragment();
                    FragmentTransaction transaction = ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                    newFragment.setArguments(bundle);
                    transaction.replace(R.id.nav_host_fragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            comicsList.addView(comic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}