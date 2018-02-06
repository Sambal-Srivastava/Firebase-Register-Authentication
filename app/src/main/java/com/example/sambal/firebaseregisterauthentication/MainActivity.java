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

public class MainActivity extends AppCompatActivity {

    EditText etLoginEmail, etLoginPass;
    Button btLogin,btLoginRegister,btGoogle;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authListener;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        etLoginEmail =  findViewById(R.id.etLoginemail);
        etLoginPass =  findViewById(R.id.etLoginPassword);
        btLogin = findViewById(R.id.btLogin);
        btGoogle = findViewById(R.id.buttonGoogle);
        btLoginRegister = findViewById(R.id.btLoginRegister);
        btLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

         btLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SignIn();
             }
         });


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!= null)
                {
                    startActivity(new Intent(MainActivity.this,WelcomeLoginActivity.class)  );
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    private void SignIn() {
        final String email,password;
        email = etLoginEmail.getText().toString();
        password = etLoginPass.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(MainActivity.this,"Please enter email",Toast.LENGTH_LONG).show();
            etLoginEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etLoginEmail.setError("Please enter a valid email");
            etLoginEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
             Toast.makeText(MainActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
            etLoginPass.requestFocus();
            return;
        }

        pd.setMessage("Trying to Login...");
        pd.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                Toast.makeText(MainActivity.this,"Welcome " + email,Toast.LENGTH_LONG).show();
            }
                else
                {
                    pd.dismiss();
                    Toast.makeText(MainActivity.this,"Login error..",Toast.LENGTH_LONG).show();
                }

                }

        });
    }
}
