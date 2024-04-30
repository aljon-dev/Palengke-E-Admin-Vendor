package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;

public class OrderItemListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_list);

        //initialize firebase
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Sustaining Value from the previous class
        String id = getIntent().getStringExtra("UserId");
        String ReceiptId = getIntent().getStringExtra("ReceiptId");
        String BuyerId = getIntent().getStringExtra("BuyerId");









    }
}