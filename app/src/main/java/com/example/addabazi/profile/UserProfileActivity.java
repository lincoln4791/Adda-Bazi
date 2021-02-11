package com.example.addabazi.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.UtilStorage;
import com.example.addabazi.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.ACTION_VIEW;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private String userID;
    private TabLayout tabLayout;
    private ImageView iv_profilePicture;
    private TextView tv_userName,tv_userEmail;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String myUID= FirebaseAuth.getInstance().getUid();
    private ViewPager viewPager;
    private Button btn_editProfile,btn_logout,btn_sendFriendRequest,btn_cancelFriendRequest,btn_rejectFriendRequest,btn_acceptFriendRequest
                    ,btn_unFriend,btn_sendMessage;

    private LinearLayout ll_logOutAndEditBtn,ll_acceptFriedRequest,ll_cancelFriendRequest,ll_sendFriendRequest,ll_unFriend;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        userID = getIntent().getStringExtra(Extras.USER_KEY);



        iv_profilePicture = findViewById(R.id.iv_ProfilePicture_UserProfileActivity);
        tv_userName = findViewById(R.id.tv_userName_userProfileInfo);
        tabLayout = findViewById(R.id.tl_userProfilrActivity);
        viewPager = findViewById(R.id.vp_userProfileActivity);

        btn_editProfile = findViewById(R.id.btn_editProfile_userProfileActivity);
        btn_sendFriendRequest = findViewById(R.id.btn_sendFriendReq_userProfileActivity);
        btn_cancelFriendRequest = findViewById(R.id.btn_cancelFriendRequest_userProfileActivity);
        btn_acceptFriendRequest = findViewById(R.id.btn_acceptFriend_userProfileActivity);
        btn_rejectFriendRequest = findViewById(R.id.btn_rejectFriend_userProfileActivity);
        btn_unFriend = findViewById(R.id.btn_unfriend_userProfileActivity);
        btn_sendMessage = findViewById(R.id.btn_sendMessage_userProfileActivity);

        ll_logOutAndEditBtn = findViewById(R.id.ll_logOutAndEditBtn);
        ll_sendFriendRequest = findViewById(R.id.ll_sendFriendReq_userProfileActivity);
        ll_cancelFriendRequest = findViewById(R.id.ll_cancelFriendReq_userProfileActivity);
        ll_acceptFriedRequest = findViewById(R.id.ll_acceptFriendReq_userProfileActivity);
        ll_unFriend = findViewById(R.id.ll_unfriend_userProfileActivity);


        btn_acceptFriendRequest.setOnClickListener(this);
        btn_cancelFriendRequest.setOnClickListener(this);
        btn_sendFriendRequest.setOnClickListener(this);
        btn_rejectFriendRequest.setOnClickListener(this);
        btn_unFriend.setOnClickListener(this);





        // A public static variable to pass data from activity to fragment
        UtilStorage.utilUserID = userID;

        loadUserProfile(userID);
        setViewPager();

    }

    public void editProfileBtnPressed(View view) {
            Intent intent = new Intent(this,ProfileActivity.class);
            startActivity(intent);

    }

    private void loadUserProfile(String userID) {

        databaseReference.child(NodeNames.USERS).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                        String userPhotoRef = "";
                        String userName = snapshot.child(NodeNames.NAME).getValue() != ""?
                                snapshot.child(NodeNames.NAME).getValue().toString():"";
                        tv_userName.setText(userName);


                        if(snapshot.child(NodeNames.PROFILE_PICTURES).child(NodeNames.PHOTO).exists()){
                            if(!snapshot.child(NodeNames.PROFILE_PICTURES).child(NodeNames.PHOTO).getValue().toString().equals("")){
                                userPhotoRef = snapshot.child(NodeNames.PROFILE_PICTURES).child(NodeNames.PHOTO).getValue().toString();
                                Glide.with(getApplicationContext()).load(Uri.parse(userPhotoRef)).into(iv_profilePicture);
                            }
                            else{
                                Glide.with(getApplicationContext()).load(R.drawable.default_profile_picture).into(iv_profilePicture);
                            }

                        }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, getString(R.string.dataBaseError,error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });




        if(userID.equals(myUID)){
            //btn_editProfile.setVisibility(View.VISIBLE);
            ll_unFriend.setVisibility(View.GONE);
            ll_acceptFriedRequest.setVisibility(View.GONE);
            ll_cancelFriendRequest.setVisibility(View.GONE);
            ll_logOutAndEditBtn.setVisibility(View.VISIBLE);
            ll_sendFriendRequest.setVisibility(View.GONE);

        }
        else{
            databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).child(myUID).child(userID)
                    .child(NodeNames.REQUEST_TYPE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(!snapshot.getValue().equals("")){
                            if(snapshot.getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_ACCEPTED)){
                                ll_unFriend.setVisibility(View.VISIBLE);
                                ll_acceptFriedRequest.setVisibility(View.GONE);
                                ll_cancelFriendRequest.setVisibility(View.GONE);
                                ll_logOutAndEditBtn.setVisibility(View.GONE);
                                ll_sendFriendRequest.setVisibility(View.GONE);
                            }


                            else if(snapshot.getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_RECEIVED)){
                                ll_unFriend.setVisibility(View.GONE);
                                ll_acceptFriedRequest.setVisibility(View.VISIBLE);
                                ll_cancelFriendRequest.setVisibility(View.GONE);
                                ll_logOutAndEditBtn.setVisibility(View.GONE);
                                ll_sendFriendRequest.setVisibility(View.GONE);
                            }


                            else if(snapshot.getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_SENT)){
                                ll_unFriend.setVisibility(View.GONE);
                                ll_acceptFriedRequest.setVisibility(View.GONE);
                                ll_cancelFriendRequest.setVisibility(View.VISIBLE);
                                ll_logOutAndEditBtn.setVisibility(View.GONE);
                                ll_sendFriendRequest.setVisibility(View.GONE);
                            }
                        }
                        else{
                            ll_unFriend.setVisibility(View.GONE);
                            ll_acceptFriedRequest.setVisibility(View.GONE);
                            ll_cancelFriendRequest.setVisibility(View.GONE);
                            ll_logOutAndEditBtn.setVisibility(View.GONE);
                            ll_sendFriendRequest.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        ll_unFriend.setVisibility(View.GONE);
                        ll_acceptFriedRequest.setVisibility(View.GONE);
                        ll_cancelFriendRequest.setVisibility(View.GONE);
                        ll_logOutAndEditBtn.setVisibility(View.GONE);
                        ll_sendFriendRequest.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int btnID = v.getId();
        if(btnID==R.id.btn_acceptFriend_userProfileActivity){
            acceptFriendRequest();
        }
        else if(btnID == R.id.btn_rejectFriend_userProfileActivity){
            rejectFriendRequest();
        }
        else if(btnID == R.id.btn_sendFriendReq_userProfileActivity){
            sendFriendRequest();
        }
        else if(btnID == R.id.btn_cancelFriendRequest_userProfileActivity){
            cancelFriendRequest();
        }

        else if(btnID == R.id.btn_unfriend_userProfileActivity){
            unFriend();
        }
    }


    public class AdapterUserProfile extends FragmentPagerAdapter {

        public AdapterUserProfile(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    BasicFragment basicFragment = new BasicFragment();
                    return basicFragment;

                case 1:
                    EducationFragment educationFragment = new EducationFragment();
                    return educationFragment;

                case 2:
                    PhotosFragment photosFragment = new PhotosFragment();
                    return photosFragment;
            }
            return  null;

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

        AdapterUserProfile adapterUserProfile = new AdapterUserProfile(getSupportFragmentManager()
                ,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(adapterUserProfile);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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



    public void profilePictureBtnPressed(View view){
        //Toast.makeText(this, "Edit Profile CLicked", Toast.LENGTH_SHORT).show();
        databaseReference.child(NodeNames.USERS).child(userID).child(NodeNames.PROFILE_PICTURES).child(NodeNames.PHOTO)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(!snapshot.getValue().toString().equals("")){
                                Uri photoUri = Uri.parse(snapshot.getValue().toString());
                                Intent intent = new Intent(ACTION_VIEW,photoUri);
                                intent.setDataAndType(photoUri,"image/jpg");
                                startActivity(intent);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }









    public void btnLogoutClicked(View view){

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUID = currentUser.getUid();
        DatabaseReference dbrTokensMy = mRootRef.child(NodeNames.TOKENS_FOLDER).child(myUID);


        dbrTokensMy.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseAuth.signOut();
                    startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                    finish();

                }
                else{
                    Toast.makeText(UserProfileActivity.this, getString(R.string.somethingWentWrong,task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }





    public void sendFriendRequest(){
        //Toast.makeText(this, "Presed", Toast.LENGTH_SHORT).show();
       DatabaseReference dbrRef = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
               child(myUID).child(userID).child(NodeNames.REQUEST_TYPE);

       dbrRef.setValue(Constants.FRIEND_REQUEST_TYPE_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                           child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_RECEIVED)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.friendRequestHasBeenSentSuccessfully), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        ll_sendFriendRequest.setVisibility(View.GONE);
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.failedToSendFriendRequest), Toast.LENGTH_SHORT).show();
                                    }
                               }
                           });
               }
               else{
                   Toast.makeText(UserProfileActivity.this, getString(R.string.failedToSendFriendRequest), Toast.LENGTH_SHORT).show();
               }
           }
       });
    }






    public void cancelFriendRequest(){
        //Toast.makeText(this, "Presed", Toast.LENGTH_SHORT).show();

        DatabaseReference dbrRef = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                child(myUID).child(userID).child(NodeNames.REQUEST_TYPE);

        dbrRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                            child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(null)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.canceledFriendRequest), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        ll_sendFriendRequest.setVisibility(View.GONE);
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.failedToCancelFriendRequest), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(UserProfileActivity.this, getString(R.string.failedToCancelFriendRequest), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }






    public void acceptFriendRequest(){
        //Toast.makeText(this, "Presed", Toast.LENGTH_SHORT).show();

        DatabaseReference dbrRef = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                child(myUID).child(userID).child(NodeNames.REQUEST_TYPE);

        dbrRef.setValue(Constants.FRIEND_REQUEST_TYPE_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                            child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_ACCEPTED)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Map hashMap = new HashMap();
                                        hashMap.put(NodeNames.TIME_STAMP,ServerValue.TIMESTAMP);
                                        hashMap.put(NodeNames.USER_ID,userID);
                                        databaseReference.child(NodeNames.CHAT_FOLDER).child(myUID).child(userID)
                                            .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Map hashMap2 = new HashMap();
                                                        hashMap2.put(NodeNames.TIME_STAMP,ServerValue.TIMESTAMP);
                                                        hashMap2.put(NodeNames.USER_ID,myUID);
                                                        databaseReference.child(NodeNames.CHAT_FOLDER).child(userID).child(myUID)
                                                                .setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    ll_acceptFriedRequest.setVisibility(View.GONE);
                                                                    Toast.makeText(UserProfileActivity.this, getString(R.string.acceptedFriendRequest), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else{

                                                    }
                                                }
                                            });
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.friendRequestHasBeenSentSuccessfully), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        ll_sendFriendRequest.setVisibility(View.GONE);
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.failedToSendFriendRequest), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(UserProfileActivity.this, getString(R.string.failedToSendFriendRequest), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    public void rejectFriendRequest(){
        DatabaseReference dbrRef = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                child(myUID).child(userID).child(NodeNames.REQUEST_TYPE);

        dbrRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                            child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(null)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        ll_acceptFriedRequest.setVisibility(View.GONE);
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.rejectedFriendRequest), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        ll_sendFriendRequest.setVisibility(View.GONE);
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.failedToRejectFriendRequest), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(UserProfileActivity.this, getString(R.string.failedToRejectFriendRequest), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }






    public void unFriend(){
        DatabaseReference dbrRef = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                child(myUID).child(userID).child(NodeNames.REQUEST_TYPE);

        dbrRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Log.d("tag","s1");
                    databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).
                            child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(null)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Log.d("tag","s2");
                                        databaseReference.child(NodeNames.CHAT_FOLDER).child(myUID).child(userID).setValue(null)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    //Log.d("tag","s3");
                                                    databaseReference.child(NodeNames.CHAT_FOLDER).child(userID).child(myUID).setValue(null)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                //Log.d("tag","s4");
                                                                ll_unFriend.setVisibility(View.GONE);
                                                                Toast.makeText(UserProfileActivity.this, getString(R.string.successFullyRemoved), Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Toast.makeText(UserProfileActivity.this, getString(R.string.failedToUnfriend,task.getException()),
                                                                        Toast.LENGTH_SHORT).show();
                                                                //Log.d("tag","f4");
                                                            }
                                                        }
                                                    });
                                                }
                                                else{
                                                    Toast.makeText(UserProfileActivity.this, getString(R.string.failedToUnfriend,task.getException()),
                                                            Toast.LENGTH_SHORT).show();
                                                    //Log.d("tag","f3");

                                                }
                                            }
                                        });
                                      /*  Toast.makeText(UserProfileActivity.this, getString(R.string.failedToUnfriend,task.getException()),
                                                Toast.LENGTH_SHORT).show();
                                        Log.d("tag","f3");*/
                                    }
                                    else{
                                        ll_sendFriendRequest.setVisibility(View.GONE);
                                        Toast.makeText(UserProfileActivity.this, getString(R.string.failedToUnfriend,task.getException()),
                                                Toast.LENGTH_SHORT).show();
                                        //Log.d("tag","f2");
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(UserProfileActivity.this, getString(R.string.failedToSendFriendRequest), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}