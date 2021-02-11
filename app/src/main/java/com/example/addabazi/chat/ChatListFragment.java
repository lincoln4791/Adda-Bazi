package com.example.addabazi.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.friendRequests.FriendRequestsAdapter;
import com.example.addabazi.friendRequests.FriendRequestsModelClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {
    private TextView emptyChatListText;
    private View customProgressbar;
    private ChatListAdapter adapter;
    private DatabaseReference dbrUsers,dbrChats,dbrFriendRequests;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private String myUID;
    private  int counter=0,counter2=0;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ChildEventListener childEventListener;
    private Query query;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myUID = FirebaseAuth.getInstance().getUid();


        recyclerView = view.findViewById(R.id.rv_fragment_chat_chatlist_ID);
        customProgressbar = view.findViewById(R.id.customProgressbar_chatFragment_ID);
        emptyChatListText = view.findViewById(R.id.tv_fragment_chat_emptychatlistText_ID);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        dbrUsers = databaseReference.child(NodeNames.USERS);
        dbrChats = databaseReference.child(NodeNames.CHAT_FOLDER);
        dbrFriendRequests = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).child(currentUser.getUid());
        query= dbrChats.child(myUID).orderByChild(NodeNames.TIME_STAMP);

        customProgressbar.setVisibility(View.VISIBLE);
        emptyChatListText.setVisibility(View.VISIBLE);


        dbrChats.child(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customProgressbar.setVisibility(View.GONE);
                //Log.d("tag","snapshot triggered");
                loadProfiles(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




/*        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot,true,snapshot.getKey());
                counter++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot,true,snapshot.getKey());

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        };
        query.addChildEventListener(childEventListener);*/
        //adapter.notifyDataSetChanged();

/*        if(counter == 0){
            emptyChatListText.setVisibility(View.VISIBLE);
        }
        customProgressbar.setVisibility(View.GONE);*/
        //Log.d("tag","counter1 : "+counter);
        //Log.d("tag","counter2 : "+counter2);

    }

    private void loadProfiles(DataSnapshot snapshot) {
        List<ChatListModelClass> chatListModelClassList;
        chatListModelClassList = new ArrayList<>();
        adapter = new ChatListAdapter(getContext(),chatListModelClassList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        if(snapshot.exists()){
            emptyChatListText.setVisibility(View.GONE);
            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                String lastMessage = "";
                String lastMessageTime = "";
                String unreadMessageCount = "";
                String userID = dataSnapshot.child(NodeNames.USER_ID).getValue().toString();

                if (dataSnapshot.child(NodeNames.LAST_MESSAGE).exists()) {
                    if (!dataSnapshot.child(NodeNames.LAST_MESSAGE).toString().equals("")) {
                        lastMessage = dataSnapshot.child(NodeNames.LAST_MESSAGE).getValue().toString();
                    }
                }

                if (dataSnapshot.child(NodeNames.LAST_MESSAGE_TIME).exists()) {
                    if (!dataSnapshot.child(NodeNames.LAST_MESSAGE_TIME).toString().equals("")) {
                        lastMessageTime = dataSnapshot.child(NodeNames.LAST_MESSAGE_TIME).getValue().toString();
                    }
                }

                if (dataSnapshot.child(NodeNames.UNREAD_COUNT).exists()) {
                    if (!dataSnapshot.child(NodeNames.UNREAD_COUNT).toString().equals("")) {
                        unreadMessageCount = dataSnapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();
                    }
                }

                String finalUnreadMessageCount = unreadMessageCount;
                String finalLastMessage = lastMessage;
                String finalLastMessageTime = lastMessageTime;
                dbrUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = "";
                        String userPhoto = "";
                        if(snapshot.child(NodeNames.NAME).exists()){
                            if(!snapshot.child(NodeNames.NAME).getValue().toString().equals("")){
                                userName = snapshot.child(NodeNames.NAME).getValue().toString();
                            }
                        }

                        if(snapshot.child(NodeNames.PROFILE_PICTURES).child(NodeNames.PHOTO).exists()){
                            if(!snapshot.child(NodeNames.NAME).getValue().toString().equals("")){
                                userPhoto = snapshot.child(NodeNames.PROFILE_PICTURES).child(NodeNames.PHOTO).getValue().toString();
                            }
                        }

                        ChatListModelClass object = new ChatListModelClass(userName,userID,userPhoto, finalUnreadMessageCount, finalLastMessage, finalLastMessageTime);
                        chatListModelClassList.add(object);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


        }
        else{
            customProgressbar.setVisibility(View.GONE);
            emptyChatListText.setVisibility(View.VISIBLE);
        }
    }







/*    private void updateList(DataSnapshot snapshot,Boolean isNew, String userID) {
        customProgressbar.setVisibility(View.GONE);
        List<ChatListModelClass> chatListModelClassList;
        chatListModelClassList = new ArrayList<>();
        adapter = new ChatListAdapter(getContext(),chatListModelClassList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        chatListModelClassList.clear();

        emptyChatListText.setVisibility(View.GONE);
        customProgressbar.setVisibility(View.GONE);
        //chatListModelClassList.clear();
        String lastMessage = "";
        String lastMessageTime = "";
        String unreadMessageCount = "";

        if (snapshot.child(NodeNames.LAST_MESSAGE).exists()) {
            if (!snapshot.child(NodeNames.LAST_MESSAGE).toString().equals("")) {
                lastMessage = snapshot.child(NodeNames.LAST_MESSAGE).getValue().toString();
            }
        }

        if (snapshot.child(NodeNames.LAST_MESSAGE_TIME).exists()) {
            if (!snapshot.child(NodeNames.LAST_MESSAGE_TIME).toString().equals("")) {
                lastMessageTime = snapshot.child(NodeNames.LAST_MESSAGE_TIME).getValue().toString();
            }
        }

        if (snapshot.child(NodeNames.UNREAD_COUNT).exists()) {
            if (!snapshot.child(NodeNames.UNREAD_COUNT).toString().equals("")) {
                unreadMessageCount = snapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();
            }
        }

        String finalUnreadMessageCount = unreadMessageCount;
        String finalLastMessage = lastMessage;
        String finalLastMessageTime = lastMessageTime;
        dbrUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = "";
                String photo = "";

                if (snapshot.child(NodeNames.NAME).exists()) {
                    if (!snapshot.child(NodeNames.NAME).toString().equals("")) {
                        userName = snapshot.child(NodeNames.NAME).getValue().toString();
                    }
                }

                if (snapshot.child(NodeNames.PHOTO).exists()) {
                    if (!snapshot.child(NodeNames.PHOTO).toString().equals("")) {
                        photo = snapshot.child(NodeNames.NAME).getValue().toString();
                    }
                }


                ChatListModelClass chatListModelClassObject = new ChatListModelClass(userName, userID, photo, finalUnreadMessageCount, finalLastMessage, finalLastMessageTime);
                chatListModelClassList.add(chatListModelClassObject);
                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/


}