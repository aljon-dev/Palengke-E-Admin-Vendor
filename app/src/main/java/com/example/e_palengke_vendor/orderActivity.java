package com.example.e_palengke_vendor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
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


    private ImageView imageView, Btn;

    private DrawerLayout drawer;

    private NavigationView nav;

    TextView nametxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        String id = getIntent().getStringExtra("id");
        String email = getIntent().getStringExtra("email");
        Boolean IsGoogleSignIn = getIntent().getBooleanExtra("IsGoogleSignIn",true);

        //Setting up firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();

        BuyerList = findViewById(R.id.BuyerList);
        BuyerItemList = new ArrayList<>();

        BuyerList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BuyerAdapter(this,BuyerItemList);

        BuyerList.setAdapter(adapter);


        drawer = findViewById(R.id.slidedraw);
        nav = findViewById(R.id.navigationMenu);

        //ImageButton to appear Drawer navigation
        View header = nav.getHeaderView(0);

        imageView = header.findViewById(R.id.photos);
        nametxt = header.findViewById(R.id.nametext);

        NavigationUser(id);

        Btn = findViewById(R.id.imageBtn);

        Btn.setOnClickListener(v-> {


            drawer.open();
        });

        adapter.setOnItemClickListener(new BuyerAdapter.onItemClickListener() {
            @Override
            public void onItemClick(BuyerModel buyerModel) {
                Intent intent = new Intent(orderActivity.this,OrderItemListActivity.class);
                intent.putExtra("UserId",id);
                intent.putExtra("BuyerId",buyerModel.getBuyerId());
                startActivity(intent);
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

    private void NavigationUser(String id){

       firebaseDatabase.getReference("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    String name = user.getName();
                    String photoUrl = user.getProfile();

                    nametxt.setText(name);
                    Glide.with(orderActivity.this)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(orderActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}