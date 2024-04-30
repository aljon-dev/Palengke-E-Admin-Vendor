package com.example.e_palengke_vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BuyerAdapter extends RecyclerView.Adapter<BuyerAdapter.ItemHolder> {


    private Context context;
    private ArrayList<BuyerModel> BuyerList;


    onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface onItemClickListener{
       void onItemClick(BuyerModel buyerModel);
    }

    public BuyerAdapter(Context context, ArrayList<BuyerModel> BuyerList){

        this.context = context;
        this.BuyerList = BuyerList;
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyerlist,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            BuyerModel buyerModel = BuyerList.get(position);
            holder.onBind(buyerModel);
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(BuyerList.get(position)));
    }

    @Override
    public int getItemCount() {
        return BuyerList.size();
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {


        TextView BuyerId,Address,Contact,Name;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            BuyerId = itemView.findViewById(R.id.BuyerId);
            Address =itemView.findViewById(R.id.BuyerAddress);
            Contact = itemView.findViewById(R.id.BuyerContact);
            Name = itemView.findViewById(R.id.BuyerName);
        }
        public void onBind (BuyerModel buyerModel){
            BuyerId.setText(buyerModel.getBuyerId());
            Address.setText(buyerModel.getAddress());
            Contact.setText(buyerModel.getContact());
            Name.setText(buyerModel.getName());
        }
    }
}
