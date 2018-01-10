package pict.s2k.frameit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import pict.s2k.frameit.models.MessageModel;
import pict.s2k.frameit.subactivities.ChatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((Button) findViewById(R.id.connectToPortal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestActivity.this,PaymentActivity.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TestActivity.this, ChatActivity.class);
                ArrayList<String> al = new ArrayList<String>();
                //Todo : First get first user uid and then get the another user uid from which we are doing message
                al.add("M7T4uZaaLWRc2YUqL7z3W1kbPn62");
                al.add("SXqKEcE9tsNK0yF7OfRVA4sGDG52") ;
                intent.putStringArrayListExtra(ChatActivity.FIRST_SECOND_PERSON_UID, al);
                startActivity(intent);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /*findViewById(R.id.testSendMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageModel message = new MessageModel("Hello World How are you doing i want this image in 25 buck",
                        System.currentTimeMillis(),
                        "M7T4uZaaLWRc2YUqL7z3W1kbPn62",
                        "SXqKEcE9tsNK0yF7OfRVA4sGDG52",
                        "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e",
                        "https://firebasestorage.googleapis.com/v0/b/frameit-ebf6d.appspot.com/o/profile_pic_thumbnails%2FM7T4uZaaLWRc2YUqL7z3W1kbPn62?alt=media&token=fa29a13f-77a2-4944-97ee-3c4d4c00872e"
                );

                String firstPersonUID = "M7T4uZaaLWRc2YUqL7z3W1kbPn62" ;
                String secondPersonUID = "SXqKEcE9tsNK0yF7OfRVA4sGDG52" ;

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;

                DatabaseReference dbToNodeChatting = firebaseDatabase
                        .getReference()
                        .child(FirebaseTreeConstants.ALL_CHATTING) ;

                dbToNodeChatting
                        .child(firstPersonUID+secondPersonUID)
                        .push()
                        .setValue(message) ;

                DatabaseReference dbRefToChattingFirst = firebaseDatabase
                        .getReference()
                        .child(FirebaseTreeConstants.USER)
                        .child(firstPersonUID)
                        .child(FirebaseTreeConstants.CHATTING)
                        .child(message.getReceiver_uid()) ;

                dbRefToChattingFirst.setValue(firstPersonUID+secondPersonUID);

                DatabaseReference dbRefToChattingSecond = firebaseDatabase
                        .getReference()
                        .child(FirebaseTreeConstants.USER)
                        .child(secondPersonUID)
                        .child(FirebaseTreeConstants.CHATTING)
                        .child(message.getSender_uid()) ;

                dbRefToChattingSecond.setValue(firstPersonUID+secondPersonUID);
            }
        });*/
    }



    /*//curl -u user:password http://sample.campfirenow.com/rooms.xml
    //curl --user "<your-api-key>:<your-api-secret>" https://api.imagga.com/v1/tagging?url=https://imagga.com/static/images/tagging/wind-farm-538576_640.jpg
    public static String getRequest(String imageUrl) {
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            //https://api.imagga.com/v1/tagging?url='+encodeURIComponent(imageUrl)

            URI uri = new URI("https://api.imagga.com/v1/tagging?url="+imageUrl);
            httpGet.setURI(uri);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials("acc_cabf9787123ca3d", "5f1bf676e94500121ab45fd8f234c71e"),
                    HTTP.UTF_8, false));

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return stringBuffer.toString();
    }*/

}