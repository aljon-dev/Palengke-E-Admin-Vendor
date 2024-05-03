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

import java.util.ArrayList;

public class ToDeliverAdapter extends RecyclerView.Adapter<ToDeliverAdapter.ItemHolder> {

    private ArrayList<ProductModel> ToDeliveryListItem;
    private Context context;



    public ToDeliverAdapter(Context context,ArrayList<ProductModel> ToDeliveryListItem) {

        this.context = context;
        this.ToDeliveryListItem = ToDeliveryListItem;
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_delivery_list,parent,false);


        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            ProductModel productModel = ToDeliveryListItem.get(position);
            holder.onBind(productModel);
    }

    @Override
    public int getItemCount() {
        return  ToDeliveryListItem.size();
    }
    public static class ItemHolder extends  RecyclerView.ViewHolder {


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
