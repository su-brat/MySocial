package com.example.mysocial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText emailET,passwordET;
    Button registerBT, loginBT;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = (EditText) findViewById(R.id.uname);
        passwordET = (EditText) findViewById(R.id.pwd);
        registerBT = (Button) findViewById(R.id.register);
        loginBT = (Button) findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailET.setError("Invalid email");
                    emailET.setFocusable(true);
                }
                else if(passwordET.length()<6) {
                    passwordET.setError("Minimum length is 6 characters");
                    passwordET.setFocusable(true);
                }
                else {
                    registerUser(email,password);
                }
            }
        });

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user...");
    }

    private void registerUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressDialog.dismiss();

                    final FirebaseUser user = mAuth.getCurrentUser();

                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String email = user.getEmail();
                                        String uid = user.getUid();

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("Email",email);
                                        hashMap.put("Uid",uid);
                                        hashMap.put("Name","");
                                        hashMap.put("Phone","");
                                        hashMap.put("Image","");
                                        hashMap.put("About","");
                                        hashMap.put("SignInTimestamp","");
                                        hashMap.put("TypingTo","");

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        reference.child(uid).setValue(hashMap);

                                        Toast.makeText(RegisterActivity.this, "A verification email is sent to your email id.", Toast.LENGTH_LONG).show();

                                        mAuth.signOut();
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
