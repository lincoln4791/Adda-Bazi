package com.example.addabazi.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.addabazi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Util {
   public static boolean connectionAvailable(Context context){
       ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       if(connectivityManager !=null && connectivityManager.getActiveNetworkInfo()!=null){
           return connectivityManager.getActiveNetworkInfo().isAvailable();
       }
       else{
           return false;
       }
   }



   public static void updateToken( Context context, String token){

       FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

       if (currentUser!= null){
           String myUid = FirebaseAuth.getInstance().getUid();
           //Log.d("tag","my yid "+myUid);
           DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
           DatabaseReference databaseReference = mRootRef.child(NodeNames.TOKENS_FOLDER).child(myUid);
           HashMap hashMap = new HashMap();
           hashMap.put(NodeNames.DEVICE_TOKEN,token);

           databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                   }
                   else{
                       Toast.makeText(context, "Failed to update token"+task.getException(), Toast.LENGTH_SHORT).show();
                   }
               }
           });
       }
   }






   public static void sendNotification (Context context, String notificationTitle, String notificationMessage, String userID){
       //Log.d("tag","Title : "+notificationTitle);
       //Log.d("tag","Message : "+notificationMessage);
       //Log.d("tag","UserID : "+userID);
       DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
       DatabaseReference dbrTokens = mRootRef.child(NodeNames.TOKENS_FOLDER).child(userID);
       dbrTokens.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.child(NodeNames.DEVICE_TOKEN).getValue()!= null){
                  String deviceToken = snapshot.child(NodeNames.DEVICE_TOKEN).getValue().toString();
                   //Log.d("tag","device token : "+deviceToken);

                   JSONObject notification = new JSONObject();
                   JSONObject notificationData = new JSONObject();
                   String fcmApiUrl = "https://fcm.googleapis.com/fcm/send";
                   String contentType = "application/json";

                   try {
                       notificationData.put(Constants.NOTIFICATION_TITLE,notificationTitle);
                       notificationData.put(Constants.NOTIFICATION_MESSAGE,notificationMessage);

                       notification.put(Constants.NOTIFICATION_TO,deviceToken);
                       notification.put(Constants.NOTIFICATION_DATA,notificationData);

                       Response.Listener<JSONObject> successListener = new Response.Listener() {
                           @Override
                           public void onResponse(Object response) {
                               //Toast.makeText(context, "Notification Send Successfully", Toast.LENGTH_SHORT).show();
                               //Log.d("tag","Success");
                           }
                       };

                       Response.ErrorListener errorListener = new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError error) {
                               Toast.makeText(context,"f "+error.getMessage(), Toast.LENGTH_LONG).show();
                               //Log.d("tag","Failed"+error.getMessage());
                               //Log.d("tag","Failed"+error.networkResponse);
                           }
                       };

                       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(fcmApiUrl,notification,successListener,errorListener){
                           @Override
                           public Map<String, String> getHeaders() throws AuthFailureError {

                               Map params = new HashMap();
                               params.put("authorization","key="+Constants.FIREBASE_KEY);
                               params.put("Sender","id="+Constants.SENDER_ID);
                               params.put("Content-Type",contentType);
                               //return super.getHeaders();
                               return params;
                           }
                       };

                       RequestQueue requestQueue = Volley.newRequestQueue(context);
                       requestQueue.add(jsonObjectRequest);

                   } catch (JSONException e) {
                       //Toast.makeText(context,"Failed0", Toast.LENGTH_SHORT).show();
                   }


               }
               else{
                   //Toast.makeText(context,"Failed1", Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(context,"failed2", Toast.LENGTH_SHORT).show();
           }
       });

   }



   public static void updateChatDetails(Context context, String userID, String notificationMessage){
       String myUID = FirebaseAuth.getInstance().getUid();
       DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
       DatabaseReference dbrChats = mRootRef.child(NodeNames.CHAT_FOLDER);


       dbrChats.child(userID).child(myUID).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               String unreadCount = snapshot.child(NodeNames.UNREAD_COUNT).getValue()!=null?
                       snapshot.child(NodeNames.UNREAD_COUNT).getValue().toString() :"0";

               Map hashMap = new HashMap();
               hashMap.put(NodeNames.TIME_STAMP, ServerValue.TIMESTAMP);
               hashMap.put(NodeNames.UNREAD_COUNT, Integer.valueOf(unreadCount)+1);
               hashMap.put(NodeNames.LAST_MESSAGE,notificationMessage);
               hashMap.put(NodeNames.LAST_MESSAGE_TIME,ServerValue.TIMESTAMP);
               hashMap.put(NodeNames.USER_ID,myUID);

               Map chatUserMap = new HashMap();
               chatUserMap.put(NodeNames.CHAT_FOLDER+"/"+userID+"/"+myUID,hashMap);
               mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                   @Override
                   public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                       if(error!=null){
                           Toast.makeText(context, context.getString(R.string.somethingWentWrong,error.getMessage()), Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(context, context.getString(R.string.somethingWentWrong,error.getMessage()), Toast.LENGTH_SHORT).show();
           }
       });

   }




   public static String getTimeAgo(long time){
       final int SECOND_MILS = 1000;
       final int MINUTE_MILS = SECOND_MILS * 60;
       final int HOUR_MILS =  MINUTE_MILS * 60;
       final int DAY_MILS = HOUR_MILS * 24;

       //time *= 1000;
       long now = System.currentTimeMillis();
       final long timeDifferenceInMills = now-time;


       if(timeDifferenceInMills >= 0 && timeDifferenceInMills <=59 * SECOND_MILS){
            return " few seconds ago ";
       }

       else if(timeDifferenceInMills>= 60 * SECOND_MILS && timeDifferenceInMills<= 119*SECOND_MILS ){
           return "a minute ago";
       }
       else if(timeDifferenceInMills >=60*SECOND_MILS && timeDifferenceInMills <=59*MINUTE_MILS){
            return timeDifferenceInMills/MINUTE_MILS+ " minutes ago";
       }
       else if(timeDifferenceInMills >=60*MINUTE_MILS && timeDifferenceInMills < 2*HOUR_MILS){
           return "an hour ago";
       }
       else if(timeDifferenceInMills >= 2*HOUR_MILS && timeDifferenceInMills < 24*HOUR_MILS){
           return timeDifferenceInMills/HOUR_MILS +" hours ago" ;
       }
       else if(timeDifferenceInMills >= 24*HOUR_MILS && timeDifferenceInMills < 48*HOUR_MILS){
           return "Yesterday" ;
       }
       else if(timeDifferenceInMills > 48*HOUR_MILS){
           return timeDifferenceInMills/DAY_MILS+" days "+(timeDifferenceInMills % DAY_MILS)/HOUR_MILS+"hours ago";
       }
       else {
           return  "now";
       }

   }




}
