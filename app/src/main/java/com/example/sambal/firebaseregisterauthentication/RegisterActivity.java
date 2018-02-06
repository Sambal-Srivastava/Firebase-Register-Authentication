package com.example.sambal.firebaseregisterauthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    EditText etRegisterEmail, etRegisterPass;
    Button btRegister;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        etRegisterEmail = (EditText) findViewById(R.id.etRegisterEmail);
        etRegisterPass = (EditText) findViewById(R.id.etRegisterPass);
        btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegister();
            }


        });

    }
    private void UserRegister() {

        String email,pass;
        email = etRegisterEmail.getText().toString().trim();
        pass = etRegisterPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)&& TextUtils.isEmpty(pass))
        {
            Toast.makeText(RegisterActivity.this,"Email and password fields are empty", Toast.LENGTH_LONG).show();

            etRegisterEmail.requestFocus();

            etRegisterPass.requestFocus();
            return;
        }


        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etRegisterEmail.setError("Please enter a valid email");
            etRegisterPass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this,"Email field is empty", Toast.LENGTH_LONG).show();
            etRegisterEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(RegisterActivity.this,"Password field is empty", Toast.LENGTH_LONG).show();
            etRegisterPass.requestFocus();
            return;
        }

        if (pass.length()<6)
        {
            etRegisterPass.setError("Minimum Length of Passowrd should be 6");
            etRegisterPass.requestFocus();
            return;
        }

        progressDialog.setMessage("Registration in progress.....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    Toast.makeText(RegisterActivity.this,"Registration successful", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    if (task.getException()instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(RegisterActivity.this,"You are already registered", Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public void LoginPage(View view) {

        startActivity(new Intent(RegisterActivity.this, MainActivity.class));

    }
}
