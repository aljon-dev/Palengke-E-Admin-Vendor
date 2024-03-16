package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
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

public class HomeAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;

    FirebaseDatabase database;

    ImageView imageView, Btn;


    DrawerLayout  drawer;

    NavigationView nav;

    TextView nametxt;


    FragmentManager fragmentManager;



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
        NavigationUser(Uid);

        //nav bar Btn
        Btn = findViewById(R.id.imageBtn);

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open();
            }
        });

        //Navigation
        nav.setNavigationItemSelectedListener(this);

        //Fragmentmanager

      fragmentManager = getSupportFragmentManager();
        replaceFragment(new HomeFragment());


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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int items = item.getItemId();
        if(items == R.id.NavHome){
            replaceFragment(new HomeFragment());
        }else if(items == R.id.NavAccount){
            replaceFragment(new AccountFragment());
        }else if(items == R.id.NavAddProduct){
            replaceFragment(new AddProduct_Fragment());
        }else if(items == R.id.NavHistory){
            replaceFragment(new HistoryFragment());
        }else if(items == R.id.NavDelivered){
            replaceFragment(new DeliveredFragment());
        }





        return false;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager Fragment = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = Fragment.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();



    }


}