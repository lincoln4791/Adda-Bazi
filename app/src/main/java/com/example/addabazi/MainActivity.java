package com.example.addabazi;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.addabazi.chat.ChatListFragment;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.findFriends.FIndFriendFragment;
import com.example.addabazi.friendRequests.FriendRequestsFragment;
import com.example.addabazi.profile.ProfileActivity;
import com.example.addabazi.profile.UserProfileActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Boolean doubleBackPress =false;
    private DatabaseReference dbrUsers;
    private DatabaseReference mRootRef;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager_MAinActivity_ID);
        tabLayout = findViewById(R.id.tabLayout_ID);
        dbrUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        myUid = FirebaseAuth.getInstance().getUid();



        mRootRef.child(NodeNames.ONLINE_STATUS_FOLDER).child(myUid).child(NodeNames.ONLINE).setValue(Constants.USER_ONLINE);
        mRootRef.child(NodeNames.ONLINE_STATUS_FOLDER).child(myUid).child(NodeNames.ONLINE).onDisconnect().setValue(Constants.USER_OFFLINE);
        setViewPager();

    }




    public class Adapter extends FragmentPagerAdapter {

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    ChatListFragment chatFragment = new ChatListFragment();
                    return  chatFragment;

                case 1:
                    FriendRequestsFragment friendRequestsFragment = new FriendRequestsFragment();
                    return friendRequestsFragment;

                case 2:
                    FIndFriendFragment fIndFriendFragment = new FIndFriendFragment();
                    return fIndFriendFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }
    }






    private void setViewPager(){
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_chat));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_friend_requests));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_find));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Adapter adapter = new Adapter(getSupportFragmentManager(),Adapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

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






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if(id == R.id.menu_profile){
                //startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                Intent profileIntent = new Intent(this, UserProfileActivity.class);
                profileIntent.putExtra(Extras.USER_KEY,myUid);
                startActivity(profileIntent);
            }
        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(tabLayout.getSelectedTabPosition()>0){
            tabLayout.selectTab(tabLayout.getTabAt(0));
        }

        else{
                if(!doubleBackPress){
                    doubleBackPress = true;
                    Toast.makeText(this, getString(R.string.pressBAckButtonAgainToCloseTheApplication), Toast.LENGTH_SHORT).show();
                    Handler handler =  new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackPress =false;
                        }
                    },1500);
                }

                else{
                    finishAffinity();
                }

        }

    }

}