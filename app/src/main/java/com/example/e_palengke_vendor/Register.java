package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText  userEmail ,userName, userPassword, ConfirmPassword,Number;

    CheckBox cbox;

    Button btn;


    FirebaseDatabase firebaseDatabase;

    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase Authentication  & Database

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();





        // Forms
        userEmail = findViewById(R.id.EmailInput);
        userName = findViewById(R.id.UserInput);
        userPassword = findViewById(R.id.PasswordInput);
        ConfirmPassword = findViewById(R.id.ConfirmPassword);
        Number = findViewById(R.id.TelePhone);
        cbox = findViewById(R.id.checkBox);
        btn = findViewById(R.id.Register);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                registration(userEmail.getText().toString().trim(),userPassword.getText().toString().trim());

            }
        });














    }

    private void registration(String UserEmail,String UserPassword){
        validationform();


        String userNames =  userName.getText().toString().trim();


        mAuth.createUserWithEmailAndPassword(UserEmail,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user  = mAuth.getCurrentUser();

                    Users users = new Users();
                    users.setName(userNames);
                    users.setUserId(user.getUid());
                    users.setProfile("");



                    ProgressDialog progressDialog = new ProgressDialog(Register.this);
                    firebaseDatabase.getReference().child("vendor").child(users.getUserId()).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                            progressDialog.setMessage("Registering");
                            progressDialog.setTitle("Registration");
                            progressDialog.show();



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });






                }
            }
        });




    }

public  void validationform(){

        String email = userEmail.getText().toString().trim();
        String userNames =  userName.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String CPassword = ConfirmPassword.getText().toString().trim();
        String Contacts = Number.getText().toString().trim();


        if(email.isEmpty() || userNames.isEmpty() || password.isEmpty() || CPassword.isEmpty() || Contacts.isEmpty()){
            Toast.makeText(this, "Fill the Fields", Toast.LENGTH_SHORT).show();
            if(!password.equals(CPassword)){
                Toast.makeText(this, "Password Not Match", Toast.LENGTH_SHORT).show();
            }else if(!cbox.isChecked()){
                Toast.makeText(this, "Put Check about the Privacy Policy ", Toast.LENGTH_SHORT).show();
            }
        }
    }





}