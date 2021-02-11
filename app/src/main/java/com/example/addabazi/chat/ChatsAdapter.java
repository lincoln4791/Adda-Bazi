package com.example.addabazi.chat;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addabazi.R;
import com.example.addabazi.common.Constants;
import com.example.addabazi.selectFriend.SelectFriendActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyChatsHolder> {
    private Context context;
    private List<ChatsModel> chatsModelList;
    private FirebaseAuth firebaseAuth;
    private ConstraintLayout selectedLayout;

    //chat options, reply, download, share, forward etc...
    private ActionMode actionMode;

    public ChatsAdapter(Context context, List<ChatsModel> chatsModelList) {
        this.context = context;
        this.chatsModelList = chatsModelList;
    }

    @NonNull
    @Override
    public MyChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.message_layout_sample,parent,false);
        return new MyChatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyChatsHolder holder, int position) {

        firebaseAuth = FirebaseAuth.getInstance();
        String fromUserID = chatsModelList.get(position).getMessage_from();
        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        SimpleDateFormat dsf = new SimpleDateFormat("dd-MM-yy HH-mm");
        String dateTime = dsf.format(new Date(chatsModelList.get(position).getMessage_time()));
        String [] splitString = dateTime.split(" ");
        String messageTime = splitString[1].replace("-",":");


//Implementation of Chat Conversation View
        if (fromUserID.equals(currentUserID)){
            if(chatsModelList.get(position).getMessage_type().equals(Constants.MESSAGE_TYPE_TEXT)){
                holder.ll_sentMessage.setVisibility(View.VISIBLE);
                holder.ll_sentImageMessage.setVisibility(View.GONE);
            }
            else{
                holder.ll_sentMessage.setVisibility(View.GONE);
                holder.ll_sentImageMessage.setVisibility(View.VISIBLE);
            }

            holder.ll_receivedMessage.setVisibility(View.GONE);
            holder.ll_receivedImageMessage.setVisibility(View.GONE);
            holder.tv_sentMessageText.setText(chatsModelList.get(position).getMessage());
            holder.tv_sentMessageTime.setText(messageTime);
            holder.tv_sentImageMessageTime.setText(messageTime);
            Glide.with(context).load(chatsModelList.get(position).getMessage()).placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture).into(holder.iv_sentImageMessage);
        }

        else {

            if(chatsModelList.get(position).getMessage_type().equals(Constants.MESSAGE_TYPE_TEXT)){
                holder.ll_receivedMessage.setVisibility(View.VISIBLE);
                holder.ll_receivedImageMessage.setVisibility(View.GONE);
            }
            else{
                holder.ll_receivedMessage.setVisibility(View.GONE);
                holder.ll_receivedImageMessage.setVisibility(View.VISIBLE);
            }
            holder.ll_sentMessage.setVisibility(View.GONE);
            holder.ll_sentImageMessage.setVisibility(View.GONE);
            holder.tv_receivedMessageText.setText(chatsModelList.get(position).getMessage());
            holder.tv_receivedMessageTime.setText(messageTime);
            holder.tv_receivedImageMessageTime.setText(messageTime);
            Glide.with(context).load(chatsModelList.get(position).getMessage()).placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture).into(holder.iv_receivedImageMessage);
        }


            holder.cl_messageSampleLayout.setTag(R.id.TAG_MESSAGE,chatsModelList.get(position).getMessage());
            holder.cl_messageSampleLayout.setTag(R.id.TAG_MESSAGE_TYPE,chatsModelList.get(position).getMessage_type());
            holder.cl_messageSampleLayout.setTag(R.id.TAG_MESSAGE_ID,chatsModelList.get(position).getMessage_id());
            //selectedLayout = holder.cl_messageSampleLayout;




            holder.cl_messageSampleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageType = v.getTag(R.id.TAG_MESSAGE_TYPE).toString();
                    Uri uri = Uri.parse(v.getTag(R.id.TAG_MESSAGE).toString());
                    if(messageType.equals(Constants.MESSAGE_TYPE_IMAGE)){
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setDataAndType(uri,"image/jpg");
                        context.startActivity(intent);
                    }
                    else if(messageType.equals(Constants.MESSAGE_TYPE_VIDEO)){
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setDataAndType(uri,"video/mp4");
                        context.startActivity(intent);
                    }
                }
            });







            holder.cl_messageSampleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(actionMode != null){
                        return false;
                    }
                    else{
                        holder.cl_messageSampleLayout.setTag(R.id.TAG_MESSAGE,chatsModelList.get(position).getMessage());
                        holder.cl_messageSampleLayout.setTag(R.id.TAG_MESSAGE_TYPE,chatsModelList.get(position).getMessage_type());
                        holder.cl_messageSampleLayout.setTag(R.id.TAG_MESSAGE_ID,chatsModelList.get(position).getMessage_id());

                        //Log.d("tag","TagMessage ID : "+holder.cl_messageSampleLayout.getTag(R.id.TAG_MESSAGE_ID).toString());
                        //Log.d("tag","TagMessage TYPE : "+holder.cl_messageSampleLayout.getTag(R.id.TAG_MESSAGE_TYPE).toString());
                        selectedLayout = holder.cl_messageSampleLayout;
                        actionMode = ((AppCompatActivity)context).startSupportActionMode(actionModeCallback);

                        holder.cl_messageSampleLayout.setBackgroundColor(context.getResources().getColor(R.color.aqua));
                        return true;
                    }



                }
            });




    }

    @Override
    public int getItemCount() {
        return chatsModelList.size();
    }

    public class MyChatsHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_sentMessage,ll_receivedMessage,ll_sentImageMessage,ll_receivedImageMessage;
        private TextView tv_sentMessageText,tv_sentMessageTime,tv_receivedMessageText, tv_receivedMessageTime,
                            tv_sentImageMessageTime,tv_receivedImageMessageTime;
        private ImageView iv_sentImageMessage,iv_receivedImageMessage;
        private ConstraintLayout cl_messageSampleLayout;



        public MyChatsHolder(@NonNull View itemView) {
            super(itemView);
            ll_sentMessage = itemView.findViewById(R.id.ll_SentMessagec_messageSampleLayout_ID);
            ll_receivedMessage = itemView.findViewById(R.id.ll_receivedMessage_messageSampleLayout_ID);
            ll_sentImageMessage = itemView.findViewById(R.id.ll_SentImageMessage_messageSampleLayout_ID);
            ll_receivedImageMessage = itemView.findViewById(R.id.ll_receivedImageMessage_messageSampleLayout_ID);
            tv_sentMessageText = itemView.findViewById(R.id.tv_sentMessage_messageSAmpleLayout_ID);
            iv_sentImageMessage = itemView.findViewById(R.id.iv_SentImageMessage_messageSampleLayout_ID);
            tv_sentMessageTime= itemView.findViewById(R.id.tv_sentMessageTime_sentMessageSampleLayout_ID);
            tv_sentImageMessageTime = itemView.findViewById(R.id.tv_SentImageMessageTime_messageSampleLayout_ID);
            tv_receivedMessageText = itemView.findViewById(R.id.tv_receivedMessage_messageSAmpleLayout_ID);
            iv_receivedImageMessage = itemView.findViewById(R.id.iv_receivedImageMessage_messageSampleLayout_ID);
            tv_receivedMessageTime = itemView.findViewById(R.id.tv_receivedMessageTime_sentMessageSampleLayout_ID);
            tv_receivedImageMessageTime = itemView.findViewById(R.id.tv_receivedImageMessageTime_messageSampleLayout_ID);
            cl_messageSampleLayout = itemView.findViewById(R.id.cl_messageSampleLayout_ID);
        }
    }




    public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = new MenuInflater(context);
            menuInflater.inflate(R.menu.menu_chat_options,menu);
            MenuItem mnuItemsDownload = menu.findItem(R.id.mnu_chatOptions_download);
            String selectableMessageType = selectedLayout.getTag(R.id.TAG_MESSAGE_TYPE).toString();
            if(selectableMessageType.equals(Constants.MESSAGE_TYPE_TEXT)){

                mnuItemsDownload.setVisible(false);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemID = item.getItemId();
            String selectedMessageID = selectedLayout.getTag(R.id.TAG_MESSAGE_ID).toString();
            String selectedMessageType = selectedLayout.getTag(R.id.TAG_MESSAGE_TYPE).toString();
            String selectedMessage = selectedLayout.getTag(R.id.TAG_MESSAGE).toString();
            Log.d("tag","tagedd"+selectedMessageType);


            switch (itemID){

                case R.id.mnuChatOptions_dlt:
                    //Toast.makeText(context, "Delete Button Pressed", Toast.LENGTH_SHORT).show();
                    if(context instanceof ChatsActivity){
                        ((ChatsActivity)context).deleteMessage(selectedMessageType,selectedMessageID);
                    }

                    actionMode.finish();
                    break;

                case R.id.mnu_chatOptions_forward:
                    if(context instanceof ChatsActivity){
                        ((ChatsActivity)context).forwardMessage(selectedMessage,selectedMessageID,selectedMessageType);
                        //Toast.makeText(context, "Forward Button Pressed", Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        break;

                    }
                case R.id.mnu_chatOptions_share:
                    //Toast.makeText(context, "Share Button Pressed", Toast.LENGTH_SHORT).show();
                    if(selectedMessageType.equals(Constants.MESSAGE_TYPE_TEXT)){
                        Intent intentShareText = new Intent();
                        intentShareText.putExtra(Intent.EXTRA_TEXT,selectedMessage);
                        intentShareText.setAction(Intent.ACTION_SEND);
                        intentShareText.setType("text/plain");
                        context.startActivity(intentShareText);
                    }
                    else{
                        ((ChatsActivity)context).downloadFile(selectedMessageType,selectedMessageID,true);
                    }
                    actionMode.finish();
                    break;


                case R.id.mnu_chatOptions_download:

                    if(context instanceof ChatsActivity){
                        ((ChatsActivity)context).downloadFile(selectedMessageType,selectedMessageID,false);
                        Toast.makeText(context, "download Button Pressed", Toast.LENGTH_SHORT).show();
                    }
                    actionMode.finish();
                    break;

                default: return false;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode= null;
            selectedLayout.setBackgroundColor(context.getResources().getColor(R.color.chatBackground));

        }
    };


}
