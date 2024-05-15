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

import java.util.ArrayList;


public class ToReceived extends Fragment {

    String BuyerId;

    public ToReceived(String BuyerId) {
        this.BuyerId = BuyerId;
    }

    FirebaseDatabase firebaseDatabase;

    ToReceivedAdapter adapter;

    RecyclerView ReceiveItemList;

    ArrayList<ProductModel> ToReceivedItemList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getFragmentManager().beginTransaction().detach(ToReceived.this).attach(ToReceived.this).commit();

        View view = inflater.inflate(R.layout.fragment_to_received, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();


        ReceiveItemList = view.findViewById(R.id.ReceiveItemList);
        ToReceivedItemList = new ArrayList<>();

        ReceiveItemList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ToReceivedAdapter(getActivity(),ToReceivedItemList);

        ReceiveItemList.setAdapter(adapter);

        firebaseDatabase.getReference("Users").child(BuyerId).child("ToReceived").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ProductModel productModel = ds.getValue(ProductModel.class);
                    ToReceivedItemList.add(productModel);

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