package com.example.addabazi.friendRequests;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.Util;
import com.example.addabazi.findFriends.FIndFriendFragment;
import com.example.addabazi.profile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder> {
    private Context context;
    private List<FriendRequestsModelClass> friendRequestsModelClassList;
    private DatabaseReference friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST_STATUS_FOLDER);
    private DatabaseReference dbrChat = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHAT_FOLDER);
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String myUID = currentUser.getUid();
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public FriendRequestsAdapter(Context context, List<FriendRequestsModelClass> friendRequestsModelClassList) {
        this.context = context;
        this.friendRequestsModelClassList = friendRequestsModelClassList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_requests_sample_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.username.setText(friendRequestsModelClassList.get(position).getUserName());









        holder.ll_allViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                String userID = friendRequestsModelClassList.get(position).getUserID();
                profileIntent.putExtra(Extras.USER_KEY,userID);
                context.startActivity(profileIntent);

            }
        });









        databaseReference.child(NodeNames.USERS).child(friendRequestsModelClassList.get(position).getUserID()).child(NodeNames.PROFILE_PICTURES)
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




        holder.btnRejectFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userID = friendRequestsModelClassList.get(position).getUserID();

                holder.btnRejectFriendRequest.setVisibility(View.GONE);
                holder.btnAcceptFriendRequest.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                friendRequestDatabase.child(myUID).child(userID).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestDatabase.child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       holder.btnRejectFriendRequest.setVisibility(View.VISIBLE);
                                       holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                                       holder.progressBar.setVisibility(View.GONE);
                                       Toast.makeText(context,R.string.canceledFriendRequestSuccessfully, Toast.LENGTH_SHORT).show();
                                       String notificationData = context.getString(R.string.rejectedFriendRequestFrom,currentUser.getDisplayName());
                                       String notificationTitle = context.getString(R.string.rejectedFriendRequest);
                                       Util.sendNotification(context,notificationTitle,notificationData,userID);
                                   }
                                   else{
                                       holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                                       holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                                       holder.progressBar.setVisibility(View.GONE);
                                       Toast.makeText(context,context.getString(R.string.failedToRejectFriendRequest,task.getException()), Toast.LENGTH_SHORT).show();
                                   }

                                }
                            });
                        }
                        else{
                            holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                            holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(context,context.getString(R.string.failedToRejectFriendRequest,task.getException()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                friendRequestDatabase.child(myUID).child(userID).child(NodeNames.REQUEST_TYPE).setValue(null).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, context.getString(R.string.failedToRejectFriendRequest), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




        holder.btnAcceptFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userID = friendRequestsModelClassList.get(position).getUserID();

                holder.btnRejectFriendRequest.setVisibility(View.GONE);
                holder.btnAcceptFriendRequest.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);

                friendRequestDatabase.child(myUID).child(userID).child(NodeNames.REQUEST_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestDatabase.child(userID).child(myUID).child(NodeNames.REQUEST_TYPE).setValue(Constants.FRIEND_REQUEST_TYPE_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Map hashMap = new HashMap();
                                        hashMap.put(NodeNames.TIME_STAMP,ServerValue.TIMESTAMP);
                                        hashMap.put(NodeNames.USER_ID,userID);
                                        dbrChat.child(myUID).child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Map hashMap2 = new HashMap();
                                                    hashMap2.put(NodeNames.TIME_STAMP,ServerValue.TIMESTAMP);
                                                    hashMap2.put(NodeNames.USER_ID,myUID);
                                                    dbrChat.child(userID).child(myUID).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(context,context.getString(R.string.accepted) , Toast.LENGTH_SHORT).show();
                                                                holder.progressBar.setVisibility(View.GONE);
                                                                holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                                                                holder.btnRejectFriendRequest.setVisibility(View.VISIBLE);
                                                                String notificationData = context.getString(R.string.acceptedFriendRequestFrom,currentUser.getDisplayName());
                                                                String notificationTitle = context.getString(R.string.acceptedFriendRequest);
                                                                Util.sendNotification(context,notificationTitle,notificationData,userID);
                                                            }
                                                            else{
                                                               exceptionHandler(holder,task.getException());

                                                            }
                                                        }
                                                    });
                                                }
                                                else{
                                                    exceptionHandler(holder,task.getException());
                                                }
                                            }
                                        });

                                    }

                                    else{
                                        exceptionHandler(holder,task.getException());
                                    }
                                }
                            });
                        }

                        else{
                            exceptionHandler(holder,task.getException());
                        }

                    }
                });


            }
        });






    }



    @Override
    public int getItemCount() {
        return friendRequestsModelClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView profilePicture;
        private Button btnAcceptFriendRequest,btnRejectFriendRequest;
        private ProgressBar progressBar;
        private LinearLayout ll_allViewHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.tv_userName_friendRequestSampleLayout_ID);
            profilePicture = itemView.findViewById(R.id.profilePicture_friendRequestSampleLayout_ID);
            btnAcceptFriendRequest = itemView.findViewById(R.id.btn_acceptFriendRequest_friendRequestsSampleLayout_ID);
            btnRejectFriendRequest = itemView.findViewById(R.id.btn_rejectFriendRequest_friendRequestsSampleLayout_ID);
            progressBar = itemView.findViewById(R.id.progressBar_friendRequestsSampleLayout_ID);
            ll_allViewHolder = itemView.findViewById(R.id.ll_allViewHolder_sampleLayout_friendRequests);


        }
    }







    private void exceptionHandler(MyViewHolder holder ,Exception exception) {
        Toast.makeText(context,context.getString(R.string.failedToAcceptFriendRequest)+exception, Toast.LENGTH_SHORT).show();
        holder.btnRejectFriendRequest.setVisibility(View.VISIBLE);
        holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
    }
}
