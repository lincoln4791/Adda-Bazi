package com.example.addabazi.selectFriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.addabazi.R;
import com.example.addabazi.chat.ChatsActivity;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectFriendAdapter selectFriendAdapter;
    private List<SelectFriendModelClass> selectFriendModelClassList;
    private StorageReference mRootRef,storageImagesFolder;
    private DatabaseReference dbrRoot,dbrUsers,dbrChats;
    private String myUID = FirebaseAuth.getInstance().getUid();
    private String selectedMessage,selectedMessageID,selectedMessageType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        if(getIntent().hasExtra(Extras.MESSAGE )){
            selectedMessage = getIntent().getStringExtra(Extras.MESSAGE);
        }
        if(getIntent().hasExtra(Extras.MESSAGE_ID )){
            selectedMessageID = getIntent().getStringExtra(Extras.MESSAGE_ID);
        }
        if(getIntent().hasExtra(Extras.MESSAGE_TYPE )){
            selectedMessageType = getIntent().getStringExtra(Extras.MESSAGE_TYPE);
        }

        recyclerView = findViewById(R.id.rvSelectFriendActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectFriendModelClassList = new ArrayList<>();
        selectFriendAdapter = new SelectFriendAdapter(this,selectFriendModelClassList);
        recyclerView.setAdapter(selectFriendAdapter);
        dbrRoot= FirebaseDatabase.getInstance().getReference();
        dbrChats = dbrRoot.child(NodeNames.CHAT_FOLDER);
        dbrUsers = dbrRoot.child(NodeNames.USERS);

        dbrChats.child(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String  userID = dataSnapshot.getKey();
                        dbrUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userName = snapshot.child(NodeNames.NAME).getValue().toString();
                                String photoName = snapshot.child(NodeNames.PHOTO) != null?snapshot.child(NodeNames.PHOTO).toString():"";

                                SelectFriendModelClass selectFriendModelClassObject = new SelectFriendModelClass(userID,userName,photoName);
                                selectFriendModelClassList.add(selectFriendModelClassObject);
                                selectFriendAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
                else{
                    Toast.makeText(SelectFriendActivity.this, "DatabaseNOt exists", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public  void  returnData(String returnedUserID, String returnedUserName, String returnedUserPhoto){
        Intent intent = new Intent();
        intent.putExtra(Extras.USER_KEY,returnedUserID);
        intent.putExtra(Extras.USER_NAME,returnedUserName);
        intent.putExtra(Extras.USER_PHOTO_NAME,returnedUserPhoto);

        intent.putExtra(Extras.MESSAGE,selectedMessage);
        intent.putExtra(Extras.MESSAGE_TYPE,selectedMessageType);
        intent.putExtra(Extras.MESSAGE_ID,selectedMessageID);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}