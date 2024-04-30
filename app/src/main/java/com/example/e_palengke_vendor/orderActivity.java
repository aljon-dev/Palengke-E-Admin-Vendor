package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class orderActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;



    RecyclerView BuyerList;

    BuyerAdapter adapter;

    ArrayList<BuyerModel> BuyerItemList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        String id = getIntent().getStringExtra("UserId");

        //Setting up firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();

        BuyerList = findViewById(R.id.BuyerList);
        BuyerItemList = new ArrayList<>();

        BuyerList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BuyerAdapter(this,BuyerItemList);

        BuyerList.setAdapter(adapter);

        adapter.setOnItemClickListener(new BuyerAdapter.onItemClickListener() {
            @Override
            public void onItemClick(BuyerModel buyerModel) {

            }
        });



        //Display Buyer List
        firebaseDatabase.getReference("admin").child(id).child("Buyer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    BuyerModel buyerModel = ds.getValue(BuyerModel.class);
                    BuyerItemList.add(buyerModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}