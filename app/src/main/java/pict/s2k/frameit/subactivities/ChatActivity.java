package pict.s2k.frameit.subactivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.MainActivity;
import pict.s2k.frameit.R;
import pict.s2k.frameit.adapter.ChattingAdapter;
import pict.s2k.frameit.adapter.ChattingAdapterListView;
import pict.s2k.frameit.fragments.ExploreFragment;
import pict.s2k.frameit.models.ArtPost;
import pict.s2k.frameit.models.ChatMessage;
import pict.s2k.frameit.models.MessageModel;
import pict.s2k.frameit.models.User;
import pict.s2k.frameit.utils.TimeDifference;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    private ChattingAdapter chattingAdapter;
    private List<MessageModel> messageList = new ArrayList<>();
    private ChattingAdapterListView adapterListView;

    public static String FIRST_SECOND_PERSON_UID = "first_second_person_uid";

    private EditText chatEditText;

    private String CHAT_KEY = "";
    boolean chatKeyPresent = false;

    private DatabaseReference postsRef;
    private ListView listView ;
    private DatabaseReference userRef;
    private DatabaseReference mobileRef;
    private DatabaseReference nameRef;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        postsRef = FirebaseDatabase.getInstance().getReference().child(FirebaseTreeConstants.POSTS);


        //For List View
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {

            final ArrayList<String> uidsInContext = receivedIntent.getStringArrayListExtra(FIRST_SECOND_PERSON_UID);
            String uid = uidsInContext.get(1);
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseTreeConstants.USER)
                    .child(uid);
            nameRef=userRef.child(FirebaseTreeConstants.DISPLAY_NAME);
            setUserName();
            mobileRef=userRef.child(FirebaseTreeConstants.MOBILE_NO);
            prepareMessages(uidsInContext.get(0), uidsInContext.get(1));

            ChattingAdapterListView.LOGGED_IN_USER_UID = uidsInContext.get(0);

            //handle The Chatting control

            chatEditText = ((EditText) findViewById(R.id.inputTextAreaForChatting));
            ((ImageButton) findViewById(R.id.sendChatMessageButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if ((!TextUtils.isEmpty(chatEditText.getText().toString())) && (!TextUtils.isEmpty(CHAT_KEY))) {
                        //Todo: Add the sender profile pic and reciver profile pic

                        String text = chatEditText.getText().toString().trim();

                        MessageModel messageModel = new MessageModel(text, "You", System.currentTimeMillis(),
                                uidsInContext.get(0), uidsInContext.get(1));
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseTreeConstants.ALL_CHATTING)
                                .child(CHAT_KEY);
                        databaseReference
                                .push()
                                .setValue(messageModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        chatEditText.setText("");
                                    }
                                });
                    }
                }
            });

            //End
        }

        listView = findViewById(R.id.listViewMy);
        adapterListView = new ChattingAdapterListView(this, R.layout.chatting_single_row, messageList);
        listView.setAdapter(adapterListView);

        // End


        /*ChattingAdapter.LOGGED_IN_USER_UID = "M7T4uZaaLWRc2YUqL7z3W1kbPn62" ;

        Intent receivedIntent = getIntent() ;
        if(receivedIntent != null){
            ArrayList<String> uidsInContext = receivedIntent.getStringArrayListExtra(FIRST_SECOND_PERSON_UID) ;
            prepareMessages(uidsInContext.get(0),uidsInContext.get(1));
            //prepareMessages();
        }

        recyclerView = (RecyclerView) findViewById(R.id.chatting_recycler_view);

        chattingAdapter = new ChattingAdapter(messageList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chattingAdapter);*/
    }

    private void setUserName() {
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTitle(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.call) {
            mobileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String callNumber = "+91"+dataSnapshot.getValue().toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + callNumber));
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    getApplicationContext().startActivity(callIntent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
    private void prepareMessages(final String firstPersonUid, String secondPersonUid) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRefToFetchChatKey = firebaseDatabase.getReference()
                .child(FirebaseTreeConstants.USER)
                .child(firstPersonUid)
                .child(FirebaseTreeConstants.CHATTING)
                .child(secondPersonUid);

        dbRefToFetchChatKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatKey = dataSnapshot.getValue(String.class);

                CHAT_KEY = chatKey;
                chatKeyPresent = true;

                DatabaseReference dbRefToChattings = firebaseDatabase.getReference()
                        .child(FirebaseTreeConstants.ALL_CHATTING)
                        .child(chatKey);

                dbRefToChattings.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MessageModel message = dataSnapshot.getValue(MessageModel.class);
                        adapterListView.add(message);
                        listView.setSelection(adapterListView.getCount() - 1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*private void prepareMessages() {
        MessageModel message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);
        message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                System.currentTimeMillis(),
                "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
        );
        messageList.add(message);

    }*/

    /*private FirebaseRecyclerAdapter<MessageModel,> initFirebaseRecyclerAdapter() {
        FirebaseRecyclerAdapter<MessageModel, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MessageModel, PostViewHolder>(
                MessageModel.class,
                R.layout.chatting_single_row,
                PostViewHolder.class,
                postsRef
        ) ;

        @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, MessageModel model, int position) {
                MessageModel messageModel = messageModelList.get(position);
                if (messageModel.getSender_uid() == LOGGED_IN_USER_UID) {
                    Toast.makeText(ChatActivity.this, "Comm", Toast.LENGTH_SHORT).show();
                    viewHolder.parentView.findViewById(R.id.chatting_right).setVisibility(View.VISIBLE);
                    viewHolder.parentView.findViewById(R.id.chatting_left).setVisibility(View.GONE);
                    Glide.with(ChatActivity.this).load(messageModel.getSender_profile_uri()).into(viewHolder.dpOfChatRight);
                    viewHolder.chatStringRight.setText(messageModel.getMessage());

                } else {
                    viewHolder.parentView.findViewById(R.id.chatting_left).setVisibility(View.VISIBLE);
                    viewHolder.parentView.findViewById(R.id.chatting_right).setVisibility(View.GONE);
                    Glide.with(ChatActivity.this).load(messageModel.getReceiver_profile_uri()).into(viewHolder.dpOfChatLeft);
                    viewHolder.chatStringLeft.setText(messageModel.getMessage());
                }
            }
    }
    class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView chatStringLeft;
        public TextView chatStringRight;
        public ImageView dpOfChatLeft;
        public ImageView dpOfChatRight;
        public View parentView;

        public MessageViewHolder(View view) {
            super(view);
            parentView = view;
            chatStringLeft = ((TextView) view.findViewById(R.id.chatting_left).findViewById(R.id.chatting_text_left));
            chatStringRight = ((TextView) view.findViewById(R.id.chatting_right).findViewById(R.id.chatting_text_right));

            dpOfChatLeft = ((ImageView) view.findViewById(R.id.chatting_left).findViewById(R.id.profile_image_left));
            dpOfChatRight = ((ImageView) view.findViewById(R.id.chatting_right).findViewById(R.id.profile_image_right));

        }
    }*/
}

    /*private ArrayList<MessageModel> allMessages ;
    private  String otherPartyUID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        allMessages = new ArrayList<>() ;


        Intent receivedIntent = getIntent() ;

        if(receivedIntent != null ) {

            otherPartyUID = receivedIntent.getStringArrayListExtra("otherPartyUri").get(0) ;

            ListView listView = ((ListView) findViewById(R.id.chattig_list_view));
            ChattingAdapter chattingAdapter = new ChattingAdapter(allMessages, "SXqKEcE9tsNK0yF7OfRVA4sGDG52" , this);
            listView.setAdapter(chattingAdapter);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseTreeConstants.USER)
                    .child("SXqKEcE9tsNK0yF7OfRVA4sGDG52")
                    .child(FirebaseTreeConstants.CHATTING)
                    .child(otherPartyUID);

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    allMessages.add(messageModel);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }) ;

        }
    }*/
