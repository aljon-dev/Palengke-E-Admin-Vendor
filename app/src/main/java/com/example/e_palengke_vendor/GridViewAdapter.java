package com.example.e_palengke_vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridViewAdapter  extends BaseAdapter {


    private ArrayList<GridClass> itemlist;

    private Context context;

    LayoutInflater layoutInflater;


    public GridViewAdapter(ArrayList<GridClass> itemlist, Context context) {

        this.itemlist = itemlist;
        this.context = context;
    }


    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int position) {

        return itemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = layoutInflater.inflate(R.layout.griditems, null, false);

        ImageView gridImage = convertView.findViewById(R.id.ItemImage);
        TextView nameItem = convertView.findViewById(R.id.ItemName);
        TextView priceItem = convertView.findViewById(R.id.ItemPrice);

        Glide.with(context).load(itemlist.get(position).getProductImg()).circleCrop().into(gridImage);
        nameItem.setText(itemlist.get(position).getProductName());
        priceItem.setText(itemlist.get(position).getPrice());


        return convertView;
    }
}




