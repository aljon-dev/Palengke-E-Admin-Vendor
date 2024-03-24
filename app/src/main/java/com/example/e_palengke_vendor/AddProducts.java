package com.example.e_palengke_vendor;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AddProducts extends Fragment {


    String id;


    FirebaseDatabase firebaseDatabase;

    int REQUEST_CAMERA_PICK = 20;

    public AddProducts(String Uid) {
       this.id = Uid;
    }

    EditText productname,productqty,productcategory,prodprice,productdesc;

    MaterialButton uploadImage,Addproduct;

    ImageView  imgview;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Setting a view
        View view = inflater.inflate(R.layout.fragment_add_products,container,false);


        //Firebase storage and Firebase Storage
        firebaseDatabase = FirebaseDatabase.getInstance();



        //EditText
        productname = view.findViewById(R.id.prodName);
        productdesc = view.findViewById(R.id.prodDesc);
        productcategory = view.findViewById(R.id.prodCategory);
        prodprice = view.findViewById(R.id.prodPrice);
        productqty = view.findViewById(R.id.prodQty);


        //ImageView
        imgview = view.findViewById(R.id.imgView);

        //Material Buttons
        uploadImage = view.findViewById(R.id.pickImg);
        Addproduct = view.findViewById(R.id.addProduct);



        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              UploadUsingCamera();
            }
        });

        Addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*  String prodname = productname.getText().toString();
               String prodDesc = productdesc.getText().toString();
               String Categories = productcategory.getText().toString();
               String productprice = prodprice.getText().toString();
               String productQty = productqty.getText().toString();
                AddProduct(prodname,prodDesc,Categories,productprice,productQty);

              */

            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAMERA_PICK ){

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            imgview.setImageBitmap(bitmap);
            UploadImage(bitmap);


        }
    }

   /*private void AddProduct(String ProdName,String Desc,String Category,String Price, String Qty){

        String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String,Object> AddProduct = new HashMap<>();

        AddProduct.put("ProductName", ProdName);
        AddProduct.put("Description",Desc);
        AddProduct.put("Category",Category);
        AddProduct.put("Price",Price);
        AddProduct.put("Quantity",Qty);


        firebaseDatabase.getReference("Users").child(id).child("Products").child(timestamp).setValue(AddProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getActivity(), "Successfull", Toast.LENGTH_SHORT).show();
                }
            }
        });



    } */


    private void UploadUsingCamera(){
        Intent UploadImages = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(UploadImages.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(UploadImages,REQUEST_CAMERA_PICK);

        }

    }

    private void UploadImage(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] ImageToData = baos.toByteArray();

        String filename = UUID.randomUUID().toString();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Uploads/" + filename);
        storageReference.putBytes(ImageToData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String Uri = uri.toString();

                        Map<String, Object> addphoto = new HashMap<>();
                        addphoto.put("ProductImg",Uri);



                        firebaseDatabase.getReference("Users").child(id).child("Products").setValue(addphoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Uploaded Pic", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });





    }


}