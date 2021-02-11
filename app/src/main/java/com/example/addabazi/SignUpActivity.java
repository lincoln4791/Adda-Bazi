package com.example.addabazi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.Util;
import com.example.addabazi.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.operation.AckUserWrite;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText etName,etEmail,etPassword,etConfirmPassword;
    private String name,email, password,confirmPassword;
    private Button btnRegister;
    private ImageView defaultProfilePicture;
    private View customProgressbar;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri localFileUri, cloudFileUri;

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName_profileActivity_ID);
        etEmail = findViewById(R.id.etEmail_profileActivity_ID);
        etPassword = findViewById(R.id.etPassword_ID);
        customProgressbar=findViewById(R.id.customProgressBarID);
        etConfirmPassword = findViewById(R.id.etConfirmPassword_ID);
        btnRegister = findViewById(R.id.btnRegister_signupActivity_ID);
        defaultProfilePicture = findViewById(R.id.default_profile_picture_id);
        storageReference = FirebaseStorage.getInstance().getReference();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpTask();
            }
        });



        defaultProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker();
            }
        });


    }



    // new profile will be created here
    private void signUpTask() {
        name = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        if(name.equals("")){
            etName.setError(getString(R.string.hint_enter_name));
        }

        else if(email.equals("")){
            etEmail.setError(getString(R.string.hint_enter_email));
        }
        else if(password.equals("")){
            etPassword.setError(getString(R.string.hint_enter_password));
        }
        else if(confirmPassword.equals("")){
            etConfirmPassword.setError(getString(R.string.hint_confirm_password));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getString(R.string.invalid_email_address));
        }
        else if(!password.equals(confirmPassword)){
            etConfirmPassword.setError(getString(R.string.password_mismatch));
        }
        else{
            if(Util.connectionAvailable(this)){
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                customProgressbar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        customProgressbar.setVisibility(View.GONE);

                        if(task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            if(localFileUri != null){
                                updateNameAmdPhoto();
                            }
                            else{
                                updateOnlyName();
                            }


                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            else{
                startActivity(new Intent(getApplicationContext(),MessagesActivity.class));
            }

        }
    }








    private void imagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE){
            if(resultCode == RESULT_OK){
                localFileUri = data.getData();
                defaultProfilePicture.setImageURI(localFileUri);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,101);
            }
            else{
                Toast.makeText(this, "Access Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void updateNameAmdPhoto(){

        if(Util.connectionAvailable(this)){
            String fileName = firebaseUser.getUid() + ".jpg";
            StorageReference fileRef = storageReference.child("images/"+fileName);
            fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                cloudFileUri = uri;

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(etName.getText().toString().trim())
                                        .setPhotoUri(cloudFileUri)
                                        .build();

                                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            String userID = firebaseUser.getUid();
                                            databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put(NodeNames.NAME,etName.getText().toString().trim());
                                            hashMap.put(NodeNames.EMAIL,etEmail.getText().toString().trim());
                                            hashMap.put(NodeNames.ONLINE,"true");
                                            hashMap.put(NodeNames.PHOTO,cloudFileUri.getPath());

                                            databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(SignUpActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        startActivity(intent);
                                                    }

                                                    else{
                                                        Toast.makeText(SignUpActivity.this, "Profile Update failed"+task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }


    }



    // new profile will be updated here with name , email, online status.
    public void updateOnlyName(){
        if(Util.connectionAvailable(this)){
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etName.getText().toString().trim())
                    .build();

            firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        String userId = firebaseUser.getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                        HashMap<String,String> hashMap = new HashMap<>();

                        hashMap.put(NodeNames.NAME,etName.getText().toString().trim());
                        hashMap.put(NodeNames.EMAIL,etEmail.getText().toString().trim());
                        hashMap.put(NodeNames.ONLINE,"true");
                        hashMap.put(NodeNames.PHOTO,"");

                        databaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this, "User Update Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(SignUpActivity.this, "UserUpdate Failed", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                    else{
                        Toast.makeText(SignUpActivity.this,"Failed to Update profile"+task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }




    public void loginBtnPressed(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}