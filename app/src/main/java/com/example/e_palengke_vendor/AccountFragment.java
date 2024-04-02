package com.example.e_palengke_vendor;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


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

    MaterialButton  EditAccount;

    FirebaseDatabase firebaseDatabase;



    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        View view = inflater.inflate(R.layout.fragment_account,container,false);

        EditAccount = view.findViewById(R.id.accountInfo);


        name = view.findViewById(R.id.name);
        emailaddress = view.findViewById(R.id.email);
        Contacts = view.findViewById(R.id.Contact);
        UserAddress = view.findViewById(R.id.Address);

        Infos(id,email);

        EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            EditData();

            }
        });



        return view;
    }



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

                  Object ContactValue = contact.get("contacts");
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

            if(getUsername.isEmpty() ||  Contact.isEmpty() || addresses.isEmpty()){

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
    FirebaseUser  user = firebaseAuth.getCurrentUser();



    if(IsGoogleSignIn == true) {

        Map<String, Object> Update = new HashMap<>();

        Update.put("contact",contacts);
        Update.put("address",Address);

        firebaseDatabase.getReference("admin").child(id).updateChildren(Update);

    }else {

        Map<String, Object> Update = new HashMap<>();
        Update.put("contacts", contacts);
        Update.put("name",username);
        Update.put("address",Address);





        firebaseDatabase.getReference("admin").child(id).updateChildren(Update);
    }



    }


}