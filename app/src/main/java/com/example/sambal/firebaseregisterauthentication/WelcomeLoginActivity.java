package com.example.sambal.firebaseregisterauthentication;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class WelcomeLoginActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE =101 ;
    ImageView photochooser;
    EditText etDisplayName;
    Button btSaveProfile;
    Uri uriProfileImage;
    ProgressBar progressBar;
    String profileImageUrl;
    FirebaseAuth mAuth;
    TextView textViewVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        photochooser = (ImageView) findViewById(R.id.photochooser);
        etDisplayName = (EditText) findViewById(R.id.etDisplayName);
        btSaveProfile = (Button) findViewById(R.id.btSaveProfile);
        textViewVerified = (TextView) findViewById(R.id.textViewVerified);

        loadUserInfromation();

        photochooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageChooser();
            }
        });

        btSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInformation();
            }

            
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(WelcomeLoginActivity.this, MainActivity.class));
        }
    }

    private void loadUserInfromation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load( user.getPhotoUrl().toString())
                        .into(photochooser);
            }
            if (user.getDisplayName() != null) {
                etDisplayName.setText(user.getDisplayName());

            }

            if (user.isEmailVerified())
            {
                textViewVerified.setText("Email Verified");
            }
            else
            {
                textViewVerified.setText("Email Not verfied (Click here to verify");
                textViewVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(WelcomeLoginActivity.this,"Verification Email Sent",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

        }

    }

    private void saveUserInformation() {
        String displayName = etDisplayName.getText().toString();

        if (displayName.isEmpty())
        {
            etDisplayName.setError("Name Required");
            etDisplayName.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null && profileImageUrl!= null)
        {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(WelcomeLoginActivity.this,"Profile updated successfully",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data!= null && data.getData() != null)
        {
                uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                photochooser.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageReference =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis()+ ".jpg");

        if (uriProfileImage != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);


                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(WelcomeLoginActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private  void showImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
}
