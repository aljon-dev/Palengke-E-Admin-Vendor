package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;


public class ToDelivery extends Fragment {

    String BuyerId;
    public ToDelivery(String BuyerId ) {

        this.BuyerId = BuyerId;

    }

    FirebaseDatabase firebaseDatabase;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_to_delivery, container, false);




        return view ;
    }
}