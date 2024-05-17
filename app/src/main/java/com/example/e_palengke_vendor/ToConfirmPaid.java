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

public class ToConfirmPaid extends Fragment {

    String id,  BuyerId;




    // TODO: Rename and change types and number of parameters

    FirebaseDatabase firebaseDatabase;
    RecyclerView productListView;

    ProductListAdapter adapter;

    ArrayList<ProductModel> ProductItems;

    public ToConfirmPaid(String id, String buyerId) {

        this.id = id;

        this.BuyerId = BuyerId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseDatabase = FirebaseDatabase.getInstance();

        View view = inflater.inflate(R.layout.fragment_to_confirm_paid, container, false);

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
                                    try{

                                        Map<String, Object> ToDelivery = (Map<String, Object>) snapshot.getValue();

                                        firebaseDatabase.getReference("Users").child(BuyerId).child("ToDeliverPaid").child(productModel.getOrderId()).updateChildren(ToDelivery);

                                        firebaseDatabase.getReference("Users").child(BuyerId).child("OrderPaid").child(productModel.getOrderId()).removeValue();

                                        firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("OrderPaid").child(productModel.getOrderId()).removeValue();
                                        refreshFragment();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "PUTANG INA ", Toast.LENGTH_SHORT).show();

                                    }



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


                            firebaseDatabase.getReference("admin").child(id).child("Buyer").child(BuyerId).child("Order").child(productModel.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    Map<String, Object> ToDelivery = (Map<String, Object>) snapshot.getValue();

                                    firebaseDatabase.getReference("Users").child(BuyerId).child("Reject").child(productModel.getOrderId()).updateChildren(ToDelivery);
                                    firebaseDatabase.getReference("Users").child(BuyerId).child("OrderPaid").child(productModel.getOrderId()).removeValue();



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }



                            });

                        }


                    }

                });
                Dialog.show();

            }
        });


        fetchData();

        return view;
    }
    private void fetchData() {
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
                adapter.notifyDataSetChanged(); // Notify adapter of changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void refreshFragment() {
        ProductItems.clear(); // Clear the existing data
        adapter.notifyDataSetChanged(); // Notify adapter of changes (optional)
        fetchData(); // Fetch updated data
    }
}