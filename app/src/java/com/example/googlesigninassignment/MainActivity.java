package com.example.googlesigninassignment;

import android.content.Intent;
import android.credentials.Credential;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


     GoogleSignInOptions gso;
   GoogleSignInClient gsc;

   Button google;

 int Google_SignIn = 20;

   FirebaseAuth mAuth;

   FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        google = findViewById(R.id.google);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_default_id))
                .requestEmail()
                .build();

       gsc  = GoogleSignIn.getClient(this,gso);

       google.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = gsc.getSignInIntent();
               startActivityForResult(intent,Google_SignIn);


           }
       });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Google_SignIn){
               Task <GoogleSignInAccount>  task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try{
                  GoogleSignInAccount account = task.getResult(ApiException.class);
                  SignIn(account.getIdToken());

                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }


        }
    }
    private void SignIn(String idToken){

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Users users = new Users();


                    users.setId(user.getUid());
                    users.setName(user.getDisplayName());
                    users.setEmail(user.getEmail());
                    users.setImage(String.valueOf(user.getPhotoUrl()));


                    database.getReference().child("users").child(user.getUid()).setValue(users);

                    Intent intent = new Intent(MainActivity.this, SignIn.class);

                    startActivity(intent);








                } else {
                    Toast.makeText(MainActivity.this, "AN ERROR", Toast.LENGTH_SHORT).show();





                }
            }
        });



    }
}