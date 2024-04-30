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

public class ReceiptList extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;

    RecyclerView ReceiptListOrder;

    ReceiptAdapter adapter;

    ArrayList<ReceiptModel> ReceiptItemList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        firebaseDatabase = FirebaseDatabase.getInstance();


        //Sustaining String value
        String id = getIntent().getStringExtra("UserId");
        String BuyerId = getIntent().getStringExtra("BuyerId");

        ReceiptListOrder = findViewById(R.id.ReceiptListOrder);
        ReceiptItemList = new ArrayList<>();

        ReceiptListOrder.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceiptAdapter(this,ReceiptItemList);

        ReceiptListOrder.setAdapter(adapter);

        firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    ReceiptModel receiptModel = ds.getValue(ReceiptModel.class);
                    ReceiptItemList.add(receiptModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








    }
}