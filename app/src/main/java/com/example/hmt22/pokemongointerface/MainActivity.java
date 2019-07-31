package com.example.hmt22.pokemongointerface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    public static String host = "cms-r1-27.cms.waikato.ac.nz";
    public static int port = 45525;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createAccount(View view){
        Intent intent  = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void signIn(View view){
        Intent intent  = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
