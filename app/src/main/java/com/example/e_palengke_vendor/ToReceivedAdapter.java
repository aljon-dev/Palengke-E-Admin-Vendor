package com.example.e_palengke_vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ToReceivedAdapter extends RecyclerView.Adapter<ToReceivedAdapter.ItemHolder> {


    private Context context;
    private ArrayList<ProductModel> ToReceivedList;
    public ToReceivedAdapter(Context context, ArrayList<ProductModel> ToReceivedList){
        this.context = context;
        this.ToReceivedList = ToReceivedList;
    }
    @NonNull
    @Override
    public ToReceivedAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.toreceivedlist,parent,false);
        return  new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToReceivedAdapter.ItemHolder holder, int position) {
        ProductModel productModel = ToReceivedList.get(position);

        holder.onBind(productModel);

    }

    @Override
    public int getItemCount() {
        return ToReceivedList.size();
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemName, ItemDescription, ItemPrice, ItemQuantity;
        ImageView productImage;

        String OrderId, ProductId;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            ItemDescription = itemView.findViewById(R.id.itemDescription);
            ItemPrice = itemView.findViewById(R.id.itemPrice);
            ItemQuantity = itemView.findViewById(R.id.itemQuantity);
            productImage = itemView.findViewById(R.id.itemImages);


        }

        public void onBind(ProductModel productModel){

            itemName.setText(productModel.getProductName());
            ItemDescription.setText(productModel.getDescription());
            ItemPrice.setText(productModel.getPrice());
            ItemQuantity.setText(productModel.getQuantity());

            OrderId = productModel.getOrderId();
            ProductId = productModel.getProductId();


            Glide.with(itemView.getContext()).load(productModel.getProductImg()).into(productImage);

        }

    }
}
