package com.example.e_palengke_vendor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.CharacterData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AccountFragment extends Fragment {



    String id;

    String email;

    Boolean IsGoogleSignIn;




    public AccountFragment(String Uid, String email,Boolean isGoogleSignIn) {

        this.id = Uid;
        this.email = email;
        this.IsGoogleSignIn = isGoogleSignIn;

    }



    TextView name,emailaddress,Contacts, UserAddress;

    MaterialButton  EditAccount,gcashButton;

    FirebaseDatabase firebaseDatabase;



    FirebaseAuth firebaseAuth;


    int UploadGcash = 20;

    int STORAGE_PERMISSION = 40;

    Uri SelectedImageUri;

    Bitmap bitmap;

    ImageView gcashphoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        View view = inflater.inflate(R.layout.fragment_account,container,false);

        //Buttons
        EditAccount = view.findViewById(R.id.accountInfo);
        gcashButton = view.findViewById(R.id.gcashupload);

        //Gcash Photo
        gcashphoto = view.findViewById(R.id.gcashPhoto);



        // Names
        name = view.findViewById(R.id.name);
        emailaddress = view.findViewById(R.id.email);
        Contacts = view.findViewById(R.id.Contact);
        UserAddress = view.findViewById(R.id.Address);

        Infos(id,email);



        //OnClick Listener to Edit A data
        EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            EditData();

            }
        });

        //OnClick Listener to Upload Gcash

        gcashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(CheckGalleryPermission()){
                        UploadQR();
                    }else{
                        RequestStoragePermission();
                    }
            }
        });





        return view;


    }
        // Void Functions


    private void Infos(String id, String email){

        firebaseDatabase.getReference("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if(snapshot.exists()){

                    String Username = user.getName();
                    String Useremail = email;
                    String address = user.getAddress();

                    name.setText(Username);
                    emailaddress.setText(Useremail);
                    UserAddress.setText(address);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error ", Toast.LENGTH_SHORT).show();

            }
        });

        firebaseDatabase.getReference("admin").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               if(snapshot.exists()){

                   HashMap<String,Object> contact = (HashMap<String,Object>) snapshot.getValue();

                  Object ContactValue = contact.get("contact");
                  String ContactData = String.valueOf(ContactValue);

                  Contacts.setText(ContactData);


               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();

            }
        });

    }

private void  EditData(){
    AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getActivity());

    alertDialog.setTitle("Edit Contact & User");

    View view = getLayoutInflater().inflate(R.layout.editinfo,null,false);
    FirebaseUser  user = firebaseAuth.getCurrentUser();
    TextInputEditText username,contacts,address;

    username = view.findViewById(R.id.setNewUsername);

    contacts = view.findViewById(R.id.setContact);
    address = view.findViewById(R.id.setAddress);

    if(IsGoogleSignIn == true){
        username.setVisibility(View.GONE);
        username.setEnabled(false);

    }else{
        username.setVisibility(View.VISIBLE);
    }



    alertDialog.setView(view);

    alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            String getUsername = username.getText().toString();

            String Contact = contacts.getText().toString();
            String addresses = address.getText().toString();

            if(Contact.isEmpty() || addresses.isEmpty()){

                Toast.makeText(getActivity(), "Fill In", Toast.LENGTH_SHORT).show();

            }else{
                SubmitData(getUsername,Contact,addresses);
                Toast.makeText(getActivity(), "Success ", Toast.LENGTH_SHORT).show();
            }


        }
    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
        }
    });
    alertDialog.show();

}

private void SubmitData( String username, String contacts, String Address){




    if(IsGoogleSignIn == true) {

        Map<String, Object> Update = new HashMap<>();

        Update.put("contact",contacts);
        Update.put("address",Address);

        firebaseDatabase.getReference("admin").child(id).updateChildren(Update);

    }else {

        Map<String, Object> Update = new HashMap<>();
        Update.put("contact", contacts);
        Update.put("name",username);
        Update.put("address",Address);

        firebaseDatabase.getReference("admin").child(id).updateChildren(Update);
    }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UploadGcash){

            if(data != null  && data.getData() != null) {

                SelectedImageUri = data.getData();



                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), SelectedImageUri);
                    gcashphoto.setImageBitmap(bitmap);
                    uploadQrCode(bitmap);

                } catch (IOException e) {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    private void uploadQrCode(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] qrdata = baos.toByteArray();

        String filename = UUID.randomUUID().toString();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("QrCodes"+ filename);
        storageReference.putBytes(qrdata).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();

                        Map<String,Object> AddGcash = new HashMap<>();

                        AddGcash.put("GcashQr",url);

                        firebaseDatabase.getReference("admin").child(id).updateChildren(AddGcash).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
            }
        });



    }


    private void RequestStoragePermission(){
        ActivityCompat.requestPermissions(requireActivity(),new String []{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION);
    }
    private Boolean CheckGalleryPermission(){
        return ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void UploadQR(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent,"Find Gcash QR"),UploadGcash);
        }else{
            Toast.makeText(getActivity(), "Gallery not found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == UploadGcash){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                UploadQR();
            }
        }
    }
}