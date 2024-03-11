package com.example.e_palengke_vendor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class HomeAdmin extends AppCompatActivity {

    FirebaseAuth auth;

    FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        //Firebase database

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();





    }
}