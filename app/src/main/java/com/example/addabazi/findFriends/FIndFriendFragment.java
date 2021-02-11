package com.example.addabazi.findFriends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FIndFriendFragment extends Fragment {
    private RecyclerView rv_friendsList_FindFriends;
    private TextView tvEmptyFindFriendListText;
    public  View progressBar;
    private DatabaseReference dbrUsers, dbrFriendRequestFolder_own;
    private String myUID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_ind_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_friendsList_FindFriends = view.findViewById(R.id.rv_fragment_findFriend_findFriendList_ID);
        tvEmptyFindFriendListText = view.findViewById(R.id.tv_fragment_findFriend_emptyFindFriendListText_ID);
        progressBar = view.findViewById(R.id.progressBar);
        dbrUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        myUID = FirebaseAuth.getInstance().getUid();
        dbrFriendRequestFolder_own = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER).child(myUID);

        loadFineFiendsList();

    }

    private void loadFineFiendsList() {
        rv_friendsList_FindFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        tvEmptyFindFriendListText.setVisibility(View.GONE);



        Query query = dbrUsers.orderByChild(NodeNames.NAME);
        //loadProfiles(query);
        dbrFriendRequestFolder_own.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loadProfiles(query);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void loadProfiles(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FindFriendModelClass> findFriendModelClassList;
                findFriendModelClassList = new ArrayList<>();
                FindFriendsAdapter findFriendsAdapter;
                findFriendsAdapter = new FindFriendsAdapter(getContext(),findFriendModelClassList);
                rv_friendsList_FindFriends.setAdapter(findFriendsAdapter);

                progressBar.setVisibility(View.VISIBLE);

                if(snapshot.exists()){
                    findFriendModelClassList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String userID = dataSnapshot.getKey();

                        if(userID.equals(myUID)){
                            //return;
                        }
                        else{

                            dbrFriendRequestFolder_own.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String fullName = "";
                                    String photoName = "";

                                    if(dataSnapshot.child(NodeNames.NAME).exists()){
                                        if(dataSnapshot.child(NodeNames.NAME)!=null){
                                            fullName = dataSnapshot.child(NodeNames.NAME).getValue().toString();
                                        }

                                    }
                                    if(dataSnapshot.child(NodeNames.PHOTO).exists()){
                                        if(dataSnapshot.child(NodeNames.PHOTO)!=null){
                                            photoName = dataSnapshot.child(NodeNames.PHOTO).getValue().toString();
                                        }
                                    }
                                    if(snapshot.exists()){
                                        if(snapshot.child(NodeNames.REQUEST_TYPE).getValue().toString().equals(Constants.FRIEND_REQUEST_TYPE_SENT)){
                                            FindFriendModelClass findFriendModelClassObject = new FindFriendModelClass(fullName,photoName,userID,true);
                                            findFriendModelClassList.add(findFriendModelClassObject);
                                            findFriendsAdapter.notifyDataSetChanged();
                                        }


                                    }
                                    else{
                                        FindFriendModelClass findFriendModelClassObject = new FindFriendModelClass(fullName,photoName,userID,false);
                                        findFriendModelClassList.add(findFriendModelClassObject);
                                        findFriendsAdapter.notifyDataSetChanged();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Database Roor", Toast.LENGTH_SHORT).show();

                                    progressBar.setVisibility(View.GONE);

                                }

                            });

                        }
                    }


                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Failed to load users",Toast.LENGTH_SHORT);
                progressBar.setVisibility(View.GONE);
                tvEmptyFindFriendListText.setVisibility(View.VISIBLE);
            }
        });
    }


}