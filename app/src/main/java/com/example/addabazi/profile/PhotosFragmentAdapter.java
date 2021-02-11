package com.example.addabazi.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.UtilStorage;
import com.google.android.gms.tasks.OnCompleteListener;
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



public class PhotosFragmentAdapter extends RecyclerView.Adapter<PhotosFragmentAdapter.MyViewHolder> {

    private Context context;
    private List<PhotosFragmentModelClass> photosFragmentModelClassList;
    public ActionMode actionMode;
    private ConstraintLayout selectedLayout;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String myUID = FirebaseAuth.getInstance().getUid();
    private String userID;

    public PhotosFragmentAdapter(Context context, List<PhotosFragmentModelClass> photosFragmentModelClassList) {
        this.context = context;
        this.photosFragmentModelClassList = photosFragmentModelClassList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_photos_fragment,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Uri photoUri = Uri.parse(photosFragmentModelClassList.get(position).getPhotoUri());
        Glide.with(context).load(photoUri).placeholder(R.drawable.ic_attachment)
                .error(R.drawable.ic_attachment).into(holder.iv_photo);


        holder.ll_Photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "On click ", Toast.LENGTH_SHORT).show();
                Uri photoUri = Uri.parse(photosFragmentModelClassList.get(position).getPhotoUri());

                Intent intent = new Intent(Intent.ACTION_VIEW,photoUri);
                intent.setDataAndType(photoUri,"image/jpg");
                context.startActivity(intent);

/*                databaseReference.child(NodeNames.IMAGES_FOLDER).child(myUID).child(NodeNames.PROFILE_PICTURES).child(folderName)
                        .child(NodeNames.URI).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    if(!snapshot.getValue().toString().equals("")){
                                        Uri photoUri = Uri.parse(snapshot.getValue().toString());
                                        Intent intent = new Intent(Intent.ACTION_VIEW,photoUri);
                                        intent.setDataAndType(photoUri,"image/jpg");
                                        context.startActivity(intent);
                                        Toast.makeText(context, "GG", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(context, "GG2", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(context, "GG3", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, context.getString(R.string.dataBaseError,error.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        });*/


            }
        });






        holder.ll_Photos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                    if(actionMode!=null){
                        return false;

                    }
                    else{
                        Toast.makeText(context, "On Long Pressed", Toast.LENGTH_SHORT).show();
                        holder.ll_Photos.setTag(R.id.TAG_PHOTO_FILE_NAME,photosFragmentModelClassList.get(position).getFileName());
                        holder.ll_Photos.setTag(R.id.TAG_PHOTO_FOLDER_NAME,photosFragmentModelClassList.get(position).getFolderName());
                        holder.ll_Photos.setTag(R.id.TAG_PHOTO_URI,photosFragmentModelClassList.get(position).getPhotoUri());
                        holder.ll_Photos.setTag(R.id.TAG_PHOTO_URI_PATH,photosFragmentModelClassList.get(position).getUriPath());
                        holder.ll_Photos.setTag(R.id.TAG_PHOTO_TIME_STAMP,photosFragmentModelClassList.get(position).getTimeStamp());
                        selectedLayout = holder.ll_Photos;

                        actionMode = ((AppCompatActivity)context).startSupportActionMode(actionModeCallback);
                        return true;
                    }



            }
        });

    }

    @Override
    public int getItemCount() {
        return photosFragmentModelClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_photo;
        private ConstraintLayout ll_Photos;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_samplePhotosFragment);
            ll_Photos = itemView.findViewById(R.id.ll_sample_photosFragment);
        }
    }





   public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            userID = UtilStorage.utilUserID;
            MenuInflater menuInflater = new MenuInflater(context);
            menuInflater.inflate(R.menu.menu_photos,menu);
            if(!userID.equals(myUID)){
                MenuItem deleteMenuItem = menu.findItem(R.id.menu_photos_delete);
                deleteMenuItem.setVisible(false);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            String fileName = selectedLayout.getTag(R.id.TAG_PHOTO_FILE_NAME).toString();
            String folderName = selectedLayout.getTag(R.id.TAG_PHOTO_FOLDER_NAME).toString();

            int itemID = item.getItemId();
            if(itemID == R.id.menu_photos_delete){
                storageReference.child(NodeNames.IMAGES_FOLDER).child(myUID).child(NodeNames.PROFILE_PICTURES)
                        .child(fileName).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            databaseReference.child(NodeNames.IMAGES_FOLDER).child(myUID).child(NodeNames.PROFILE_PICTURES)
                                    .child(folderName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, context.getString(R.string.successfullyDeletedPhoto), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                        else{
                            Toast.makeText(context, context.getString(R.string.failedToDeletePhoto), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }





            else if(itemID==R.id.menu_photos_share){
                Toast.makeText(context, context.getString(R.string.shareBtnCLicked), Toast.LENGTH_SHORT).show();
            }


            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
                actionMode =null;
        }
    };
}
