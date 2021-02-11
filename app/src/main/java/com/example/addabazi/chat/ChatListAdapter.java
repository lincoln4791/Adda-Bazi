package com.example.addabazi.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.Util;
import com.example.addabazi.profile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private Context context;
    private List<ChatListModelClass> chatListModelClassList;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String myUID = FirebaseAuth.getInstance().getUid();

    public ChatListAdapter(Context context, List<ChatListModelClass> chatListModelClassList) {
        this.context = context;
        this.chatListModelClassList = chatListModelClassList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chatlist_sample_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userName.setText(chatListModelClassList.get(position).getUserName());

        String lastMessageTime = chatListModelClassList.get(position).getLastMessageTime()!=null?
                chatListModelClassList.get(position).getLastMessageTime():"";

        if(lastMessageTime.equals("")){
              holder.lastMessageTime.setText("");
        }
        else{
            holder.lastMessageTime.setText(Util.getTimeAgo(Long.parseLong(chatListModelClassList.get(position).getLastMessageTime())));
        }
        //holder.lastMessageTime.setText(Util.getTimeAgo(Long.parseLong(lastMessageTime)));

        //showing last message
        String lastMessage = chatListModelClassList.get(position).getLastMessage();
        if(lastMessage.startsWith("https://firebasestorage.")){
            lastMessage = context.getString(R.string.imageReceived);
            holder.lastMessage.setText(lastMessage);
        }
        else{
            if(lastMessage.length()>30){
                holder.lastMessage.setText(lastMessage.substring(0,30));
            }else{
                holder.lastMessage.setText(lastMessage);
            }
        }



        databaseReference.child(NodeNames.USERS).child(chatListModelClassList.get(position).getUserID()).child(NodeNames.PROFILE_PICTURES)
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

        StorageReference storageReferenceProfilePicture = FirebaseStorage.getInstance().getReference().child(NodeNames.IMAGES_FOLDER).child(chatListModelClassList.get(position).getUserID()+".jpg");
        storageReferenceProfilePicture.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    storageReferenceProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).placeholder(R.drawable.default_profile_picture).error(R.drawable.default_profile_picture).into(holder.profilePicture);

                        }
                    });
                }
                else{
                    //Toast.makeText(context, "Failed to load Image"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Unread message count
        if(!chatListModelClassList.get(position).getUnreadMessageCount().equals("0")){
            holder.unreadMessageCount.setVisibility(View.VISIBLE);
            holder.unreadMessageCount.setText(chatListModelClassList.get(position).getUnreadMessageCount());
        }
        else{
            holder.unreadMessageCount.setVisibility(View.INVISIBLE);
        }



        holder.ll_userNameHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra(Extras.USER_KEY,chatListModelClassList.get(position).getUserID());
                intent.putExtra(Extras.USER_NAME,chatListModelClassList.get(position).getUserName());
                intent.putExtra(Extras.USER_PHOTO_NAME,chatListModelClassList.get(position).getPhotoName());
                context.startActivity(intent);
            }
        });





        holder.ll_imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                profileIntent.putExtra(Extras.USER_KEY,chatListModelClassList.get(position).getUserID());
                context.startActivity(profileIntent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return chatListModelClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userID;
        private ImageView profilePicture;
        private TextView unreadMessageCount;
        private TextView lastMessage;
        private TextView lastMessageTime;
        private LinearLayout ll_imageHolder,ll_userNameHolder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_userName_chatListSampleLayout_ID);
            profilePicture = itemView.findViewById(R.id.iv_profilepicture_chatListSampleLyaout_ID);
            //layout = itemView.findViewById(R.id.layout_allViewHolder_chtFragmentSampleLayout_ID);
            unreadMessageCount = itemView.findViewById(R.id.tv_unreadMessageCounter_chatListSampleLayout_ID);
            lastMessage = itemView.findViewById(R.id.tv_lastMessage_chatListSampleLayout_ID);
            lastMessageTime = itemView.findViewById(R.id.tv_lastMessageTIming_chatListSampleLayout_ID);
            ll_imageHolder = itemView.findViewById(R.id.ll_imageHolder_chatListSampleLayout_ID);
            ll_userNameHolder = itemView.findViewById(R.id.ll_userNameHolder_chatListSampleLayout_ID);
        }
    }
}
