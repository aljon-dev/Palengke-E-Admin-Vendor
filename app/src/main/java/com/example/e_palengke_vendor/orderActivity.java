package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class orderActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;



    RecyclerView RecyclerViewOrderReceipt;

    ReceiptAdapter adapter;

    ArrayList<ReceiptModel> ReceiptList ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Setting up firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();

        RecyclerViewOrderReceipt = findViewById(R.id.RecyclerViewOrderReceipt);
        ReceiptList = new ArrayList<>();

        RecyclerViewOrderReceipt.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceiptAdapter(this,ReceiptList);

        RecyclerViewOrderReceipt.setAdapter(adapter);


        firebaseDatabase.getReference("admin").child

    }
}