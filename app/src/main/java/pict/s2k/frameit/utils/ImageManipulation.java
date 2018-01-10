package pict.s2k.frameit.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import pict.s2k.frameit.FirebaseTreeConstants;

/**
 * Created by suryaa on 5/9/17.
 */

public class ImageManipulation {

    public static String UID;

    //Create a Thumnail and then Store to Storage Bucket and then return the download Url in string
    public static void storeAndGetThumbnail(Uri phtotToStore, final String uid, Context ctx) {
        /*ImageCompressionUtils utils=new ImageCompressionUtils(ctx);
        String filePath=utils.compressImage(phtotToStore.toString());
        Uri thumbnailUri=Uri.fromFile(new File(filePath));*/
        UID = uid;
        Uri thumbnailUri = phtotToStore;
        new UploadTask().execute(thumbnailUri);

    }

    public static class UploadTask extends AsyncTask<Uri, Void, Void> {
        private InputStream input = null;

        @Override
        protected Void doInBackground(Uri... uris) {

            try {
                input = new URL(uris[0].toString()).openStream();
                StorageReference thumbnailPath = FirebaseStorage.getInstance().getReference().child(FirebaseTreeConstants.PROFILE_PICTURE_THUMBNAIL).child(UID);
                thumbnailPath.putStream(input).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
                    @Override
                    public void onSuccess(TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        DatabaseReference userID = FirebaseDatabase.getInstance()
                                .getReference()
                                .child(FirebaseTreeConstants.USER)
                                .child(UID)
                                .child(FirebaseTreeConstants.PROFILE_URL);
                        userID.setValue(downloadUrl.toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

