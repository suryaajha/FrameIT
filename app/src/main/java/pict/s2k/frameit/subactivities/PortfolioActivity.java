package pict.s2k.frameit.subactivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.R;
import pict.s2k.frameit.fragments.PostsGridView;
import pict.s2k.frameit.fragments.PostsLinearView;
import pict.s2k.frameit.models.User;

public class PortfolioActivity extends AppCompatActivity {
    private static final String TAG = "Portfolio";
    ViewPager viewPager;
    TabLayout tabLayout;

    //UI Components
    SimpleDraweeView profileImage;
    TextView userName;
    TextView postsCount;
    TextView followersCount;
    TextView ratingView;
    Button followButton;
    Button messageButton;

    //Firebase classes
    FirebaseDatabase mDatabase;
    DatabaseReference rootRef;
    DatabaseReference userRef;
    DatabaseReference followersRef;
    String uidOfPortfolioActivityUser;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private String firstPerson ;
    private String secondPerson ;
    private boolean followingThisUser;

    private int[] tabIcons = {
            R.drawable.ic_grid_view,
            R.drawable.ic_list_view,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        viewPager=(ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);
        mAuth=FirebaseAuth.getInstance();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        currentUser = FirebaseAuth.getInstance().getCurrentUser() ;
        setupTabIcons();
        setupUI();
        loadWithFirebaseData();


        firstPerson = currentUser.getUid() ;
        final Button followBtn=findViewById(R.id.btn_follow);
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference dbRefToFollowers = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child(FirebaseTreeConstants.FOLLOWERS)
                        .child(uidOfPortfolioActivityUser)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if(!followingThisUser) {
                    dbRefToFollowers.setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(PortfolioActivity.this);
                    builder.setTitle("Unfollow");
                    builder.setMessage("Do you want to unfollow this user ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dbRefToFollowers.removeValue();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
        });

        findViewById(R.id.btn_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(PortfolioActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

                DatabaseReference queryOne = FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseTreeConstants.USER)
                        .child(firstPerson)
                        .child(FirebaseTreeConstants.CHATTING) ;
                final DatabaseReference queryTwo = FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseTreeConstants.USER)
                        .child(secondPerson)
                        .child(FirebaseTreeConstants.CHATTING) ;


                queryOne.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(secondPerson)){
                            DatabaseReference dbRefToFetchChatKey = FirebaseDatabase.getInstance().getReference()
                                    .child(FirebaseTreeConstants.USER)
                                    .child(firstPerson)
                                    .child(FirebaseTreeConstants.CHATTING)
                                    .child(secondPerson);
                            dbRefToFetchChatKey.setValue(firstPerson+secondPerson) ;
                            queryTwo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(firstPerson)){
                                        DatabaseReference dbRefToFetchChatKey = FirebaseDatabase.getInstance().getReference()
                                                .child(FirebaseTreeConstants.USER)
                                                .child(secondPerson)
                                                .child(FirebaseTreeConstants.CHATTING)
                                                .child(firstPerson);
                                        dbRefToFetchChatKey.setValue(firstPerson+secondPerson) ;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                ArrayList<String> uids = new ArrayList<String>() ;
                uids.add(firstPerson);
                uids.add(secondPerson);

                Intent intentForChatActivity = new Intent(PortfolioActivity.this,ChatActivity.class) ;
                intentForChatActivity.putStringArrayListExtra(ChatActivity.FIRST_SECOND_PERSON_UID,uids) ;
                startActivity(intentForChatActivity);

            }
        });
        /*findViewById(R.id.btn_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference queryOne = FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseTreeConstants.USER)
                        .child(firstPerson)
                        .child(FirebaseTreeConstants.CHATTING) ;
                queryOne.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(secondPerson)){
                            AlertDialog.Builder builder=new AlertDialog.Builder(PortfolioActivity.this);
                            builder.setTitle("New Chat");
                            builder.setMessage("It seems like you're chatting first time, would you like to start ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference senderChatRef = FirebaseDatabase.getInstance().getReference()
                                            .child(FirebaseTreeConstants.USER)
                                            .child(firstPerson)
                                            .child(FirebaseTreeConstants.CHATTING)
                                            .child(secondPerson);
                                    final DatabaseReference receiverChatRef = FirebaseDatabase.getInstance().getReference()
                                            .child(FirebaseTreeConstants.USER)
                                            .child(secondPerson)
                                            .child(FirebaseTreeConstants.CHATTING)
                                            .child(firstPerson);
                                    senderChatRef.setValue(firstPerson+senderChatRef).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            receiverChatRef.setValue(firstPerson+secondPerson).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent chatIntent=new Intent(PortfolioActivity.this,ChatActivity2.class);
                                                    chatIntent.putExtra("firstPerson",firstPerson);
                                                    chatIntent.putExtra("secondPerson",secondPerson);
                                                    startActivity(chatIntent);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }else{
                            Intent chatIntent=new Intent(PortfolioActivity.this,ChatActivity2.class);
                            chatIntent.putExtra("firstPerson",firstPerson);
                            chatIntent.putExtra("secondPerson",secondPerson);
                            startActivity(chatIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        });*/
        SetupFont.setupDefaultFont(this);
    }

    private void loadWithFirebaseData() {
        mDatabase=FirebaseDatabase.getInstance();
        Intent intent=getIntent();
        uidOfPortfolioActivityUser =intent.getExtras().getString(FirebaseTreeConstants.UID);
        secondPerson = uidOfPortfolioActivityUser;
        checkUser();
        rootRef=mDatabase.getReference();
        rootRef.keepSynced(true);
        userRef=rootRef.child(FirebaseTreeConstants.USER).child(uidOfPortfolioActivityUser);
        followersRef=rootRef.child(FirebaseTreeConstants.FOLLOWERS);
        setFollowButton();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userName.setText(user.getDisplay_name());
                Uri uri=Uri.parse(user.getProfile_pic_url());
                profileImage.setImageURI(uri);
                /*Glide.with(getActivity()).load(user.getProfile_pic_url())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profileImage);*/
                followersCount.setText(String.valueOf(user.getFollowers()));
                postsCount.setText(String.valueOf(user.getPosts()));
                ratingView.setText(String.valueOf(user.getRating()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void setFollowButton() {
        Log.d(TAG, "setFollowButton: ");
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(secondPerson)){
                    Log.d(TAG, "setFollowButton: User has some followers");
                    if(dataSnapshot.child(secondPerson).hasChild(currentUser.getUid())) {
                        followButton.setBackground(ContextCompat.getDrawable(PortfolioActivity.this, R.drawable.gradient_button));
                        followButton.setTextColor(ContextCompat.getColor(PortfolioActivity.this, R.color.colorPrimary));
                        followButton.setText("Following");
                        Log.d(TAG, "Including current user");
                        followingThisUser=true;
                    }
                    else {
                        followButton.setBackground(ContextCompat.getDrawable(PortfolioActivity.this, R.drawable.rounded_button));
                        followButton.setTextColor(ContextCompat.getColor(PortfolioActivity.this, android.R.color.black));
                        followButton.setText("Follow");
                        followingThisUser=false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkUser() {
        if(secondPerson.equals(mAuth.getCurrentUser().getUid())) {
            findViewById(R.id.btn_follow).setVisibility(View.GONE);
            findViewById(R.id.btn_message).setVisibility(View.GONE);
        }
    }

    private void setupUI() {
        profileImage=findViewById(R.id.profile_image);
        userName=findViewById(R.id.txt_name);
        postsCount=findViewById(R.id.txt_posts);
        followersCount=findViewById(R.id.txt_followers);
        ratingView=findViewById(R.id.txt_rating);
        followButton=findViewById(R.id.btn_follow);
    }
    private void setupTabIcons() {
        View view=LayoutInflater.from(this).inflate(R.layout.custom_tab_grid,null);
        tabLayout.getTabAt(0).setCustomView(view);
        view=LayoutInflater.from(this).inflate(R.layout.custom_tab_list,null);
        tabLayout.getTabAt(1).setCustomView(view);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new TimeCheck(), "Time Comparision");
        adapter.addFragment(new PostsGridView(), "Grid View");
        adapter.addFragment(new PostsLinearView(), "List View");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
