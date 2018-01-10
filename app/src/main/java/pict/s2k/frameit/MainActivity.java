package pict.s2k.frameit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import me.anwarshahriar.calligrapher.Calligrapher;
import pict.s2k.frameit.fragments.ExploreFragment;
import pict.s2k.frameit.fragments.NotificationFragment;
import pict.s2k.frameit.fragments.ProfileFragment;
import pict.s2k.frameit.fragments.WishListFragment;
import pict.s2k.frameit.subactivities.LoginActivity;
import pict.s2k.frameit.subactivities.NewPost;
import pict.s2k.frameit.subactivities.SetupFont;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    FragmentManager fragmentManager=getSupportFragmentManager();
    FragmentTransaction transaction;
    BottomNavigationView navigation;
    private GoogleApiClient mGoogleApiClient ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction=fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.content,new ExploreFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.content,new NotificationFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                case R.id.navigation_profile:
                    transaction.replace(R.id.content,new ProfileFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }

            return false;
        }

    };
    void setDefaultFragment(){
        transaction.replace(R.id.content,new ExploreFragment()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetupFont.setupDefaultFont(this);

        googleSignInConfiguration();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        transaction=fragmentManager.beginTransaction();
        setDefaultFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.new_post){
            startActivity(new Intent(MainActivity.this, NewPost.class));
        }else if(item.getItemId()==R.id.sign_out){
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Sign out");
            builder.setMessage("Do you want to sign out ?");
            builder.setPositiveButton("Take Me Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logOut();
                    Intent loginIntent=new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            });
            builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/
    private void logOut() {
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mAuth.signOut();
            Toast.makeText(this, "Signed Out...", Toast.LENGTH_SHORT).show();
        }

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
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Network connection failed", Toast.LENGTH_SHORT).show();
    }
}