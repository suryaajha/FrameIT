package pict.s2k.frameit.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.PaymentActivity;
import pict.s2k.frameit.R;
import pict.s2k.frameit.subactivities.PortfolioActivity;
import pict.s2k.frameit.utils.TimeDifference;
import pict.s2k.frameit.models.ArtPost;
import pict.s2k.frameit.models.User;
import pict.s2k.frameit.subactivities.PostFullScreen;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    DatabaseReference postsRef;
    DatabaseReference usersRef;
    DatabaseReference likesRef;
    RecyclerView mPostlist;
    private boolean mProcessLike = false;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_explore, container, false);
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        rootRef=mDatabase.getReference();
        postsRef=rootRef.child(FirebaseTreeConstants.POSTS);
        likesRef=mDatabase.getReference().child(FirebaseTreeConstants.LIKES);
        mPostlist=v.findViewById(R.id.rv_art_list);
        mPostlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPostlist.setLayoutManager(linearLayoutManager);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPostlist.setAdapter(initFirebaseRecyclerAdapter());
        usersRef=mDatabase.getReference().child(FirebaseTreeConstants.USER);
    }

    private FirebaseRecyclerAdapter<ArtPost,PostViewHolder> initFirebaseRecyclerAdapter() {
        FirebaseRecyclerAdapter<ArtPost,PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ArtPost, PostViewHolder>(
                ArtPost.class,
                R.layout.item_post_row,
                PostViewHolder.class,
                postsRef
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final ArtPost model, int position) {
                final String post_key=getRef(position).getKey();
                final FirebaseUser user=mAuth.getCurrentUser();
                DatabaseReference postUser=usersRef.child(model.getUid());
                viewHolder.setUserDetails(getActivity(),postUser);
                viewHolder.setPostImage(getActivity(),model.getPost_image());
                viewHolder.setPostDetails(model.getDescription(),
                                        String.valueOf(model.getSent_time()),
                                        String.valueOf(model.getPrice()));
                viewHolder.setLikeButton(post_key,likesRef,user);
                viewHolder.setLikeCount(post_key,likesRef);
                viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;
                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(mProcessLike){
                                    if(dataSnapshot.child(post_key).hasChild(user.getUid())){
                                        likesRef.child(post_key).child(user.getUid()).removeValue();
                                        mProcessLike=false;
                                    }else{
                                        likesRef.child(post_key).child(user.getUid()).setValue(user.getDisplayName());
                                        mProcessLike=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder.postImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent postFullIntent=new Intent(getActivity(), PostFullScreen.class);
                        postFullIntent.putExtra("post_key",post_key);
                        startActivity(postFullIntent);
                    }
                });
                viewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent portfolioIntent=new Intent(getActivity(), PortfolioActivity.class);
                        portfolioIntent.putExtra(FirebaseTreeConstants.UID,model.getUid());
                        startActivity(portfolioIntent);
                    }
                });
                viewHolder.imgBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),PaymentActivity.class) ;

                        intent.putExtra("price",viewHolder.priceView.getText().toString()) ;
                        startActivity(intent);
                    }
                });
            }
        };
        return firebaseRecyclerAdapter;
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        SimpleDraweeView profilePic;
        SimpleDraweeView postImageView;
        ImageButton likeButton;
        TextView userName;
        TextView postDescription;
        TextView likesCount;
        TextView timeDifference;
        TextView priceView;
        ImageButton imgBuy;

        public PostViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            profilePic =itemView.findViewById(R.id.profile_image);
            postImageView =itemView.findViewById(R.id.post_image);
            likeButton =itemView.findViewById(R.id.likeBtn);
            userName=itemView.findViewById(R.id.post_user_name);
            postDescription =itemView.findViewById(R.id.post_description);
            likesCount =itemView.findViewById(R.id.like_count);
            timeDifference =itemView.findViewById(R.id.time);
            priceView =itemView.findViewById(R.id.price);
            imgBuy=itemView.findViewById(R.id.buy_button);
        }

        public void setUserDetails(final Context ctx,DatabaseReference postUser) {
            postUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);

                    if(user!=null) {
                        Uri uri = Uri.parse(user.getProfile_pic_url());
                        profilePic.setImageURI(uri);
                    /*Glide.with(ctx).load(user.getProfile_pic_url())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(profilePic);*/
                        userName.setText(user.getDisplay_name());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setPostImage(final Context ctx,final String postImage) {
            Uri uri=Uri.parse(postImage);
            postImageView.setImageURI(uri);
            /*Glide.with(ctx).load(postImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(postImageView);*/
        }
        public void setPostDetails(String desription,String postTime,String price){
            postDescription.setText(desription);
            timeDifference.setText(TimeDifference.getTimeDifference(postTime));
            priceView.setText(price);
        }
        public void setLikeCount(final String post_key, DatabaseReference likesRef){
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(post_key)) {
                        long likes = dataSnapshot.child(post_key).getChildrenCount();
                        if (likes == 1)
                            likesCount.setText("1 Like");
                        else if (likes > 1)
                            likesCount.setText(likes + " likes");
                    } else
                        likesCount.setText("Like");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setLikeButton(final String post_key, DatabaseReference likesRef, final FirebaseUser user){
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(user.getUid()))
                        likeButton.setImageResource(R.drawable.ic_like_on);
                    else
                        likeButton.setImageResource(R.drawable.ic_like_off);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
