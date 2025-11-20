package com.irisid.user.it100_sample.Settings;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.irisid.user.it100_sample_project.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TabImagesFragment extends Fragment
{
    GridView gridView;
    View lastView = null;

    private List<MediaFileInfo> mediaList = new ArrayList<MediaFileInfo>();

    private ImageAdapter imageAdapter;

    Uri ImageClickFileUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab_fragment_images, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);

        imageAdapter = new ImageAdapter(getContext(), mediaList);

        for(int i = 1 ; i < 10; i++) {
            String file_name = "wp_p_0" + i +".mp4";
            Uri file_uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/drawable/wp_p_0" + i);
            imageAdapter.addListItem(file_name, file_uri);

        }

        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ImageClickFileUri = mediaList.get(position).getFileUri();

                if(lastView == null) {
                    ImageView movie_box_img = (ImageView) view.findViewById(R.id.movie_box_img);
                    movie_box_img.setVisibility(View.VISIBLE);
                    lastView = view;
                } else {
                    ImageView movie_box_img = (ImageView) lastView.findViewById(R.id.movie_box_img);
                    movie_box_img.setVisibility(View.GONE);

                    ImageView movie_box_img2 = (ImageView) view.findViewById(R.id.movie_box_img);
                    movie_box_img2.setVisibility(View.VISIBLE);

                    lastView = view;
                }

                String video_clear = null;

                // save to sharedpreferences >> read for Main, idpw, system
                SharedPreferences pref = getActivity().getSharedPreferences(
                        getString(R.string.prefer_name_wallpaper_path), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getString(R.string.prefer_key_image_filepath), ImageClickFileUri.toString());
                editor.putString(getString(R.string.prefer_key_video_filepath), video_clear);
                editor.apply();

                WallpaperManager wm = WallpaperManager.getInstance(getActivity());
                try{
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(ImageClickFileUri);
                   //wm.setResource(Drawable.createFromStream(inputStream, ImageClickFileUri.toString() ));

                   wm.setStream(inputStream);
                }catch(Exception e){

                }
            }
        });

        return rootView;
    }
}
