package com.example.addabazi.friendRequests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsFragment extends Fragment {
    private TextView emptyFriendRequestListText;
    private View customProgressbar;
    private ProgressBar progressBar;
    private DatabaseReference dbrUsers;
    private StorageReference storageReference;
    private  int counterEmptyFriendRequestListCheck = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recyclerView = view.findViewById(R.id.rv_fragment_friendRequest_friendRequestList_ID);
        customProgressbar = view.findViewById(R.id.progressbar_fragmentFriendRequests_ID);
        emptyFriendRequestListText = view.findViewById(R.id.tv_fragment_friendRequest_emptyFriendRequestListText_ID);
        progressBar = view.findViewById(R.id.progressBar_friendRequestsSampleLayout_ID);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbrUsers = databaseReference.child(NodeNames.USERS);
        DatabaseReference dbrFriendRequestsOwn = databaseReference.child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).child(currentUser.getUid());

        customProgressbar.setVisibility(View.VISIBLE);
        //emptyFriendRequestListText.setVisibility(View.VISIBLE);
        //Log.d("tag","friendRequestFragent started");

        dbrFriendRequestsOwn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FriendRequestsModelClass> friendRequestsModelClassList;
                friendRequestsModelClassList = new ArrayList<>();
                FriendRequestsAdapter adapter;
                adapter = new FriendRequestsAdapter(getContext(),friendRequestsModelClassList);
                recyclerView.setAdapter(adapter);
                friendRequestsModelClassList.clear();

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String requestType = dataSnapshot.child(NodeNames.REQUEST_TYPE).getValue().toString();
                        if(requestType.equals(Constants.FRIEND_REQUEST_TYPE_RECEIVED)){
                            counterEmptyFriendRequestListCheck++;
                            //Log.d("tag","FriendRequestFragment, dbrFriendRequest value event listener -- counter : "+counter);
                            String userIDFromWhomRequestReceived = dataSnapshot.getKey();
                            dbrUsers.child(userIDFromWhomRequestReceived).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String userName = "";
                                        String userPhoto = "";
                                        if(snapshot.child(NodeNames.NAME).exists()){
                                            userName  = !snapshot.child(NodeNames.NAME).getValue().toString().equals("")?
                                                    snapshot.child(NodeNames.NAME).getValue().toString():"";
                                        }

                                        if(snapshot.child(NodeNames.PHOTO).exists()){
                                            userPhoto = !snapshot.child(NodeNames.PHOTO).getValue().toString().equals("")?
                                                    snapshot.child(NodeNames.PHOTO).getValue().toString():"";
                                        }

                                        FriendRequestsModelClass friendRequestsModelClass = new FriendRequestsModelClass(userName,userPhoto,userIDFromWhomRequestReceived);
                                        friendRequestsModelClassList.add(friendRequestsModelClass);
                                        adapter.notifyDataSetChanged();



                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }

                }
                //Log.d("tag","Friend Request datasnapshot loop finished2, Counter = "+counter);
                customProgressbar.setVisibility(View.GONE);
                if(counterEmptyFriendRequestListCheck == 0){
                    //Toast.makeText(getContext(), "counter = "+counter, Toast.LENGTH_SHORT).show();
                    emptyFriendRequestListText.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getContext(), "Failed to load friend req fragment "+error, Toast.LENGTH_SHORT).show();
            }
        });
    }



}