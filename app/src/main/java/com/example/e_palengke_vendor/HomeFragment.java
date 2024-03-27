package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {


    String id;

    public HomeFragment(String id) {

        this.id = id;
    }



    FirebaseDatabase firebaseDatabase;
    TextView NumberProducts;

    int numberOfObjects ;


    ArrayList<GridClass> Itemlist;
    GridView gridView;

    GridViewAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      firebaseDatabase = FirebaseDatabase.getInstance();
      View view  = inflater.inflate(R.layout.fragment_home, container, false);


      gridView = view.findViewById(R.id.gridView);
      Itemlist = new ArrayList<>();
      adapter = new GridViewAdapter(Itemlist,getActivity());
      gridView.setAdapter(adapter);




      firebaseDatabase.getReference("admin").child(id).child("Products").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                  GridClass gridClass = dataSnapshot.getValue(GridClass.class);

                  Itemlist.add(gridClass);

              }
              adapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });






















      // Dashboard
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