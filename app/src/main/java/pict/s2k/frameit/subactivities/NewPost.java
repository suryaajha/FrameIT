package pict.s2k.frameit.subactivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.MainActivity;
import pict.s2k.frameit.R;
import pict.s2k.frameit.models.ArtPost;

public class NewPost extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    FirebaseDatabase mDatabase=FirebaseDatabase.getInstance();
    FirebaseStorage mStorage=FirebaseStorage.getInstance();
    StorageReference mStorageRef=mStorage.getReference();
    DatabaseReference mRootRef;
    DatabaseReference mPostsReference;
    ProgressDialog mProgressDialog;

    private Uri mImageUri;

    ImageButton mSelectImage;
    EditText mTextDescription;
    EditText mTextPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mRootRef=mDatabase.getReference();
        mPostsReference=mRootRef.child(FirebaseTreeConstants.POSTS);

        mSelectImage=(ImageButton) findViewById(R.id.select_image);
        mTextDescription=(EditText) findViewById(R.id.txt_description);
        mTextPrice=(EditText) findViewById(R.id.txt_price);
    }
    public void startPosting(View v){
        mProgressDialog=new ProgressDialog(NewPost.this);
        mProgressDialog.setMessage("Posting...");
        mProgressDialog.show();
        final String description=mTextDescription.getText().toString().trim();
        final String price=mTextPrice.getText().toString();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        Calendar c = Calendar.getInstance();
        final long sentTime =  c.getTimeInMillis(); //formatDate.format(c.getTime());

        if(!TextUtils.isEmpty(description) && !TextUtils.isEmpty(price) && mImageUri!=null){
            final FirebaseUser user=mAuth.getCurrentUser();
            final DatabaseReference newPost = mPostsReference.push();



            final ArtPost postData=new ArtPost(description,"",sentTime,Long.parseLong(price),user.getUid(),0.0f,0.0f);
            StorageReference filePath = mStorageRef.child("Post_Images").child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    newPost.setValue(postData);
                    newPost.child(FirebaseTreeConstants.POST_IMAGE).setValue(downloadUrl.toString());
                    //Toast.makeText(NewPost.this, "Post Successful", Toast.LENGTH_SHORT).show();
                    //updatePostCount();

                    Intent mainIntent=new Intent(NewPost.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }

                private void updatePostCount() {
                    DatabaseReference postsRef=mRootRef.child(FirebaseTreeConstants.POSTS);
                    Query postsCount=postsRef.orderByChild(FirebaseTreeConstants.UID).equalTo(user.getUid());
                    postsCount.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DatabaseReference mUserPostCountRef=mRootRef.child(FirebaseTreeConstants.USER)
                                    .child(user.getUid())
                                    .child(FirebaseTreeConstants.POST_COUNTER);
                            mUserPostCountRef.setValue(dataSnapshot.getChildrenCount());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            });


        }else{
            Toast.makeText(this, "Please fill up all details", Toast.LENGTH_SHORT).show();
        }

    }



    public void openGallery(View v){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            mSelectImage.setImageURI(mImageUri);
        }
    }
}
