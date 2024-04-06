package com.example.e_palengke_vendor;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AccountActivity extends AppCompatActivity {

    TextView name,emailaddress,Contacts, UserAddress,username;

    MaterialButton EditAccount,gcashButton;

    FirebaseDatabase firebaseDatabase;



    FirebaseAuth firebaseAuth;


    int UploadGcash = 20;

    int STORAGE_PERMISSION = 40;

    Uri SelectedImageUri;



    ImageView gcashphoto,imageView;

    DrawerLayout drawer;

    NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //User, Email get Intent from the Previous Activity
        String id = getIntent().getStringExtra("UserId");
        String useremail = getIntent().getStringExtra("UserEmail");



        // Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //Navigation Set up
        drawer = findViewById(R.id.slidedraw);
        nav = findViewById(R.id.navigationMenu);

        //ImageButton to appear Drawer navigation
        View header = nav.getHeaderView(0);

        imageView = header.findViewById(R.id.photos);
        username = header.findViewById(R.id.nametext);





        //Buttons
        EditAccount = findViewById(R.id.accountInfo);
        gcashButton = findViewById(R.id.gcashupload);

        //Gcash Photo
        gcashphoto = findViewById(R.id.gcashPhoto);

        name = findViewById(R.id.name);
        emailaddress = findViewById(R.id.email);
        Contacts = findViewById(R.id.Contact);
        UserAddress = findViewById(R.id.Address);


        //Display UserProfile Thru Navigation
        DisplayUserProfileNavigation(id);

        //Display UserData
        DisplaySellerInfo(id,useremail);


        gcashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    openGalleryIntent();
                }else{
                    requestStoragePermission();
                }
            }
        });







    }


    private void DisplayUserProfileNavigation(String id){

        firebaseDatabase.getReference("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);

                    String Username = user.getName();
                    String photourl = user.getProfile();

                    username.setText(Username);
                    Glide.with(AccountActivity.this)
                            .load(photourl)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Loading Data Error", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void DisplaySellerInfo (String id,String email){

        firebaseDatabase.getReference("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                String username = user.getName();
                String userEmail = email;
                String userContact = user.getContact();
                String userAddress = user.getAddress();

                name.setText(username);
                emailaddress.setText(userEmail);
                Contacts.setText(userContact);
                UserAddress.setText(userAddress);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Loading Data Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private boolean isStoragePermissionGranted(){
        return ContextCompat.checkSelfPermission(AccountActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(AccountActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION);
    }

    private void openGalleryIntent(){
        Intent gallery  = new Intent(Intent.ACTION_PICK);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery,UploadGcash);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UploadGcash){

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryIntent();
            } else {
                Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
            }

        }
        }
    }




