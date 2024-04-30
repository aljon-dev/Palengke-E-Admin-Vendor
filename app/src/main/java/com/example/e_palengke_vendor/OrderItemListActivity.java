package com.example.e_palengke_vendor;

import android.os.Bundle;
import android.widget.Toast;

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

public class OrderItemListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;

    RecyclerView productListView;

    ProductListAdapter adapter;

    ArrayList<ProductModel> ProductItems;




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

        productListView = findViewById(R.id.OrderListItem);
        ProductItems = new ArrayList<>();

        productListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductListAdapter(this,ProductItems);

        productListView.setAdapter(adapter);


        firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("Order").child(ReceiptId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    for(DataSnapshot ds : snapshot.getChildren()){
                        ProductModel productModel = ds.getValue(ProductModel.class);
                        ProductItems.add(productModel);
                    }
                }catch (Exception e){
                    Toast.makeText(OrderItemListActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}