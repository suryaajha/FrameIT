package pict.s2k.frameit.subactivities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pict.s2k.frameit.MainActivity;
import pict.s2k.frameit.R;
import pict.s2k.frameit.auth.AuthenticationBaseCode;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SetupFont.setupDefaultFont(this);
    }
    public void registerUser(View v){
        ArrayList<EditText> editTextArrayList=new ArrayList<>();
        editTextArrayList.add((EditText)findViewById(R.id.txt_name));
        editTextArrayList.add((EditText)findViewById(R.id.txt_mobile));
        editTextArrayList.add((EditText)findViewById(R.id.txt_email));
        editTextArrayList.add((EditText)findViewById(R.id.txt_password));
        editTextArrayList.add((EditText)findViewById(R.id.txt_re_password));

        boolean ok = true ;
        for(EditText eT : editTextArrayList){
            if(TextUtils.isEmpty(eT.getText().toString()))
                ok=false;
        }
        if(editTextArrayList.get(3).getText().toString().equals(editTextArrayList.get(4).getText().toString()) && ok){
            String email=editTextArrayList.get(2).getText().toString().trim();
            String password=editTextArrayList.get(3).getText().toString().trim();
            String name=editTextArrayList.get(0).getText().toString().trim();
            String mobileNumber=editTextArrayList.get(1).getText().toString().trim();
            AuthenticationBaseCode.createAccountWithEmailAndPassword(SignUpActivity.this,email,password,name,mobileNumber,selectedImageUri,SignUpActivity.this);

        }
    }
    public void selectImage(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            selectedImageUri = data.getData() ;
            ImageView iV = ((ImageView) findViewById(R.id.profile_image));
            iV.setImageURI(null);
            iV.setImageURI(selectedImageUri);
        }
    }
}
