package com.test.expirydatetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextView signup;
    private FirebaseAuth mAuth;
    Button login;
    EditText igmail,ipass;

    private long pressedTime;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent sign = new Intent(Login.this, Home.class);
            startActivity(sign);
            finish();
        }
    }
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.reg_page);
        igmail = findViewById(R.id.gmail);
        ipass = findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent log = new Intent(Login.this, Home.class);
//                startActivity(log);
                String gmail = String.valueOf(igmail.getText()).trim();
                String pass = String.valueOf(ipass.getText()).trim();
                if (TextUtils.isEmpty(gmail)){
                    Toast.makeText(Login.this,"Please Enter Gmail..",Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(pass)){
                    Toast.makeText(Login.this,"Please Enter pass..",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(gmail,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login Successfully.",Toast.LENGTH_SHORT).show();
                                    Intent sign = new Intent(Login.this, Home.class);
                                    startActivity(sign);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Login.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(Login.this, Registration.class);
                startActivity(sign);
            }
        });
    }
}