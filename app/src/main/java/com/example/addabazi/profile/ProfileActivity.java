package com.example.addabazi.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.addabazi.MainActivity;
import com.example.addabazi.R;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.UtilStorage;
import com.example.addabazi.password.ChangePasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView iv_defaultProfilePicture;
    private View customProgressbar;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference,dbrUsers;
    private StorageReference storageReference;
    private Uri localFileUri, cloudFileUri;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String myUID = FirebaseAuth.getInstance().getUid();


    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        iv_defaultProfilePicture = findViewById(R.id.default_profile_picture_id);
        customProgressbar=findViewById(R.id.customProgressBarID);
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbrUsers = databaseReference.child(NodeNames.USERS);


        tabLayout = findViewById(R.id.tl_profileActivity);
        viewPager = findViewById(R.id.vp_profileActivity);

        setViewPager();






        databaseReference.child(NodeNames.USERS).child(myUID).child(NodeNames.PROFILE_PICTURES)
                .child(NodeNames.PHOTO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if( !snapshot.getValue().toString().equals("")){
                        cloudFileUri = Uri.parse(snapshot.getValue().toString());
                        Glide.with(getApplicationContext()).load(Uri.parse(snapshot.getValue().toString())).into(iv_defaultProfilePicture);
                    }
                    else{
                        Glide.with(getApplicationContext()).load(R.drawable.default_profile_picture).into(iv_defaultProfilePicture);
                    }

                }
                else{
                    Glide.with(getApplicationContext()).load(R.drawable.default_profile_picture).into(iv_defaultProfilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }







    public void tvChangePasswordClicked(View view){
        Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }









    public void BtnSaveClick(View view){

                if(localFileUri != null){
                    updateNameAmdPhoto();
                }
                else{
                    updateProfileTextInfosOnly();
                }

        }





    public void profilePicPressed(View view){
            if(cloudFileUri == null){
                imagePicker();
            }
            else{
                PopupMenu popupMenu = new PopupMenu(this,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_layout,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if(id == R.id.menuItem_changeImageID){
                            imagePicker();
                        }
                        else if (id == R.id.menuItem_removeImageID){
                            removePhoto();
                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
    }




    public void removePhoto(){
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(null)
                .build();

        customProgressbar.setVisibility(View.VISIBLE);
        currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                customProgressbar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    String myUID = currentUser.getUid();
                    dbrUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.PHOTO,"");
                    customProgressbar.setVisibility(View.VISIBLE);
                    dbrUsers.child(myUID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            customProgressbar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Photo Removed Successfully", Toast.LENGTH_SHORT).show();

                            }

                            else{
                                Toast.makeText(ProfileActivity.this, "Profile Update failed"+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


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
                iv_defaultProfilePicture.setImageURI(localFileUri);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_CODE);
            }
            else{
                Toast.makeText(this, "Access Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }





    public void updateNameAmdPhoto(){

        String pushID = databaseReference.push().getKey();
        String fileName = pushID+".jpg";
        String folderName = pushID;
        StorageReference fileRef = storageReference.child(NodeNames.IMAGES_FOLDER).child(myUID).child(NodeNames.PROFILE_PICTURES+"/"+fileName);
        //StorageReference fileRef = storageReference.child("images/"+fileName);
        customProgressbar.setVisibility(View.VISIBLE);
        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                customProgressbar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Success storage", Toast.LENGTH_SHORT).show();
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(ProfileActivity.this, "Success downloadbale URI" , Toast.LENGTH_SHORT).show();
                            cloudFileUri = uri;
                            Map<String,Object> userMap = new HashMap();
                            userMap.put(NodeNames.PHOTO,cloudFileUri.toString());
                            userMap.put(NodeNames.FILE_NAME,fileName);

                           dbrUsers.child(myUID).child(NodeNames.PROFILE_PICTURES).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   HashMap dbrProfileFolder = new HashMap();
                                   dbrProfileFolder.put(NodeNames.FOLDER_NAME,folderName);
                                   dbrProfileFolder.put(NodeNames.FILE_NAME,fileName);
                                   dbrProfileFolder.put(NodeNames.URI,cloudFileUri.toString());
                                   dbrProfileFolder.put(NodeNames.TIME_STAMP,ServerValue.TIMESTAMP);

                                   databaseReference.child(NodeNames.IMAGES_FOLDER).child(myUID).child(NodeNames.PROFILE_PICTURES).child(folderName)
                                           .updateChildren(dbrProfileFolder).addOnCompleteListener(new OnCompleteListener() {
                                       @Override
                                       public void onComplete(@NonNull Task task) {
                                           if(task.isSuccessful()){
                                               localFileUri=null;
                                               cloudFileUri = null;
                                               updateProfileTextInfosOnly();
                                           }
                                           //Toast.makeText(ProfileActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                       }
                                   });
                               }
                           });


/*
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(cloudFileUri)
                                    .build();
                            //customProgressbar.setVisibility(View.VISIBLE);
                            currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    customProgressbar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        dbrUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        //hashMap.put(NodeNames.NAME,etName.getText().toString().trim());
                                        hashMap.put(NodeNames.PHOTO,cloudFileUri);

                                        customProgressbar.setVisibility(View.VISIBLE);
                                        dbrUsers.child(myUID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                customProgressbar.setVisibility(View.GONE);
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    //updateProfileTextInfosOnly();
                                                    //finish();
                                                }

                                                else{
                                                    Toast.makeText(ProfileActivity.this, "Profile Update failed"+task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
*/

                        }


                    });
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Database Error"+task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d("tag","gg"+task.getException());
                }
            }
        });

    }



    // new profile will be updated here with name , email, online status.
    public void updateProfileTextInfosOnly(){

        //UtilStorage.utilStorageUserName = etName.getText().toString();
        BasicProfileFragment.updateDataFromSaveDataButton(getApplicationContext());

        String userName = UtilStorage.utilStorageUserName;
        String userAge = UtilStorage.utilStorageUserAge;
        String userCountry = UtilStorage.utilStorageUserCountry;
        String userEmail = UtilStorage.utilStorageUserEmail;




        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
        currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dbrUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String,Object> hashMap = new HashMap<>();

                    hashMap.put(NodeNames.NAME,userName);
                    hashMap.put(NodeNames.AGE,userAge);
                    hashMap.put(NodeNames.COUNTRY,userCountry);
                    hashMap.put(NodeNames.EMAIL,userEmail);
                    customProgressbar.setVisibility(View.VISIBLE);
                    dbrUsers.child(myUID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            customProgressbar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "User Update Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(ProfileActivity.this, "UserUpdate Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                else{
                    Toast.makeText(ProfileActivity.this,"Failed to Update profile"+task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }








    public class AdapterProfileActivity extends FragmentPagerAdapter {

        public AdapterProfileActivity(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    BasicProfileFragment basicProfileFragment = new BasicProfileFragment();
                    return  basicProfileFragment;

                case 1:
                    EducationProfileFragment educationProfileFragment = new EducationProfileFragment();
                    return educationProfileFragment;
                case 2:
                    PhotosProfileFragment photosProfileFragment = new PhotosProfileFragment();
                    return photosProfileFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }
    }



    public void setViewPager(){
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_basic_userprofile));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_education_userprofile));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_photos_userprofile));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER|TabLayout.GRAVITY_FILL);

        AdapterProfileActivity adapter = new AdapterProfileActivity(getSupportFragmentManager()
                ,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

}