package com.example.e_palengke_vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ItemHolder> {


    private Context context;
    private ArrayList<ReceiptModel> ReceiptList;

    public ReceiptAdapter(Context context, ArrayList<ReceiptModel> ReceiptList){
        this.context = context;
        this.ReceiptList = ReceiptList;
    }

    OnClickItemListener onClickItemListener;

    public void setOnClickListener(OnClickItemListener onClickItemListener){
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener{

        void onClick(ReceiptModel receiptModel);
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiptlistitem,parent,false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        ReceiptModel receiptModel = ReceiptList.get(position);
        holder.OnBind(receiptModel);
        holder.itemView.setOnClickListener(v -> onClickItemListener.onClick(ReceiptList.get(position)));

    }

    @Override
    public int getItemCount() {
        return ReceiptList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView ReceiptId;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            ReceiptId = itemView.findViewById(R.id.ReceiptId);

        }
        public void OnBind(ReceiptModel receiptModel){

            ReceiptId.setText(receiptModel.getReceiptID());
        }

    }
}
