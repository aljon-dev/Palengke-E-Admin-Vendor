package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeAdmin extends AppCompatActivity  {

   private FirebaseAuth auth;

   private FirebaseDatabase database;

    private   ImageView imageView, Btn;

    private   DrawerLayout  drawer;

    private  NavigationView nav;

    private  TextView nametxt, NumberProducts;

    private   ArrayList<GridClass> Itemlist;
    private  GridView gridView;

    private   GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        //Firebase database

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Navigation
        drawer = findViewById(R.id.slidedraw);
        nav = findViewById(R.id.navigationMenu);

        //ImageButton to appear Drawer navigation
        View header = nav.getHeaderView(0);

        imageView = header.findViewById(R.id.photos);
        nametxt = header.findViewById(R.id.nametext);


        //Retrieve the user from the login

        String Uid = getIntent().getStringExtra("id");
        String email = getIntent().getStringExtra("email");
        Boolean IsGoogleSignIn = getIntent().getBooleanExtra("IsGoogleSignIn",true);
        NavigationUser(Uid);


        //Display Dashboard

        NumberProducts = findViewById(R.id.NumberProducts);

        gridView = findViewById(R.id.gridView);

        Itemlist = new ArrayList<>();
        adapter = new GridViewAdapter(Itemlist,this);
        gridView.setAdapter(adapter);


        // Adapter Set On Click Appear a Dialog to delete or Edit Prices etc.
       adapter.OnItemClickListener(new GridViewAdapter.OnItemClickListener() {
           @Override
           public void onClick(GridClass gridClass) {

               CharSequence SelectAction []  = {"Edit","Delete"};

               AlertDialog.Builder EditDialog = new AlertDialog.Builder(HomeAdmin.this);
               EditDialog.setTitle("Update Product");


               EditDialog.setItems(SelectAction, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){

                                EditProduct(Uid,gridClass.getProductId());

                            }else if(which == 1){
                                Delete(Uid,gridClass.getProductId());
                            }
                   }
               });


               EditDialog.show();
           }
       });


        // Display All Products and Dashboard
        database.getReference("admin").child(Uid).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshots : snapshot.getChildren()){
                    GridClass gridClass = snapshots.getValue(GridClass.class);
                    Itemlist.add(gridClass);
                }

                NumberProducts.setText(Itemlist.size() + " - " + "Products");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //nav bar Btn
        Btn = findViewById(R.id.imageBtn);

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open();
            }
        });

        //Navigation
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int items = item.getItemId();
                if(items == R.id.NavHome){
                    Toast.makeText(HomeAdmin.this, "You Already in Home ", Toast.LENGTH_SHORT).show();

                }else if(items == R.id.NavAccount){
                    Intent intent = new Intent (HomeAdmin.this, AccountActivity.class);
                    intent.putExtra("UserId",Uid);
                    intent.putExtra("UserEmail",email);
                    intent.putExtra("IsGoogleSignIn",IsGoogleSignIn);
                    startActivity(intent);

                }else if (items == R.id.NavOrder){
                    Intent intent = new Intent (HomeAdmin.this, orderActivity.class);
                    intent.putExtra("UserId",Uid);
                    intent.putExtra("UserEmail",email);
                    startActivity(intent);

                }

                else if(items == R.id.NavAddProduct){

                    Intent intent = new Intent (HomeAdmin.this, AddProduct.class);
                    intent.putExtra("UserId",Uid);
                    intent.putExtra("UserEmail",email);
                    startActivity(intent);


                }else if(items == R.id.NavHistory){



                }else if(items == R.id.NavDelivered){

                }

                else if(items == R.id.LogOut){

                    HomeAdmin.this.finish();

                }




                return false;
            }
        });




    }

    // Retrieve the User Void Function
    // Showing the name and photo thru google sign In or Google Sign In
    private void NavigationUser(String id){

        database.getReference("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    String name = user.getName();
                    String photoUrl = user.getProfile();

                    nametxt.setText(name);
                    Glide.with(HomeAdmin.this)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeAdmin.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void Delete(String UserId,String id){
            // Delete Products

        database.getReference("admin").child(UserId).child("Products").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(HomeAdmin.this, "Deleted", Toast.LENGTH_SHORT).show();
                    recreate();
                }else{
                    Toast.makeText(HomeAdmin.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void EditProduct(String Uid,String id){

        // Edit Price and Quantities

        AlertDialog.Builder EditDialog = new  AlertDialog.Builder(HomeAdmin.this);
        View view = LayoutInflater.from(HomeAdmin.this).inflate(R.layout.updateproduct,null,false);

        TextInputEditText  priceproduct,productQuantities;

        priceproduct = view.findViewById(R.id.UpdatePrices);
        productQuantities = view.findViewById(R.id.UpdateQuantities);

        EditDialog.setView(view);

        EditDialog.setTitle("Edit");

        //Display Current Data of the product thru SetText

     database.getReference("admin").child(Uid).child("Products").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> ProductInfo = (HashMap<String, Object>) snapshot.getValue();
                Object myprice = ProductInfo.get("Price");
                Object MyQty = ProductInfo.get("Quantity");

                priceproduct.setText(myprice.toString());
                productQuantities.setText(MyQty.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        EditDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedPrice = priceproduct.getText().toString().trim();
                String UpdateQty = productQuantities.getText().toString().trim();

                HashMap <String,Object> UpdateProduct = new HashMap<>();
                UpdateProduct.put("Price",updatedPrice);
                UpdateProduct.put("Quantity",UpdateQty);


                database.getReference("admin").child(Uid).child("Products").child(id).updateChildren(UpdateProduct);
                recreate();

            }
        });
        EditDialog.show();
    }







}