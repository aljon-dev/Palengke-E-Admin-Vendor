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



    TextView name,emailaddress,Contacts;

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

        firebaseDatabase.getReference("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if(snapshot.exists()){

                    String Username = user.getName();
                    String Useremail = email;


                    name.setText(Username);
                    emailaddress.setText(Useremail);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error ", Toast.LENGTH_SHORT).show();

            }
        });

        firebaseDatabase.getReference("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               if(snapshot.exists()){

                   HashMap<String,Object> contact = (HashMap<String,Object>) snapshot.getValue();

                  Object ContactValue = contact.get("Contacts");
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

    TextInputEditText username,password,contacts;

    username = view.findViewById(R.id.setNewUsername);
    password = view.findViewById(R.id.setNewPassword);
    contacts = view.findViewById(R.id.setContact);

    if(IsGoogleSignIn == true){
        username.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
    }else{
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);

    }



    alertDialog.setView(view);


    alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String getUsername = username.getText().toString();
            String getPassword = password.getText().toString();
            String Contact = contacts.getText().toString();

            SubmitData(getUsername,getPassword,Contact);

        }
    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
        }
    });
    alertDialog.show();

}

private void SubmitData( String username,String password, String contacts){
    FirebaseUser  user = firebaseAuth.getCurrentUser();
    if(user != null){
        Boolean isGoogleSign = false;

        for(UserInfo info : user.getProviderData()){
            if(info.getProviderId().equals("google.com")){
                isGoogleSign = true;
            }
            if(isGoogleSign)
            {

                Map<String,Object> Update = new HashMap<>();

                Update.put("Contacts",contacts);

                firebaseDatabase.getReference("Users").child(id).updateChildren(Update);


            }else{

                Map<String,Object> Update = new HashMap<>();

                Update.put("Contacts",contacts);
                Update.put("name",username);

                firebaseDatabase.getReference("Users").child(id).updateChildren(Update);










            }
        }
    }
}

}