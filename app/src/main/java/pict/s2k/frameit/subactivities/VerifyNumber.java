package pict.s2k.frameit.subactivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.MainActivity;
import pict.s2k.frameit.R;
import pict.s2k.frameit.auth.AuthenticationBaseCode;
import pict.s2k.frameit.models.User;

public class VerifyNumber extends AppCompatActivity implements TextWatcher{
    private static final String TAG = "Verification";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference usersRef;
    AuthCredential googleCredential;


    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //[OTP Edittexts]
    private EditText edtOTP1;
    private EditText edtOTP2;
    private EditText edtOTP3;
    private EditText edtOTP4;
    private EditText edtOTP5;
    private EditText edtOTP6;
    //[OTP Edittexts]

    private EditText mobileNumberView;
    private Button btnSend;


    public static final int MAX=1;
    public static final String COUNTRY_CODE="+91";
    private String mobileNumber;
    private String enteredCode="";
    private boolean withCredential;
    ProgressDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);

        initMobileNumber();
        setupOTPFields();

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        //Toast.makeText(this, user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
        googleCredential=GoogleAuthProvider.getCredential(user.getUid(),null);
        //Toast.makeText(this, googleCredential.getProvider(), Toast.LENGTH_SHORT).show();
        usersRef =FirebaseDatabase.getInstance().getReference().child(FirebaseTreeConstants.USER);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(final PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                updatePhoneNumberWithAuth(credential);
                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                dialog.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;


                // ...
            }
        };

        findViewById(R.id.btn_resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(mobileNumber,mResendToken);
            }
        });

    }

    private void initMobileNumber() {
        Intent intent=getIntent();
        withCredential=intent.getExtras().getBoolean("with_credential");
        if(!withCredential){
            mobileNumber=intent.getExtras().getString("mobile_number");
            ((EditText) findViewById(R.id.edt_mobile_number)).setText(mobileNumber);
            //((Button) findViewById(R.id.btn_send)).setVisibility(View.INVISIBLE);
            //startPhoneNumberVerification(mobileNumber);
        }




    }
    public void sendCode(View v) {
        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.show();
        mobileNumber = mobileNumberView.getText().toString().trim();
        if (!TextUtils.isEmpty(mobileNumber)) {
            startPhoneNumberVerification(COUNTRY_CODE + mobileNumber);
        } else {
            mobileNumberView.setError(getString(R.string.error_field_required));
        }
    }
    private void setupOTPFields() {
        mobileNumberView=findViewById(R.id.edt_mobile_number);
        btnSend=findViewById(R.id.btn_send);
        edtOTP1=findViewById(R.id.edt_otp_1);
        edtOTP2=findViewById(R.id.edt_otp_2);
        edtOTP3=findViewById(R.id.edt_otp_3);
        edtOTP4=findViewById(R.id.edt_otp_4);
        edtOTP5=findViewById(R.id.edt_otp_5);
        edtOTP6=findViewById(R.id.edt_otp_6);


        edtOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isMAX(edtOTP1)){
                    enteredCode+=edtOTP1.getText().toString();
                    edtOTP2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isMAX(edtOTP2)){
                    enteredCode+=edtOTP2.getText().toString();
                    edtOTP3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isMAX(edtOTP3)) {
                    enteredCode+=edtOTP3.getText().toString();
                    edtOTP4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isMAX(edtOTP4)) {
                    enteredCode+=edtOTP4.getText().toString();
                    edtOTP5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isMAX(edtOTP5)) {
                    enteredCode+=edtOTP5.getText().toString();
                    edtOTP6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isMAX(edtOTP6)){
                    enteredCode+=edtOTP6.getText().toString();
                    //Toast.makeText(VerifyNumber.this, enteredCode, Toast.LENGTH_SHORT).show();
                    verifyPhoneNumberWithCode(mVerificationId,enteredCode);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private boolean isMAX(EditText editText){
        return editText.getText().length()==MAX;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    public void verifyAccount(String code){
        verifyPhoneNumberWithCode(mVerificationId,code);
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }
    public void confirmAccount(View v){

        //String mobileNumber=((EditText)findViewById(R.id.edt_mobile_number)).getText().toString().trim();

        //User userData=new User(user.getDisplayName(),Long.parseLong(mobileNumber),user.getEmail());
        //String countryCode="+91";
        startPhoneNumberVerification(mobileNumber);

        //AuthenticationBaseCode.createAFreshNewAccount(user.getUid(),userData,this,user.getPhotoUrl(),true);
        //ImageManipulation.storeAndGetThumbnail(user.getPhotoUrl(),user.getUid(),this);
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        updatePhoneNumberWithAuth(credential);
        // [END verify_with_code]
        /*mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(VerifyNumber.this, "Phone Number Linked", Toast.LENGTH_SHORT).show();

            }
        });*/

        //signInWithPhoneAuthCredential(credential);
    }

    private void updatePhoneNumberWithAuth(PhoneAuthCredential credential) {
        user.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(VerifyNumber.this, "Updated number", Toast.LENGTH_SHORT).show();
                if(user.getPhoneNumber()!=null) {
                    User userData = new User(user.getDisplayName(), Long.parseLong(user.getPhoneNumber()), user.getEmail());
                    userData.setNumber_verified(true);

                    if (withCredential)
                        AuthenticationBaseCode.createAFreshNewAccount(user.getUid(), userData, VerifyNumber.this, user.getPhotoUrl(), true);
                    else {
                        DatabaseReference verifyRef = FirebaseDatabase.getInstance()
                                .getReference()
                                .child(FirebaseTreeConstants.USER)
                                .child(user.getUid())
                                .child(FirebaseTreeConstants.NUMBER_VERIFIED);
                        verifyRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                signOut();
                                Intent loginIntent = new Intent(VerifyNumber.this, LoginActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginIntent);
                            }
                        });
                    }
                }else{
                    Toast.makeText(VerifyNumber.this, "Number already used", Toast.LENGTH_SHORT).show();
                }
                /*Intent mainIntent=new Intent(VerifyNumber.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);*/
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            final FirebaseUser user = task.getResult().getUser();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]
}

