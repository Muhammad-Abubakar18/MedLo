package com.example.medlo;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText login_email, login_pswd;
    private Button btn_login;
    private TextView signRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        login_email=findViewById(R.id.login_email);
        login_pswd=findViewById(R.id.login_pswd);
        btn_login=findViewById(R.id.btn_login);
        signRedirect=findViewById(R.id.signupRedirect);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString();
                String pswd= login_pswd.getText().toString();
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if (!pswd.isEmpty()){
                        auth.signInWithEmailAndPassword(email,pswd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(login.this,"Successfully Login",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(login.this,MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this,"Login Failed",Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        login_pswd.setError("Password can't be Empty");
                    }
                } else if (email.isEmpty()) {
                    login_email.setError("Email can't be Empty");

                }else {
                    login_email.setError("Please Enter Valid Email");
                }
            }
        });
        signRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,Signup.class));
            }
        });

    }
}