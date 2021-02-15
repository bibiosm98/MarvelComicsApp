package com.example.marvelcomics.ui.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marvelcomics.BitmapOperation;
import com.example.marvelcomics.R;
import com.example.marvelcomics.requests.getImage;
import com.example.marvelcomics.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsFragment extends Fragment {

    private DetailsVievModel detailsVievModel;
    View root;
    Context ct;
    JSONObject comic;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
            detailsVievModel =
                ViewModelProviders.of(this).get(DetailsVievModel.class);
        root = inflater.inflate(R.layout.fragment_details, container, false);


        ct = getContext();
        Toast.makeText(ct, "DETAILS", Toast.LENGTH_SHORT).show();
//        Toast.makeText(ct, getArguments().getString("comic"), Toast.LENGTH_SHORT).show();
        try {
            comic = new JSONObject(getArguments().getString("comic"));
            displayData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        root.findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
            }
        });

        return root;
    }


    private void displayData() {
        String url = null;
        try {
            url = comic.getJSONObject("thumbnail").getString("path");
            url += "/landscape_incredible.jpg";
            getImage.getImage(ct, url, new getImage.VolleyCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    if(bitmap!= null)
                        root.findViewById(R.id.comicImage).setBackground(BitmapOperation.scaleImage(ct, bitmap));
                }
            });

            final String webUrl = ((JSONObject)comic.getJSONArray("urls").get(0)).getString("url"); // tutaj wyszukać i sprawdzić ktory w liście będzie "detail" //TODO
            root.findViewById(R.id.web_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + webUrl));
                    startActivity(browserIntent);
                }
            });

            String
                    title="",
                    writer="",
                    description="";
            title = comic.getString("title");
            description = comic.getString("description");


            JSONArray jsonArray = comic.getJSONObject("creators").getJSONArray("items");
            for (int y=0; y<jsonArray.length(); y++) {
                String wr = ((JSONObject)jsonArray.get(y)).getString("role");
                if(wr.equals("writer")) {
                    if(writer.length()>0){
                        writer += ", " + ((JSONObject)jsonArray.get(y)).getString("name");
                    }else{
                        writer += ((JSONObject)jsonArray.get(y)).getString("name");
                    }
                }
            }

            TextView tv = (TextView) root.findViewById(R.id.comicTitle);
            tv.setText(title);

            tv = (TextView) root.findViewById(R.id.comicAuthor);
            tv.setText(writer);

            tv = (TextView) root.findViewById(R.id.description);
            tv.setText(description);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}