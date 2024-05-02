package com.example.e_palengke_vendor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        adapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void OnClick(ProductModel productModel) {

                AlertDialog.Builder Dialog = new AlertDialog.Builder(OrderItemListActivity.this);
                Dialog.setTitle("Confirmation Order");
                CharSequence charSequence [] = {"Confirm","Reject"};

                Dialog.setItems(charSequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                     if(which == 0){
                firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("Order").child(ReceiptId).child(productModel.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Map<String, Object> ToDelivery = (Map <String, Object>) snapshot.getValue();

                                firebaseDatabase.getReference("Users").child(BuyerId).child("ToDeliver").child(productModel.getOrderId()).updateChildren(ToDelivery);

                                firebaseDatabase.getReference("Users").child(BuyerId).child("Order").child(productModel.getOrderId()).removeValue();



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }

                        });

                        firebaseDatabase.getReference("admin").child(id).child("Products").child(productModel.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Map<String,Object> Quantities = (Map<String, Object>) snapshot.getValue();
                                Object productQty = Quantities.get("Quantity");

                                String CurrentQty = productQty.toString();
                                String BuyerQty = productModel.getQuantity();

                                int ItemQuantities = Integer.parseInt(CurrentQty) ;
                                int BuyerQuantities = Integer.parseInt(BuyerQty);


                                int newQuantity = ItemQuantities - BuyerQuantities;


                                String newProductQty = String.valueOf(newQuantity);
                                Map<String,Object> UpdateQty = new HashMap<>();
                                UpdateQty.put("Quantity",newProductQty);



                                firebaseDatabase.getReference("admin").child(id).child("Products").child(productModel.getProductId()).updateChildren(UpdateQty);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




                     }else if (which == 1){

                     }


                    }

                });
                Dialog.show();

            }
        });




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