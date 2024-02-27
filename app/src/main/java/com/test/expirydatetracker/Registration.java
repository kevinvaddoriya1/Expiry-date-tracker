package com.test.expirydatetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends AppCompatActivity {

    TextView login;
    private long pressedTime;
    private FirebaseAuth mAuth;
    EditText igmail,iusr,ipass,iconf_pass;
    Button signup;
    UserData userData;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent sign = new Intent(Registration.this, Home.class);
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
        setContentView(R.layout.activity_registration);
        login = findViewById(R.id.login_page);
        signup = findViewById(R.id.signup);
        igmail = findViewById(R.id.gmail);
        iusr = findViewById(R.id.usr);
        ipass = findViewById(R.id.pass);
        iconf_pass = findViewById(R.id.conf_pass);


        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent log = new Intent(Registration.this, Login.class);
                startActivity(log);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent sign = new Intent(Registration.this, Home.class);
//                startActivity(sign);
                String gmail = String.valueOf(igmail.getText()).trim();
                String usr = String.valueOf(iusr.getText()).trim();
                String pass = String.valueOf(ipass.getText()).trim();
                String conf_pass = String.valueOf(iconf_pass.getText()).trim();

                if (TextUtils.isEmpty(gmail)){
                    Toast.makeText(Registration.this,"Please Enter Gmail..",Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(usr)){
                    Toast.makeText(Registration.this,"Please Enter Username..",Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(pass)){
                    Toast.makeText(Registration.this,"Please Enter Password..",Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(conf_pass)){
                    Toast.makeText(Registration.this,"Please Enter Conform password..",Toast.LENGTH_SHORT).show();
                    return;
                }if (!conf_pass.equals(pass)) {
                    Toast.makeText(Registration.this, "Password and conform password not same", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(gmail,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Registration.this, "Account created.",Toast.LENGTH_SHORT).show();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference();
                                    reference= reference.child("Users").child(mAuth.getUid());

                                    userData = new UserData();

                                    userData.setName(usr);
                                    userData.setGmail(gmail);
                                    userData.setPassword(pass);

                                    reference.setValue(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Registration.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                                    Intent sign = new Intent(Registration.this, Home.class);
                                                    finish();

                                                    startActivity(sign);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Registration.this, "Registration Failed.", Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                } else {
                                    Toast.makeText(Registration.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Registration.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }
}