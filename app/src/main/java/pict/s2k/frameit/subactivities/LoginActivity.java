package pict.s2k.frameit.subactivities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;
import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.MainActivity;
import pict.s2k.frameit.R;
import pict.s2k.frameit.auth.AuthenticationBaseCode;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    //UI references
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    //For google sign in
    private GoogleApiClient mGoogleApiClient ;
    private static final String TAG = "Google Sign In Issue";
    private static final int RC_SIGN_IN = 9001;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Google sign in
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        googleSignInConfiguration();
        DatabaseReference mVerifyRef=FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseTreeConstants.USER);
        mVerifyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(user!=null) {
                            if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified() && dataSnapshot.hasChild(user.getUid())) {
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //setup login UI
        mEmailView=(AutoCompleteTextView) findViewById(R.id.txt_email);
        loadSavedLines(mEmailView);
        mPasswordView=(EditText) findViewById(R.id.txt_password);
        mEmailSignInButton=(Button) findViewById(R.id.btn_sign_in);
        SetupFont.setupDefaultFont(this);
    }

    //Google Sign In

    private void googleSignInConfiguration() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClientWork(gso);
    }

    private void googleApiClientWork(GoogleSignInOptions gso) {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    // [START signIn]
    public void signIn(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Network connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            //TextView mStatusTextView = ((TextView) findViewById(R.id.status)) ;
            //mStatusTextView.setText(acct.getDisplayName());
        } else {
            // Signed out, show unauthenticated UI.
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final String uid=task.getResult().getUser().getUid();
                            user=task.getResult().getUser();
                            DatabaseReference mUserRef= FirebaseDatabase.getInstance()
                                    .getReference().child(FirebaseTreeConstants.USER);
                            mUserRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent launchIntent;
                                    if(dataSnapshot.hasChild(uid)){
                                        DataSnapshot number=dataSnapshot.child(uid).child(FirebaseTreeConstants.NUMBER_VERIFIED);
                                        String numberVerified=number.getValue().toString();
                                        if(Boolean.parseBoolean(numberVerified)){
                                            launchIntent=new Intent(LoginActivity.this,MainActivity.class);
                                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(launchIntent);
                                        }else {
                                            launchIntent=new Intent(LoginActivity.this,VerifyNumber.class);
                                            launchIntent.putExtra("with_credential",true);
                                            startActivity(launchIntent);
                                        }
                                    }else{
                                        launchIntent=new Intent(LoginActivity.this,VerifyNumber.class);
                                        launchIntent.putExtra("with_credential",true);
                                        startActivity(launchIntent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(LoginActivity.this, "Sign Out is Successufl", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //End Google Sign in

    public void attemptLogin(View v){
        dialog=new ProgressDialog(this);
        dialog.setMessage("Signing In...");
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if(!TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView=mPasswordView;
            cancel=true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            dialog.show();
            signInWithEmailPassword(email,password);
            }

        }
    public void signInWithEmailPassword(String email, String password){
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    final String uid=task.getResult().getUser().getUid();
                    DatabaseReference mUserRef= FirebaseDatabase.getInstance()
                            .getReference().child(FirebaseTreeConstants.USER);
                    mUserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Intent launchIntent;
                            if (dataSnapshot.hasChild(uid)) {
                                DataSnapshot number = dataSnapshot.child(uid).child(FirebaseTreeConstants.NUMBER_VERIFIED);
                                String numberVerified = number.getValue().toString();
                                if (Boolean.parseBoolean(numberVerified)) {
                                    if(mAuth.getCurrentUser().isEmailVerified()) {
                                        dialog.dismiss();
                                        launchIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(launchIntent);
                                    }else{
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Please verify your email first !", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    dialog.dismiss();
                                    launchIntent = new Intent(LoginActivity.this, VerifyNumber.class);
                                    launchIntent.putExtra("with_credential", false);
                                    startActivity(launchIntent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }else{
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    public void createNewAccount(View v){
        Intent signUpIntent=new Intent(LoginActivity.this,SignUpActivity.class);
        signUpIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signUpIntent);
    }
    public void forgetPassword(View v){
        String email=mEmailView.getText().toString().trim();
        if(!TextUtils.isEmpty(email)){
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(LoginActivity.this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void loadSavedLines(AutoCompleteTextView mAutoCompleteTextView) {
        SharedPreferences settings = getSharedPreferences("data", 0);
        int size = settings.getInt("size", 0);

        if (size != 0) {
            String[] history = new String[size];

            for (int i = 0; i < size; i++) {
                history[i] = settings.getString(String.valueOf(i + 1), "empty");
            }
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_email_suggestion, history);
            mAutoCompleteTextView.setAdapter(adapter);
            //mAutoCompleteTextView.setText(saved_uri);
        }
    }
    public void checkThanSaveLine(String new_line, AutoCompleteTextView autoComplete) {
        SharedPreferences settings = getSharedPreferences("data", 0);
        int size = settings.getInt("size", 0);
        String[] history = new String[size+1];
        boolean repeat = false;
        for (int i = 0; i < size; i++) {
            history[i] = settings.getString(String.valueOf(i+1),"empty");
            if (history[i].equals(new_line.toString())) {
                repeat = true;
            }
        }
        if (repeat == false) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(String.valueOf(size+1), new_line.toString() );
            editor.putInt("size", size+1);
            editor.commit();
            history[size] = new_line.toString();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,R.layout.item_email_suggestion, history);
            autoComplete.setAdapter(adapter);
        }
    }
}

