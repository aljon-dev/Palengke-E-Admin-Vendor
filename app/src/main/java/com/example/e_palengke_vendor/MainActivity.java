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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    GoogleSignInOptions gso;

    GoogleSignInClient gsc;

    FirebaseDatabase database;

    FirebaseAuth auth;






    Button googleSignInButton, loginBtn;

    EditText UserEmail,passwordInput;


    final int RC_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView   Tap Register



        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //TextView
        UserEmail = findViewById(R.id.UserEmail);
        passwordInput = findViewById(R.id.PasswordInput);



        // Button
        googleSignInButton = findViewById(R.id.GoogleSignin);
        loginBtn = findViewById(R.id.loginbtn);

        // Google Sign
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);






        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = UserEmail.getText().toString();
                String password = passwordInput.getText().toString();

                Login(email,password);
            }
        });




    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Google Sign-In failed: " + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void Login(String email, String password){

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    Users users = new Users();

                    users.setUserId(user.getUid());
                    users.setName("");
                    users.setContact("");
                    users.setAddress("");
                    users.setProfile("");


                    database.getReference("admin").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Intent intent = new Intent(MainActivity.this,HomeAdmin.class);
                                intent.putExtra("id",users.getUserId());
                                intent.putExtra("email",user.getEmail());
                                intent.putExtra("IsGoogleSignIn",false);
                                startActivity(intent);

                            }else{
                                database.getReference("admin").child(user.getUid()).setValue(users);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
                else{
                    Toast.makeText(MainActivity.this, "Invalid User & Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setProfile(String.valueOf(user.getPhotoUrl()));
                            users.setAddress("");

                            database.getReference("admin").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        Toast.makeText(MainActivity.this, "Account Logged In", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, HomeAdmin.class);
                                        intent.putExtra("id", users.getUserId());
                                        intent.putExtra("email",user.getEmail());
                                        startActivity(intent);
                                    }else{

                                        database.getReference().child("admin").child(users.getUserId()).setValue(users);

                                        Intent intent = new Intent(MainActivity.this, HomeAdmin.class);
                                        intent.putExtra("id", users.getUserId());
                                        intent.putExtra("email",user.getEmail());
                                        intent.putExtra("IsGoogleSignIn",true);

                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Toast.makeText(MainActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}