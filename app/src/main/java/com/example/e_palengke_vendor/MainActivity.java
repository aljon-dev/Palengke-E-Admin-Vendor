package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.credentials.Credential;
import android.os.Bundle;
import android.service.credentials.CredentialEntry;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    GoogleSignInOptions gso;

    GoogleSignInClient gsc;

    FirebaseDatabase database;

    FirebaseAuth Auth;


    Button GoogleBtn;

    TextView  register;


    final int RC_SignIn = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView   Tap Register

        register = findViewById(R.id.tapregister);


        //Google Login
        GoogleBtn = findViewById(R.id.GoogleSignin);



        //Firebase database
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Google Sign In

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });






        GoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();

            }
        });






    }

    private void FirebaseAuth(String idToken){

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        Auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = Auth.getCurrentUser();
                    Users users = new Users();

                    users.setUserId(user.getUid());
                    users.setName(user.getDisplayName());
                    users.setProfile(String.valueOf(user.getPhotoUrl()));

                    database.getReference().child("vendor").child(users.getUserId()).setValue(users);

                    Intent intent = new Intent(MainActivity.this, HomeAdmin.class);
                    intent.putExtra("id",users.getUserId());
                    startActivity(intent);

                }
            }
        });
    }

    private void UserLogin(String user, String password){

      Auth.signInWithEmailAndPassword(user,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()) {
                  FirebaseUser user = Auth.getCurrentUser();
                  Users users = new Users();

                  users.setUserId(user.getUid());

                  Intent intent = new Intent(MainActivity.this, HomeAdmin.class);
                  intent.putExtra("id", users.getUserId());
                  startActivity(intent);
              }
              else{
                  Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
              }
          }
      });
    }

    // Void SignIn

    private void SignIn(){
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,RC_SignIn);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SignIn){
            Task <GoogleSignInAccount> task  =  GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                throw new RuntimeException(e);
            }


        }



    }
}