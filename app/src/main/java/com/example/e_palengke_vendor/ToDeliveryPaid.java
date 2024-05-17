package com.example.e_palengke_vendor;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ToDeliveryPaid extends Fragment {



    String BuyerId;

    String id;
    public ToDeliveryPaid(String BuyerId ,String id) {
        this.BuyerId = BuyerId;

        this.id = id;

    }


    FirebaseDatabase firebaseDatabase;


    RecyclerView DeliveryList;

    ArrayList<ProductModel> ProductList;

    ToDeliverAdapter adapter;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_delivery_paid, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        DeliveryList = view.findViewById(R.id.DeliveryListItem);
        ProductList = new ArrayList<>();

        DeliveryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ToDeliverAdapter(getActivity(),ProductList);

        DeliveryList.setAdapter(adapter);



        adapter.setOnItemClickListener(new ToDeliverAdapter.OnItemClickListener() {
            @Override
            public void onClick(ProductModel productModel) {
                ToReceived(productModel);
            }
        });

        fetchData();

        return view;
    }

    private void ToReceived(ProductModel productModel){

        AlertDialog.Builder ToReceived = new AlertDialog.Builder(getActivity());
        ToReceived.setTitle("Confirm To Received");

        CharSequence Action [] = {"Confirmed"};


        ToReceived.setItems(Action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    firebaseDatabase.getReference("Users").child(BuyerId).child("ToDeliverPaid").child(productModel.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String,Object> ToReceiving = (Map<String, Object>) snapshot.getValue();

                            firebaseDatabase.getReference("Users").child(BuyerId).child("ToReceivedPaid").child(productModel.getOrderId()).setValue(ToReceiving);
                            firebaseDatabase.getReference("Users").child(BuyerId).child("ToDeliver").child(productModel.getOrderId()).removeValue();

                            refreshData ();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else if (which == 1 ){


                }
            }
        });

        ToReceived.show();

    }

    private void refreshData (){
        ProductList.clear();
        adapter.notifyDataSetChanged();
        fetchData();

    }

    private void fetchData(){

        firebaseDatabase.getReference("Users").child(BuyerId).child("ToDeliverPaid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ProductModel productModel = ds.getValue(ProductModel.class);
                    ProductList.add(productModel);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}