package com.irisid.user.it100_sample.UserList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Item> itemArrayList = new ArrayList<>();
//    DatabaseHelper databaseHelper;

    String search_text = "";

    int selected_position = -1 ;

    public ListViewAdapter(){}

    public ListViewAdapter(Context context, ArrayList<Item> data)
    {
        this.context = context;
        this.itemArrayList = data;
        //loadDataFromDB();
    }

    @Override
    public int getCount()
    {
        return itemArrayList.size();
    }

    public void set_search_text(String search_text)
    {
        //Log.e("TAG >> ", "search_text : " + search_text);
        this.search_text = search_text;
    }

    @Override
    public Object getItem(int position)
    {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Context context = parent.getContext();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        ImageView picture_img = (ImageView) convertView.findViewById(R.id.picture_img);
        TextView name_txt = (TextView) convertView.findViewById(R.id.name_txt);
        RelativeLayout item_layout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
        ImageView admin_img = (ImageView) convertView.findViewById(R.id.img_admin);

        picture_img.setImageResource(R.drawable.ic_profile_list_default);
        byte[] face_small_img = itemArrayList.get(position).getFace_small_img();
        if(face_small_img != null)
        {
//            Log.e("getView >>", "face_small_img >>>>>" + position);
            Bitmap faceBitmap = BitmapFactory.decodeByteArray( face_small_img, 0, face_small_img.length );
            picture_img.setImageBitmap(faceBitmap); // set face image
        }

        if(IrisApplication.display_lang.contains("zh") || IrisApplication.display_lang.equals("ja") ||
                IrisApplication.display_lang.contains("ko")){
            name_txt.setText(itemArrayList.get(position).lastName + " " + itemArrayList.get(position).firstName);
        }else
            name_txt.setText(itemArrayList.get(position).firstName + " " + itemArrayList.get(position).lastName);

        if(itemArrayList.get(position).role!=null && itemArrayList.get(position).role.equals("Administrator"))
            admin_img.setVisibility(View.VISIBLE);
        else
            admin_img.setVisibility(View.GONE);

        if(position == selected_position)
        {
            item_layout.setBackgroundColor(Color.parseColor("#000000"));
            name_txt.setTextColor(Color.parseColor("#5FB5AD"));
        }
        else
        {
            item_layout.setBackgroundColor(Color.parseColor("#222222"));
            name_txt.setTextColor(Color.parseColor("#ffffff"));
        }
        return convertView;

    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }


    public Item getListItem(int position)
    {
        Item item = itemArrayList.get(position);
        return item;
    }

    public void set_selected_position(int position)
    {
        selected_position = position;
    }
}
