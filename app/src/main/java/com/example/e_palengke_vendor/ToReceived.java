package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

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

        View view = inflater.inflate(R.layout.fragment_to_received, container, false);



        return view ;
    }
}