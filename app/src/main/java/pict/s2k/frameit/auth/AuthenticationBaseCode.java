package pict.s2k.frameit.auth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.MainActivity;
import pict.s2k.frameit.models.User;
import pict.s2k.frameit.subactivities.LoginActivity;
import pict.s2k.frameit.subactivities.VerifyNumber;
import pict.s2k.frameit.utils.ImageManipulation;

/**
 * Created by suryaa on 3/9/17.
 */

public class AuthenticationBaseCode {

    private static boolean accountCreated = false ;

    public static boolean createAccountWithEmailAndPassword(String email, String password,final Activity activity) {
        accountCreated = false ;
        FirebaseAuth mAuth = FirebaseAuth.getInstance() ;
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            accountCreated = true ;
                            AuthResult authResult = task.getResult() ;
                            FirebaseUser firebaseUser = authResult.getUser() ;

                            /*UserModel.BasicPersonalInformation basicPersonalInformation = new UserModel.BasicPersonalInformation(Long.parseLong(firebaseUser.getPhoneNumber(),10)) ;   ;
                            UserModel.Photo photo = new UserModel.Photo( ImageManipulation.storeAndGetThumbnail(firebaseUser.getPhotoUrl())) ;

                            createAFreshNewAccount(firebaseUser.getUid(),basicPersonalInformation,photo);*/
                            //Toast.makeText(activity, "Created A new Acconunt", Toast.LENGTH_LONG).show() ;
                            activity.startActivity(new Intent(activity, MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) ;
        if(accountCreated)
            return true ;
        else
            return false ;
    }

    public static boolean createAccountWithEmailAndPassword(Context ctx, String email, String password, final String displayName , final String mobileNo , final Uri selectedProfilePhoto, final Activity activity) {
        accountCreated = false ;
        final ProgressDialog dialog=new ProgressDialog(ctx);
        dialog.setMessage("Setting up your account...");
        dialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance() ;
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            accountCreated = true ;
                            AuthResult authResult = task.getResult() ;
                            final FirebaseUser firebaseUser = authResult.getUser() ;
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName).build();
                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                User user=new User(firebaseUser.getDisplayName(),Long.parseLong(mobileNo),firebaseUser.getEmail());
                                                createAFreshNewAccount(dialog,firebaseUser.getUid(),user,activity,selectedProfilePhoto,false);
                                            }
                                        }
                                    });
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid){
                                    Toast.makeText(activity, "Verification Link Sent", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) ;
        if(accountCreated)
            return true ;
        else
            return false ;
    }
    public static void createAFreshNewAccount(final String uid, final User user, final Activity activity, final Uri profilePicUri, final boolean withCredential){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference currentUserRef=firebaseDatabase.getReference().child(FirebaseTreeConstants.USER).child(uid);
        currentUserRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                StorageReference profilePicRef= FirebaseStorage.getInstance().getReference()
                        .child(FirebaseTreeConstants.USER_PROFILE_PICTURES)
                        .child(uid);
                if(withCredential){
                    ImageManipulation.storeAndGetThumbnail(profilePicUri,uid,activity);
                }else{

                    profilePicRef.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            currentUserRef.child(FirebaseTreeConstants.PROFILE_URL).setValue(downloadUrl.toString());
                            Toast.makeText(activity, "Created A new Acconunt", Toast.LENGTH_LONG).show() ;
                            Intent mainIntent=new Intent(activity,VerifyNumber.class);
                            mainIntent.putExtra("mobile_number", String.valueOf(user.getMobile_no()));
                            mainIntent.putExtra("with_credential",false);
                            activity.startActivity(mainIntent);
                        }
                    });
                }
            }
        });
    }
    public static void createAFreshNewAccount(final ProgressDialog dialog, final String uid, final User user, final Activity activity, final Uri profilePicUri, final boolean withCredential){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference currentUserRef=firebaseDatabase.getReference().child(FirebaseTreeConstants.USER).child(uid);
        currentUserRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                StorageReference profilePicRef= FirebaseStorage.getInstance().getReference()
                        .child(FirebaseTreeConstants.USER_PROFILE_PICTURES)
                        .child(uid);
                if(withCredential){
                    ImageManipulation.storeAndGetThumbnail(profilePicUri,uid,activity);
                }else{

                    profilePicRef.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            currentUserRef.child(FirebaseTreeConstants.PROFILE_URL).setValue(downloadUrl.toString());
                            Toast.makeText(activity, "Created A new Acconunt", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            Intent mainIntent=new Intent(activity,VerifyNumber.class);
                            mainIntent.putExtra("mobile_number", String.valueOf(user.getMobile_no()));
                            mainIntent.putExtra("with_credential",false);
                            activity.startActivity(mainIntent);
                        }
                    });
                }
            }
        });
    }
    /*public static void createAFreshNewAccount(String uid , UserModel.BasicPersonalInformation basicPersonalInformation , UserModel.Photo photo) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUserNodeReference = firebaseDatabase.getReference()
                .child(FirebaseTreeConstants.USER)
                .child(uid);
        currentUserNodeReference
                .child(FirebaseTreeConstants.PERSONAL_INFORMATION)
                .child(FirebaseTreeConstants.PERSONAL_INFORMATION_BASIC)
                .setValue(basicPersonalInformation);
        currentUserNodeReference
                .child(FirebaseTreeConstants.PERSONAL_INFORMATION)
                .child(FirebaseTreeConstants.PHOTOS)
                .setValue(photo);
        currentUserNodeReference
                .child(FirebaseTreeConstants.SETTINGS)
                .child(FirebaseTreeConstants.COUNTERS)
                .child(FirebaseTreeConstants.POST_COUNTERS)
                .setValue(0);
        currentUserNodeReference
                .child(FirebaseTreeConstants.SETTINGS)
                .child(FirebaseTreeConstants.COUNTERS)
                .child(FirebaseTreeConstants.WORK_COUNTERS)
                .setValue(0);
    }*/
}
