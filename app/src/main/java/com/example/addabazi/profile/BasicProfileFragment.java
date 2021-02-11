package com.example.addabazi.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.addabazi.R;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.UtilStorage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BasicProfileFragment extends Fragment {
    private static TextInputEditText etName,etEmail,etAge,etCountry;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    //private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String myUID = FirebaseAuth.getInstance().getUid();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        etName = view.findViewById(R.id.etName_profileActivity_ID);
        etEmail = view.findViewById(R.id.etEmail_profileActivity_ID);
        etAge = view.findViewById(R.id.etAge_profileActivity_ID);
        etCountry = view.findViewById(R.id.etCountry_profileActivity_ID);

        databaseReference.child(NodeNames.USERS).child(myUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String userName = "";
                //String userPhotoName = "";
                String userAge = "";
                String userEmail= "";
                String userCountry= "";
                if(snapshot.child(NodeNames.NAME).exists()){
                    userName = snapshot.child(NodeNames.NAME).getValue().toString();
                }
                //if(snapshot.child(NodeNames.PHOTO).getValue()!=""){
                   // userPhotoName = snapshot.child(NodeNames.PHOTO).getValue().toString();
                //}

                if(snapshot.child(NodeNames.AGE).exists()){
                    userAge = snapshot.child(NodeNames.AGE).getValue().toString();
                }
                if(snapshot.child(NodeNames.COUNTRY).exists()){
                    userCountry = snapshot.child(NodeNames.COUNTRY).getValue().toString();
                }
                if(snapshot.child(NodeNames.EMAIL).exists()){
                    userEmail = snapshot.child(NodeNames.EMAIL).getValue().toString();
                }

                etName.setText(userName);
                etEmail.setText(userEmail);
                etAge.setText(userAge);
                etCountry.setText(userCountry);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public static void updateDataFromSaveDataButton(Context context){
        if(etName.getText().toString().equals("")){
            etName.setError(context.getString(R.string.hint_enter_name));
        }
        else{
            UtilStorage.utilStorageUserName = "";
            UtilStorage.utilStorageUserAge = "";
            UtilStorage.utilStorageUserCountry = "";
            UtilStorage.utilStorageUserEmail = "";
            if(!etName.getText().toString().equals("")){
                UtilStorage.utilStorageUserName = etName.getText().toString();
            }
            if(!etAge.getText().toString().equals("")){
                UtilStorage.utilStorageUserAge = etAge.getText().toString();
            }
            if(!etCountry.getText().toString().equals("")){
                UtilStorage.utilStorageUserCountry = etCountry.getText().toString();
            }
            if(!etEmail.getText().toString().equals("")){
                UtilStorage.utilStorageUserEmail = etEmail.getText().toString();
            }



        }
        }


}