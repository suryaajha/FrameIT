package pict.s2k.frameit.subactivities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.R;
import pict.s2k.frameit.models.ChatMessage;

public class ChatActivity2 extends AppCompatActivity {
    RecyclerView chatRecycler;
    DatabaseReference chatRef;
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    private EditText chatEditText;
    private Button sendButton;
    private String firstPerson;
    private String secondPerson;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        mAuth=FirebaseAuth.getInstance();
        chatRecycler=findViewById(R.id.recycler_chat);
        sendButton=findViewById(R.id.sendChatMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setHasFixedSize(true);
        chatEditText=findViewById(R.id.inputTextAreaForChatting);
        intent=getIntent();
        firstPerson=intent.getExtras().getString("firstPerson");
        secondPerson=intent.getExtras().getString("secondPerson");
        userRef=FirebaseDatabase.getInstance().getReference()
                .child(FirebaseTreeConstants.USER)
                .child(firstPerson)
                .child("chatting")
                .child(secondPerson);
        final String[] chatID = new String[1];
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatID[0] =dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chatRef= FirebaseDatabase.getInstance().getReference()
                .child(FirebaseTreeConstants.ALL_CHATS)
                .child(chatID[0]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatRecycler.setAdapter(initAdapter());
    }
    public FirebaseRecyclerAdapter<ChatMessage,ChatViewHolder> initAdapter(){
        FirebaseRecyclerAdapter<ChatMessage,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(
                ChatMessage.class,
                R.layout.chatting_single_row,
                ChatViewHolder.class,
                chatRef
        ) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, ChatMessage model, int position) {
                Toast.makeText(ChatActivity2.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                if(model.getSender_uid().equals(mAuth.getCurrentUser().getUid())){
                    viewHolder.setSenderSenderMessage(model.getMessage(),model.getSender_profile_pic());
                }else{
                    viewHolder.setReceiverMessage(model.getMessage(),model.getReceiver_profile_pic());
                }
            }
        };
        return adapter;
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        View mView;
        SimpleDraweeView senderProfile;
        SimpleDraweeView receiverProfile;
        TextView senderText;
        TextView receiverText;
        LinearLayout senderChat;
        LinearLayout receiverChat;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            //senderProfile=itemView.findViewById(R.id.profile_image_right);
            senderText=itemView.findViewById(R.id.chatting_text_right);
            senderChat=itemView.findViewById(R.id.chatting_right);
            //receiverProfile=itemView.findViewById(R.id.profile_image_left);
            receiverText=itemView.findViewById(R.id.chatting_text_left);
            receiverChat=itemView.findViewById(R.id.chatting_left);
        }
        public void setSenderSenderMessage(String message,String profilePic){
            //Uri uri=Uri.parse(profilePic);
            receiverChat.setVisibility(View.GONE);
            senderChat.setVisibility(View.VISIBLE);
            senderText.setText(message);
            //senderProfile.setImageURI(uri);
        }
        public void setReceiverMessage(String message,String profilePic){
            //Uri uri=Uri.parse(profilePic);
            receiverChat.setVisibility(View.VISIBLE);
            senderChat.setVisibility(View.GONE);
            receiverText.setText(message);
            //receiverProfile.setImageURI(uri);
        }
    }
    void sendMessage(){
        final String text=chatEditText.getText().toString().trim();
        final DatabaseReference newMessage=FirebaseDatabase.getInstance().getReference()
                .child(FirebaseTreeConstants.ALL_CHATS)
                .child(firstPerson+secondPerson)
                .push();
        /*DatabaseReference senderProfile=FirebaseDatabase.getInstance().getReference()
                .child(FirebaseTreeConstants.USER)
                .child(firstPerson)
                .child(FirebaseTreeConstants.PROFILE_URL);
        final DatabaseReference receiverProfile=FirebaseDatabase.getInstance().getReference()
                .child(FirebaseTreeConstants.USER)
                .child(secondPerson)
                .child(FirebaseTreeConstants.PROFILE_URL);
        final String[] senderProfileUrl = new String[1];
        final String[] receiverProfileUrl = new String[1];
        senderProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderProfileUrl[0] =dataSnapshot.getValue().toString();
                receiverProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        receiverProfileUrl[0] =dataSnapshot.getValue().toString();
                        ChatMessage message=new ChatMessage(text,firstPerson,secondPerson,senderProfileUrl[0],receiverProfileUrl[0]);
                        newMessage.setValue(message);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}
