package com.example.addabazi.findFriends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.Util;
import com.example.addabazi.profile.ProfileActivity;
import com.example.addabazi.profile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.MyViewHolder> {

    private Context context;
    private List<FindFriendModelClass> findFriendsModelClassList;
    private DatabaseReference friendRequestDatabase;
    private String myUID;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public FindFriendsAdapter(Context context, List<FindFriendModelClass> findFriendsModelClassList) {
        this.context = context;
        this.findFriendsModelClassList = findFriendsModelClassList;
    }

    @NonNull
    @Override
    public FindFriendsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(context).inflate(R.layout.find_friends_sample_layout,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FindFriendsAdapter.MyViewHolder holder, int position) {


        holder.userName.setText(findFriendsModelClassList.get(position).getUserName());
        String userID = findFriendsModelClassList.get(position).getUserID();



        databaseReference.child(NodeNames.USERS).child(findFriendsModelClassList.get(position).getUserID()).child(NodeNames.PROFILE_PICTURES)
                .child(NodeNames.PHOTO).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(!snapshot.getValue().toString().equals("")){
                                String userPhotoRef = snapshot.getValue().toString();
                                Glide.with(context).load(Uri.parse(userPhotoRef)).into(holder.profilePicture);
                            }
                            else{
                                Glide.with(context).load(R.drawable.default_profile_picture).into(holder.profilePicture);
                            }

                        }
                        else{
                            Glide.with(context).load(R.drawable.default_profile_picture).into(holder.profilePicture);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        if(findFriendsModelClassList.get(position).isRequestSent()){
            holder.btnSendFriendRequest.setVisibility(View.GONE);
            holder.btnCancelFriendRequest.setVisibility(View.VISIBLE);
        }
        else{
            holder.btnSendFriendRequest.setVisibility(View.VISIBLE);
            holder.btnCancelFriendRequest.setVisibility(View.GONE);
        }










        holder.btnSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //customProgressBar.setVisibility(View.VISIBLE);
                holder.btnSendFriendRequest.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER);
                myUID = FirebaseAuth.getInstance().getUid();
                friendRequestDatabase.child(myUID).child(userID).child(NodeNames.REQUEST_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_SENT)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestDatabase.child(userID).child(myUID).child(NodeNames.REQUEST_TYPE)
                                    .setValue(Constants.FRIEND_REQUEST_TYPE_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        holder.btnCancelFriendRequest.setVisibility(View.VISIBLE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(context, R.string.friendRequestHasBeenSentSuccessfully, Toast.LENGTH_SHORT).show();
                                        String notificationData = context.getString(R.string.incomingFriendRequestFrom,currentUser.getDisplayName());
                                        String notificationTitle = context.getString(R.string.incomingFriendRequest);
                                        Util.sendNotification(context,notificationTitle,notificationData,userID);
                                        //notifyDataSetChanged();
                                    }
                                    else{
                                        holder.btnSendFriendRequest.setVisibility(View.VISIBLE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(context, R.string.failedToSendFriendRequest, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                        else {
                            holder.btnSendFriendRequest.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, R.string.failedToSendFriendRequest, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });








        holder.btnCancelFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnCancelFriendRequest.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER);
                myUID = FirebaseAuth.getInstance().getUid();
                friendRequestDatabase.child(myUID).child(userID).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestDatabase.child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        holder.btnSendFriendRequest.setVisibility(View.VISIBLE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(context,R.string.canceledFriendRequestSuccessfully, Toast.LENGTH_SHORT).show();
                                        String notificationData = context.getString(R.string.canceledFriendRequestFrom,currentUser.getDisplayName());
                                        String notificationTitle = context.getString(R.string.canceledFriendRequest);
                                        Util.sendNotification(context,notificationTitle,notificationData,userID);
                                        //notifyDataSetChanged();
                                    }
                                    else {
                                        holder.btnCancelFriendRequest.setVisibility(View.VISIBLE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(context,context.getString(R.string.failedToCancelFriendRequest,task.getException()), Toast.LENGTH_SHORT).show();
                                        //notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                        else{
                            holder.btnCancelFriendRequest.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(context,context.getString(R.string.failedToCancelFriendRequest,task.getException()), Toast.LENGTH_SHORT).show();
                            //notifyDataSetChanged();
                        }
                    }
                });

            }
        });








        holder.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                //profileIntent.putExtra(Extras.USER_NAME,findFriendsModelClassList.get(position).getUserName());
                profileIntent.putExtra(Extras.USER_KEY,findFriendsModelClassList.get(position).getUserID());
                context.startActivity(profileIntent);
            }
        });






    }

    @Override
    public int getItemCount() {
        return findFriendsModelClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePicture;
        private TextView userName;
        private Button btnSendFriendRequest,btnCancelFriendRequest;
        private ProgressBar progressBar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.iv_profilePicture_findFriendSampleLayout_ID);
            userName = itemView.findViewById(R.id.tv_userName_findFriendsSampleLayout_ID);
            btnSendFriendRequest = itemView.findViewById(R.id.btn_sendFriendRequest_findFriendsSampleLayout_ID);
            btnCancelFriendRequest = itemView.findViewById(R.id.btn_cancelFriendRequest_findFriendsSampleLayout_ID);
            progressBar = itemView.findViewById(R.id.progressBar_findFriendsSampleLayoutID);

        }
    }
}
