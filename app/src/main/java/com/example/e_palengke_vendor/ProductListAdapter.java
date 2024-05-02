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

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ItemHolder> {

    private Context context;
    private ArrayList<ProductModel> ProductList;


    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
    }
    public interface  OnItemClickListener{

        void OnClick(ProductModel productModel);
    }

    public ProductListAdapter(Context context ,ArrayList<ProductModel> ProductList){
        this.context = context;
       this.ProductList = ProductList;
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ItemHolder holder, int position) {
            ProductModel productModel = ProductList.get(position);
            holder.itemView.setOnClickListener(v-> onItemClickListener.OnClick(ProductList.get(position)));
            holder.onBind(productModel);
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView productname,price,description,Quantity;
        ImageView productimg;

        String ProductId,Category,OrderId;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);


            productname = itemView.findViewById(R.id.productname);
            price = itemView.findViewById(R.id.productprice);
            description = itemView.findViewById(R.id.productdescription);
            Quantity = itemView.findViewById(R.id.productQuantity);
            productimg = itemView.findViewById(R.id.ItemImg);
        }
        public void onBind (ProductModel productModel){

            productname.setText(productModel.getProductName());
            price.setText(productModel.getPrice());
            description.setText(productModel.getDescription());
            Quantity.setText(productModel.getQuantity());
            ProductId = productModel.getProductId();
            Category = productModel.getCategory();
            OrderId = productModel.getOrderId();
            Glide.with(itemView.getContext()).load(productModel.getProductImg()).into(productimg);
        }
    }
}
