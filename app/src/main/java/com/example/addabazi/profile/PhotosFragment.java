package com.example.addabazi.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.addabazi.R;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.UtilStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {
    private RecyclerView rv;
    private List<PhotosFragmentModelClass> photosProfileFragmentList;
    private PhotosFragmentAdapter photosFragmentAdapter;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String userID,myUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = UtilStorage.utilUserID;
        myUID = FirebaseAuth.getInstance().getUid();


        photosProfileFragmentList = new ArrayList<>();
        rv = view.findViewById(R.id.rv_photosFragment);
        //rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setLayoutManager(new GridLayoutManager(getContext(),3));
        photosFragmentAdapter = new PhotosFragmentAdapter(getContext(),photosProfileFragmentList);
        rv.setAdapter(photosFragmentAdapter);

        loadData();
    }

    private void loadData() {
        databaseReference.child(NodeNames.IMAGES_FOLDER).child(userID).child(NodeNames.PROFILE_PICTURES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                                String folderName = "";
                                String fileName= "";
                                String uriPath = "";
                                String photoUri = "";
                                String timeStamp = "";

                                if(dataSnapshot.child(NodeNames.URI).exists()){
                                     photoUri = !dataSnapshot.child(NodeNames.URI).equals("")?
                                            dataSnapshot.child(NodeNames.URI).getValue().toString():"";
                                }

                                if(dataSnapshot.child(NodeNames.FILE_NAME).exists()){
                                     fileName = !dataSnapshot.child(NodeNames.FILE_NAME).equals("")?
                                            dataSnapshot.child(NodeNames.FILE_NAME).getValue().toString():"";
                                }

                                if(dataSnapshot.child(NodeNames.FOLDER_NAME).exists()){
                                     folderName = !dataSnapshot.child(NodeNames.FOLDER_NAME).equals("")?
                                            dataSnapshot.child(NodeNames.FOLDER_NAME).getValue().toString():"";
                                }
                                if(dataSnapshot.child(NodeNames.TIME_STAMP).exists()){
                                     timeStamp = !dataSnapshot.child(NodeNames.TIME_STAMP).equals("")?
                                            dataSnapshot.child(NodeNames.TIME_STAMP).getValue().toString():"";
                                }
                                if(dataSnapshot.child(NodeNames.URI_PATH).exists()){
                                     uriPath = !dataSnapshot.child(NodeNames.URI_PATH).equals("")?
                                            dataSnapshot.child(NodeNames.URI_PATH).getValue().toString():"";
                                }

                                PhotosFragmentModelClass object = new PhotosFragmentModelClass(folderName,fileName,photoUri,timeStamp,uriPath);
                                photosProfileFragmentList.add(object);
                                photosFragmentAdapter.notifyDataSetChanged();

                            }
                        }
                        else{
                            //Toast.makeText(getContext(), getString(R.string.snapNotExists), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}