package com.example.addabazi.selectFriend;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.MyViewHolder> {
    private Context context;
    private List<SelectFriendModelClass> selectFriendModelClassList;
    private StorageReference mRootRef = FirebaseStorage.getInstance().getReference();
    private String myUid = FirebaseAuth.getInstance().getUid();

    public SelectFriendAdapter(Context context, List<SelectFriendModelClass> selectFriendModelClassList) {
        this.context = context;
        this.selectFriendModelClassList = selectFriendModelClassList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout_select_friend_activity,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_userName.setText(selectFriendModelClassList.get(position).getUserName());
        //StorageReference photoRef = mRootRef.child(NodeNames.IMAGES_FOLDER+"/"+selectFriendModelClassList.get(position).getUserID()+".jpg");
        StorageReference photoRef = mRootRef.child(NodeNames.IMAGES_FOLDER).child(selectFriendModelClassList.get(position).getUserID()+".jpg");

        photoRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).placeholder(R.drawable.default_profile_picture)
                                    .error(R.drawable.default_profile_picture).into(holder.iv_profilePicture);
                        }
                    });
                }

            }
        });


    holder.cl_sampleSelectFriendLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(context, "Holder Clicked", Toast.LENGTH_SHORT).show();
            String userName = selectFriendModelClassList.get(position).getUserName();
            String userID = selectFriendModelClassList.get(position).getUserID();
            String userPhoto = selectFriendModelClassList.get(position).getUserID()+".jpg";
            if(context instanceof SelectFriendActivity){
            ((SelectFriendActivity)context).returnData(userID,userName,userPhoto);
            }
        }
    });
    }

    @Override
    public int getItemCount() {
        return selectFriendModelClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_sampleSelectFriendLayout;
        TextView tv_userName;
        ImageView iv_profilePicture;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cl_sampleSelectFriendLayout = itemView.findViewById(R.id.cl_sampleLayout_selectFriendActivity);
            tv_userName=itemView.findViewById(R.id.tv_username_sampleSelectFriendLayout);
            iv_profilePicture = itemView.findViewById(R.id.iv_profilePicture_sampleSelectFriendLayout);
        }
    }
}
