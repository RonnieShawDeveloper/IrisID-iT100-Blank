package com.irisid.user.it100_sample.Settings;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.irisid.user.it100_sample_project.R;

import java.util.List;

public class VideoAdapter extends BaseAdapter
{
    private Context ctx;
    private List<MediaFileInfo> mediaList;

    public VideoAdapter(Context ctx, List<MediaFileInfo> mediaList)
    {
        this.ctx = ctx;
        this.mediaList = mediaList;
    }

    @Override
    public int getCount()
    {
        return mediaList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mediaList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
        {
            grid = inflater.inflate(R.layout.wallpaper_item, null);

            ImageView movie_icon_img = (ImageView) grid.findViewById(R.id.movie_icon_img);
            movie_icon_img.setVisibility(View.VISIBLE);

            ImageView movie_box_img = (ImageView) grid.findViewById(R.id.movie_box_img);
            movie_box_img.setImageResource(R.drawable.movie_box);
            movie_box_img.setVisibility(View.GONE);

            ImageView item_img = (ImageView) grid.findViewById(R.id.item_img);

            if(position == 0)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_01);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 1)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_02);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 2)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_03);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 3)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_04);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 4)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_05);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 5)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_06);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 6)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_07);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else if(position == 7)
            {
                item_img.setImageResource(R.drawable.thumb_wp_m_08);
                movie_icon_img.setImageResource(R.drawable.ic_icon_movie);
            }
            else
            {

            }

        }
        else
        {
            grid = convertView;
        }

        return grid;
    }

    public void addListItem(String file_name, Uri file_uri)
    {
        MediaFileInfo item = new MediaFileInfo();
        item.setFileName(file_name);
        item.setFileUri(file_uri);
        mediaList.add(item);
    }
}
