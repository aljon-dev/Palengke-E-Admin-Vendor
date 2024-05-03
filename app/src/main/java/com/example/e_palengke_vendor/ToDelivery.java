package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;


public class ToDelivery extends Fragment {

    String BuyerId;
    public ToDelivery(String BuyerId ) {

        this.BuyerId = BuyerId;

    }

    FirebaseDatabase firebaseDatabase;


    RecyclerView DeliveryList;

    ArrayList<ProductModel> ProductList;

    ToDeliverAdapter adapter;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_to_delivery, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        DeliveryList = view.findViewById(R.id.DeliveryListItem);
        ProductList = new ArrayList<>();

        DeliveryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ToDeliverAdapter(getActivity(),ProductList);

        DeliveryList.setAdapter(adapter);

        firebaseDatabase.getReference("Users").child(BuyerId).child("ToDeliver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ProductModel productModel = ds.getValue(ProductModel.class);
                    ProductList.add(productModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view ;
    }

}