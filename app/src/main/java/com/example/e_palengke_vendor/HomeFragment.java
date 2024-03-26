package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {


    String id;

    public HomeFragment(String id) {

        this.id = id;


    }



    FirebaseDatabase firebaseDatabase;
    TextView NumberProducts;

    int numberOfObjects ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      firebaseDatabase = FirebaseDatabase.getInstance();
      View view  = inflater.inflate(R.layout.fragment_home, container, false);

      NumberProducts = view.findViewById(R.id.NumberProducts);

      firebaseDatabase.getReference("admin").child(id).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              for(numberOfObjects = 0; numberOfObjects < snapshot.getChildrenCount(); numberOfObjects++){
                  numberOfObjects++ ;

              }
              NumberProducts.setText(String.valueOf(numberOfObjects ) +" "+ "- " +"Products");
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });








        return view ;
    }
}