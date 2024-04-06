package com.example.e_palengke_vendor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddProduct extends AppCompatActivity {

    FirebaseAuth auth;

    FirebaseDatabase database;

    ImageView imageView, Btn;


    EditText productname,productqty,productcategory,prodprice,productdesc;

    MaterialButton uploadImage,Addproduct;

    ImageView  imgview;

    Bitmap bitmap;

    Uri SelectedImageUri;


    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_GALLERY_PERMISSION = 102;
    private static final int REQUEST_CAMERA_CAPTURE = 103;
    private static final int REQUEST_GALLERY_PICK = 104;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        String id = getIntent().getStringExtra("UserId");

        //Firebase storage and Firebase Storage
        database= FirebaseDatabase.getInstance();



        //EditText
        productname = findViewById(R.id.prodName);
        productdesc = findViewById(R.id.prodDesc);
        productcategory = findViewById(R.id.prodCategory);
        prodprice =findViewById(R.id.prodPrice);
        productqty = findViewById(R.id.prodQty);


        //ImageView
        imgview = findViewById(R.id.imgView);

        //Material Buttons
        uploadImage = findViewById(R.id.pickImg);
        Addproduct = findViewById(R.id.addProduct);



        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence ActionSelect [] =  {"Camera","Gallery"};
                android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddProduct.this);

                alertDialog.setTitle("Select Action");

                alertDialog.setItems(ActionSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            if(CheckCameraPermission()){
                                UploadUsingCamera();

                            }else {

                                RequestCameraPermission();
                            }
                        }else if( which == 1){
                            if(CheckGalleryPermission()){

                                UploadGallery();
                            }else{
                                requestStoragePermission();

                            }
                        }
                    }
                });

                alertDialog.show();

            }
        });

        Addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String prodname = productname.getText().toString();
                String prodDesc = productdesc.getText().toString();
                String Categories = productcategory.getText().toString();
                String productprice = prodprice.getText().toString();
                String productQty = productqty.getText().toString();

                if(prodname.isEmpty() || prodDesc.isEmpty() || Categories.isEmpty() || productprice.isEmpty() || productQty.isEmpty()){
                    Toast.makeText(AddProduct.this, "Fill the fields", Toast.LENGTH_SHORT).show();
                }else{
                    AddProduct(bitmap,prodname,prodDesc,Categories,productprice,productQty,id);
                }
            }
        });



    }



    private void AddProduct(Bitmap bitmaps, String ProdName, String Desc, String Category, String Price, String Qty,String id){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmaps.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] imageData = baos.toByteArray();

        String filename = UUID.randomUUID().toString();

        String timestamp = String.valueOf(System.currentTimeMillis());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Upload/" + filename);
        storageReference.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String Uri = uri.toString();

                        Map<String,Object> AddProduct = new HashMap<>();

                        AddProduct.put("ProductName", ProdName);
                        AddProduct.put("Description",Desc);
                        AddProduct.put("Category",Category);
                        AddProduct.put("Price",Price);
                        AddProduct.put("Quantity",Qty);
                        AddProduct.put("ProductImg",Uri);
                        AddProduct.put("ProductId",timestamp);

                        database.getReference("admin").child(id).child("Products").child(timestamp).setValue(AddProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddProduct.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(AddProduct.this, "Failed To Upload ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });

    }

    private void UploadUsingCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CAPTURE);
    }
    private void UploadGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY_PICK);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAMERA_CAPTURE){
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            imgview.setImageBitmap(bitmap);

        }else  if(requestCode == REQUEST_GALLERY_PICK){
            if(data != null && data.getData() != null){
                SelectedImageUri = data.getData();

                try{
                    bitmap = MediaStore.Images.Media.getBitmap(AddProduct.this.getContentResolver(),SelectedImageUri);
                    imgview.setImageBitmap(bitmap);
                }catch(IOException e){
                    Toast.makeText(AddProduct.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void RequestCameraPermission(){
        ActivityCompat.requestPermissions(AddProduct.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(AddProduct.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE },REQUEST_GALLERY_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               UploadUsingCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              UploadGallery();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }


        }



    private boolean CheckCameraPermission(){
        return ContextCompat.checkSelfPermission(AddProduct.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

    }

    private Boolean CheckGalleryPermission(){
        return ContextCompat.checkSelfPermission(AddProduct.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }


    private void SelectActionDialog(){


            }

            private void Successfull() {

                productname.setText("");
                productqty.setText("");
                prodprice.setText("");
                productcategory.setText("");
                productdesc.setText("");
            }


}
