package pict.s2k.frameit.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.R;
import pict.s2k.frameit.models.ArtPost;
import pict.s2k.frameit.models.User;
import pict.s2k.frameit.subactivities.PortfolioActivity;
import pict.s2k.frameit.subactivities.PostFullScreen;
import pict.s2k.frameit.utils.TimeDifference;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsGridView extends Fragment {
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRootRef;
    DatabaseReference mPostsRef;
    Query postsRef;

    RecyclerView gridPostsRecycler;

    public static final String ACTIVITY_MAIN="MainActivity";
    public static final String ACTIVITY_PORTFOLIO="PortfolioActivity";

    public PostsGridView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_grid_view, container, false);
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        mRootRef=mDatabase.getReference();
        mPostsRef=mRootRef.child(FirebaseTreeConstants.POSTS);

        gridPostsRecycler=v.findViewById(R.id.rv_grid_posts);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),3);
        /*gridLayoutManager.setReverseLayout(true);
        gridLayoutManager.setStackFromEnd(true);*/
        gridPostsRecycler.setLayoutManager(new GridLayoutManager(getActivity(),3));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        gridPostsRecycler.setAdapter(initFirebaseRecyclerAdapter());

    }
    private FirebaseRecyclerAdapter<ArtPost,PostViewHolder> initFirebaseRecyclerAdapter() {
        String activityName=getActivity().getClass().getSimpleName();
        if(activityName.equals(ACTIVITY_MAIN)){
            postsRef=mPostsRef.orderByChild(FirebaseTreeConstants.UID).equalTo(mAuth.getCurrentUser().getUid());
        }else if(activityName.equals(ACTIVITY_PORTFOLIO)){
            String uid=getActivity().getIntent().getExtras().getString(FirebaseTreeConstants.UID);
            postsRef=mPostsRef.orderByChild(FirebaseTreeConstants.UID).equalTo(uid);
        }
        FirebaseRecyclerAdapter<ArtPost,PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ArtPost, PostViewHolder>(
                ArtPost.class,
                R.layout.item_grid_post,
                PostViewHolder.class,
                postsRef
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, ArtPost model, int position) {
                final String post_key=getRef(position).getKey();
                //final FirebaseUser user=mAuth.getCurrentUser();
                //DatabaseReference postUser=usersRef.child(model.getUid());
                viewHolder.setPostImage(getActivity(),model.getPost_image());
                viewHolder.postImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure want delete this post ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                              DatabaseReference deleteRef=FirebaseDatabase.getInstance().getReference()
                                      .child(FirebaseTreeConstants.POSTS)
                                      .child(post_key);
                                deleteRef.removeValue();
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
                        return true;
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
            }

        };
        return firebaseRecyclerAdapter;
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        SimpleDraweeView postImageView;

        public PostViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            postImageView =itemView.findViewById(R.id.post_image);
        }
        public void setPostImage(final Context ctx,final String postImage) {
            Uri uri=Uri.parse(postImage);
            postImageView.setImageURI(uri);
            /*Glide.with(ctx).load(postImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(postImageView);*/
        }
    }
}
