package com.example.e_palengke_vendor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AddProducts extends Fragment {


    String id;


    FirebaseDatabase firebaseDatabase;

    int REQUEST_CAMERA_PICK = 20;
    int REQUEST_GALLERY_PICK = 40;

    int CAMERA_PERMISSION = 100;
    int STORAGE_PERMISSION = 101;

    public AddProducts(String Uid) {
       this.id = Uid;
    }

    EditText productname,productqty,productcategory,prodprice,productdesc;

    MaterialButton uploadImage,Addproduct;

    ImageView  imgview;

    Bitmap bitmap;

    Uri SelectedImageUri;




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
                    SelectActionDialog();
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
                   Toast.makeText(getActivity(), "Fill the fields", Toast.LENGTH_SHORT).show();
               }else{
                   AddProduct(bitmap,prodname,prodDesc,Categories,productprice,productQty);
               }



            }
        });


        return view;
    }



   private void AddProduct(Bitmap bitmaps, String ProdName,String Desc,String Category,String Price, String Qty){

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

                            firebaseDatabase.getReference("admin").child(id).child("Products").child(timestamp).setValue(AddProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

                                  }else{
                                      Toast.makeText(getActivity(), "Failed To Upload ", Toast.LENGTH_SHORT).show();
                                  }
                                }
                            });

                    }
                });
            }
        });

    }

    private void UploadUsingCamera(){
        Intent UploadImages = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(UploadImages.resolveActivity(requireActivity().getPackageManager())!= null){
            startActivityForResult(UploadImages,REQUEST_CAMERA_PICK);

        }else{

            Toast.makeText(getActivity(), "NO CAMERA FOUND ", Toast.LENGTH_SHORT).show();
        }

    }
    private void UploadGallery(){
        Intent PickGallery = new Intent (Intent.ACTION_GET_CONTENT);
        PickGallery.setType("image/*");
        if(PickGallery.resolveActivity(requireActivity().getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(PickGallery,"Select Picture"),REQUEST_GALLERY_PICK);
        }
        else{
            Toast.makeText(getActivity(), "No Gallery App Available", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAMERA_PICK){
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            imgview.setImageBitmap(bitmap);

        }
        if(requestCode == REQUEST_GALLERY_PICK){
                if(data != null && data.getData() != null){
                    SelectedImageUri = data.getData();

                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),SelectedImageUri);
                        imgview.setImageBitmap(bitmap);
                    }catch(IOException e){
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    private void RequestCameraPermission(){
        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                UploadUsingCamera();
            }else{
                Toast.makeText(getActivity(), "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }

            }else if(requestCode == STORAGE_PERMISSION){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                UploadGallery();
            }else{
                Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean CheckCameraPermission(){
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

    }

    private Boolean CheckGalleryPermission(){
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    private void SelectActionDialog(){

        CharSequence ActionSelect [] =  {"Camera","Gallery"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Select Action");

    alertDialog.setItems(ActionSelect, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {


            if(which == 0) {

                if(CheckCameraPermission()){
                    UploadUsingCamera();
                }else{
                    RequestCameraPermission();
                }

            }else if (which == 1) {
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
    private void Successfull(){

        productname.setText("");
        productqty.setText("");
        prodprice.setText("");
        productcategory.setText("");
        productdesc.setText("");
    }


    }




