package com.example.e_palengke_vendor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ToConfirm extends Fragment {


    String id,  BuyerId;

    public ToConfirm(String id,String BuyerId) {
        this.id = id;

        this.BuyerId = BuyerId;

    }


    FirebaseDatabase firebaseDatabase;
    RecyclerView productListView;

    ProductListAdapter adapter;

    ArrayList<ProductModel> ProductItems;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_confirm, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        productListView = view.findViewById(R.id.OrderListItem);
        ProductItems = new ArrayList<>();

        productListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ProductListAdapter(getContext(), ProductItems);

        productListView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void OnClick(ProductModel productModel) {

                AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity());
                Dialog.setTitle("Confirmation Order");
                CharSequence charSequence[] = {"Confirm", "Reject"};

                Dialog.setItems(charSequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("Order").child(productModel.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    Map<String, Object> ToDelivery = (Map<String, Object>) snapshot.getValue();

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

                                    Map<String, Object> Quantities = (Map<String, Object>) snapshot.getValue();
                                    Object productQty = Quantities.get("Quantity");


                                    String CurrentQty = productQty.toString();
                                    String BuyerQty = productModel.getQuantity();

                                    int ItemQuantities = Integer.parseInt(CurrentQty);
                                    int BuyerQuantities = Integer.parseInt(BuyerQty);


                                    int newQuantity = ItemQuantities - BuyerQuantities;


                                    String newProductQty = String.valueOf(newQuantity);
                                    Map<String, Object> UpdateQty = new HashMap<>();
                                    UpdateQty.put("Quantity", newProductQty);


                                    firebaseDatabase.getReference("admin").child(id).child("Products").child(productModel.getProductId()).updateChildren(UpdateQty);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else if (which == 1) {

                        }


                    }

                });
                Dialog.show();

            }
        });


        firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("Order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ProductModel productModel = ds.getValue(ProductModel.class);
                        ProductItems.add(productModel);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}


