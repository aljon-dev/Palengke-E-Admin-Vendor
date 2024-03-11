package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeAdmin extends AppCompatActivity {

    FirebaseAuth auth;

    FirebaseDatabase database;

    ImageView imageView;


    DrawerLayout  drawer;

    NavigationView nav;

    TextView nametxt;



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

        imageView = header.findViewById(R.id.imageBtn);
        nametxt = header.findViewById(R.id.nametext);


        //Retrieve the user from the login
        String Uid = getIntent().getStringExtra("id");
        NavigationUser(Uid);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open();
            }
        });





    }

    // Retrieve the User Void Function
    // Showing the name and photo thru google sign In or Google Sign In
    private void NavigationUser(String id){

        database.getReference().child("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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


}