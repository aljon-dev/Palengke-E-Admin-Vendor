package com.example.e_palengke_vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ItemHolder> {

    private Context context;
    private ArrayList<ProductModel> ProductList;


    @NonNull
    @Override
    public ProductListAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView productname,price,description,Quantity;
        ImageView productimg;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
