package com.example.medlo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText sign_email, sign_pswd, sign_name, sign_number;
    private Button btn_sign;
    private TextView loginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        sign_email=findViewById(R.id.signUp_email);
        sign_pswd=findViewById(R.id.signUp_pswd);
        sign_name=findViewById(R.id.signUp_name);
        sign_number=findViewById(R.id.signUp_no);
        btn_sign=findViewById(R.id.btn_signup);
        loginRedirect=findViewById(R.id.loginRedirect);


        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = sign_email.getText().toString().trim();
                String pswd = sign_pswd.getText().toString().trim();
                String signname = sign_name.getText().toString().trim();
                String signnumber = sign_number.getText().toString().trim();

                if(user.isEmpty()){
                    sign_email.setError("Email can't be Empty");
                }
                if(pswd.isEmpty()){
                    sign_pswd.setError("Password can't be Empty");
                } else {
                    auth.createUserWithEmailAndPassword(user, pswd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        // ✅ Insert into SQLite after successful Firebase sign-up
                                        DatabaseHelper dbHelper = new DatabaseHelper(Signup.this);
                                        boolean inserted = dbHelper.insertUser(signname, user, signnumber);
                                        if (inserted) {
                                            Toast.makeText(Signup.this, "User registered", Toast.LENGTH_SHORT).show();

                                            // Store email in SharedPreferences
                                            SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("user_email", user);
                                            editor.apply();
                                        } else {
                                            Toast.makeText(Signup.this, "User already exists in DB", Toast.LENGTH_SHORT).show();
                                        }

                                        Toast.makeText(Signup.this,"Sign-Up Successfully",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Signup.this, login.class));
                                    } else {
                                        Toast.makeText(Signup.this,"Sign-Up Failed: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });


        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,login.class));
            }
        });
    }
}