package com.example.e_palengke_vendor;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //User, Email get Intent from the Previous Activity
        String id = getIntent().getStringExtra("UserId");
        String useremail = getIntent().getStringExtra("UserEmail");
        Boolean IsGoogleSignIn = getIntent().getBooleanExtra("IsGoogleSignIn",true);


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
                }else if (Build.VERSION.SDK_INT <= 33){
                    requestStoragePermission();
                }else {
                    requestImageMediaPermission();
                }
            }
        });

        EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!IsGoogleSignIn){
                    ChooseAction();
                }else {
                    ChooseActionGoogleSignIn();
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

    private void requestImageMediaPermission(){
                ActivityCompat.requestPermissions(AccountActivity.this,new String[]{Manifest.permission.READ_MEDIA_IMAGES},STORAGE_PERMISSION);
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

            SelectedImageUri = data.getData();
            if(data != null && data.getData() != null ){

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),SelectedImageUri);
                    gcashphoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else{
                Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }


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



    private void upload(Bitmap bitmap,String id){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] imageData = baos.toByteArray();
        String filename = UUID.randomUUID().toString();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Uploads" + filename);
        storageReference.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
                        progressDialog.setTitle("Upload");
                        progressDialog.setMessage("Uploading");

                        progressDialog.show();
                        String url = uri.toString();

                        Map<String,Object> updateProfile = new HashMap<>();
                        updateProfile.put("profile",url);

                        firebaseDatabase.getReference("admin").child(id).updateChildren(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    onRestart();
                                }else{
                                    Toast.makeText(AccountActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                });
            }
        });
    }


    private void ChangePassword(){


        AlertDialog.Builder Changepass = new AlertDialog.Builder(AccountActivity.this);
        Changepass.setTitle("Change Password");

        View view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_change_password,null,false);

        Changepass.setView(view);

        TextInputEditText Changepassword,ChangePasswordConfirm;

        Changepassword = view.findViewById(R.id.changepassword);
        ChangePasswordConfirm = view.findViewById(R.id.changepasswordconfirm);
        Changepass.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = mAuth.getCurrentUser();
                String password = Changepassword.getText().toString();
                String passconfirm = ChangePasswordConfirm.getText().toString();

                if(password.isEmpty() && passconfirm.isEmpty()){
                    Toast.makeText(AccountActivity.this, "Input Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }else if(password.equals(passconfirm)){
                    Toast.makeText(AccountActivity.this, "Password Not Match", Toast.LENGTH_SHORT).show();
                }else{
                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AccountActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AccountActivity.this, "Failed To Change Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        Changepass.show();




    }

    private void ChangeUsername(String id) {


        AlertDialog.Builder ChangeUser = new AlertDialog.Builder(AccountActivity.this);
        ChangeUser.setTitle("Change Username");

        View view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_change_username, null, false);

        ChangeUser.setView(view);

        TextInputEditText Changeuser;

        Changeuser = view.findViewById(R.id.changeusername);



        ChangeUser.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String username = Changeuser.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(AccountActivity.this, "It Cannot be Empty", Toast.LENGTH_SHORT).show();
                    onRestart();
                }else{

                    Map<String, Object> UpdateUser = new HashMap<>();
                    UpdateUser.put("name", username);

                    firebaseDatabase.getReference("admin").child(id).updateChildren(UpdateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AccountActivity.this, "Username Updated", Toast.LENGTH_SHORT).show();
                                onRestart();
                            }else{
                                Toast.makeText(AccountActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ChangeUser.show();
    }

    private void ChangeAddress(String id ){



        AlertDialog.Builder ChangeAddress = new AlertDialog.Builder(AccountActivity.this);
        ChangeAddress.setTitle("Change Address");

        View view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_change_address, null, false);

        ChangeAddress.setView(view);

        TextInputEditText ChangeAddresses;

        ChangeAddresses = view.findViewById(R.id.changeAddress);

        ChangeAddress.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = ChangeAddresses.getText().toString();

                if(address.isEmpty()){
                    Toast.makeText(AccountActivity.this, "Fill the field", Toast.LENGTH_SHORT).show();

                }else{

                    Map<String,Object> UpdateAddress = new HashMap<>();

                    UpdateAddress.put("address",address);

                    firebaseDatabase.getReference("admin").child(id).updateChildren(UpdateAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AccountActivity.this, "Address Update", Toast.LENGTH_SHORT).show();
                                onRestart();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                            onRestart();
                        }
                    });
                }



            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ChangeAddress.show();

    }



    private void ChangeContact(String id) {


        AlertDialog.Builder ChangeContacts = new AlertDialog.Builder(AccountActivity.this);
        ChangeContacts.setTitle("Change Contact");

        View view = LayoutInflater.from(AccountActivity.this).inflate(R.layout.dialog_change_contact, null, false);

        ChangeContacts.setView(view);

        TextInputEditText ChangeContact;

        ChangeContact = view.findViewById(R.id.changecontact);

        ChangeContacts.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String Contacts = ChangeContact.getText().toString();

                if (Contacts.isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Fill the field", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> UpdateContact = new HashMap<>();
                    UpdateContact.put("contact", Contacts);

                    firebaseDatabase.getReference("admin").child(id).updateChildren(UpdateContact).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AccountActivity.this, "Success Change", Toast.LENGTH_SHORT).show();
                            onRestart();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountActivity.this, "Failed to change ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ChangeContacts.show();

    }

    private void ChooseAction(){

        String id = getIntent().getStringExtra("UserId");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        CharSequence charSequence [] = { "Update Address","Change Password","Update Contact","Update Username"};

        alertDialog.setTitle("Choose Action");


        alertDialog.setItems(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0 ){
                    ChangeAddress(id);
                }else if(which == 1){
                    ChangePassword();
                }else if(which == 2){
                    ChangeContact(id);
                }else if(which == 3){
                    ChangeUsername(id);
                }
            }
        });
        alertDialog.show();

    }

    private void ChooseActionGoogleSignIn(){

        String id = getIntent().getStringExtra("UserId");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        CharSequence charSequence [] = { "Update Address","Update Contact"};

        alertDialog.setTitle("Choose Action");


        alertDialog.setItems(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0 ){
                    ChangeAddress( id);
                }else if(which == 1) {
                    ChangeContact( id);
                }


            }
        });
        alertDialog.show();

    }




}




