package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.irisid.user.it100_sample_project.R;

import java.util.ArrayList;
import java.util.List;

public class TabMoviesFragment extends Fragment
{
    GridView gridView;
    View lastView = null;

    private List<MediaFileInfo> mediaList = new ArrayList<MediaFileInfo>();
    private VideoAdapter mediaAdapter;

    Uri videoClickFileUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab_fragment_movies, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);

        mediaAdapter = new VideoAdapter(getContext(), mediaList);
        for(int i = 1 ; i < 9; i++)
        {
            String file_name = "wp_m_0" + i +".mp4";
            Uri file_uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/wp_m_0" + i);
            mediaAdapter.addListItem(file_name, file_uri);

        }

        gridView.setAdapter(mediaAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                videoClickFileUri = mediaList.get(position).getFileUri();

                if(lastView == null)
                {
                    ImageView movie_box_img = (ImageView) view.findViewById(R.id.movie_box_img);
                    movie_box_img.setVisibility(View.VISIBLE);
                    lastView = view;
                }
                else
                {
                    ImageView movie_box_img = (ImageView) lastView.findViewById(R.id.movie_box_img);
                    movie_box_img.setVisibility(View.GONE);

                    ImageView movie_box_img2 = (ImageView) view.findViewById(R.id.movie_box_img);
                    movie_box_img2.setVisibility(View.VISIBLE);

                    lastView = view;
                }

                String image_clear = null;

                SharedPreferences pref = getActivity().getSharedPreferences(
                        getString(R.string.prefer_name_wallpaper_path), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getString(R.string.prefer_key_image_filepath), image_clear);
                editor.putString(getString(R.string.prefer_key_video_filepath), videoClickFileUri.toString());
                editor.commit();

            }
        });
        return view;
    }
}
