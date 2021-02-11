package com.example.addabazi.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addabazi.R;
import com.example.addabazi.common.Extras;
import com.example.addabazi.common.NodeNames;
import com.example.addabazi.common.Util;
import com.example.addabazi.common.UtilStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Node;


public class BasicFragment extends Fragment {
    private String userID,myUID;
    private TextView tv_userName,tv_userEmail,tv_userAge,tv_userCountry;
    private DatabaseReference databaseReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        myUID = FirebaseAuth.getInstance().getUid();
        userID = UtilStorage.utilUserID;

        tv_userName= view.findViewById(R.id.tv_nameValue_basicFragment);
        tv_userEmail = view.findViewById(R.id.tv_emailValue_basicFragment);
        tv_userCountry = view.findViewById(R.id.tv_countryValue_basicFragment);
        tv_userAge = view.findViewById(R.id.tv_ageValue_basicFragment);


        databaseReference.child(NodeNames.USERS).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = "";
                String userCountry = "";
                String userAge = "";
                String userEmail = "";
                if(snapshot.exists()){


                    if(snapshot.child(NodeNames.NAME).exists()){
                        userName = snapshot.child(NodeNames.NAME).getValue().toString();
                    }


                    if(snapshot.child(NodeNames.AGE).exists()){
                        userAge = snapshot.child(NodeNames.AGE).getValue().toString();
                    }



                    if(snapshot.child(NodeNames.COUNTRY).exists()){
                        userCountry = snapshot.child(NodeNames.COUNTRY).getValue().toString();
                    }



                    if(snapshot.child(NodeNames.EMAIL).exists()){
                        userEmail = snapshot.child(NodeNames.EMAIL).getValue().toString();
                    }
                    tv_userName.setText(userName);
                    tv_userAge.setText(userAge);
                    tv_userCountry.setText(userCountry);
                    tv_userEmail.setText(userEmail);

                }
                else {
                    Toast.makeText(getContext(), getString(R.string.snapNotExists), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}