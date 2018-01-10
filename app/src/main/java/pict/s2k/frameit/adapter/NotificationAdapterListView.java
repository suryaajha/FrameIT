package pict.s2k.frameit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.R;
import pict.s2k.frameit.models.MessageModel;
import pict.s2k.frameit.models.NotificationModel;
import pict.s2k.frameit.subactivities.ChatActivity;
import pict.s2k.frameit.subactivities.PortfolioActivity;

/**
 * Created by suryaa on 16/9/17.
 */

public class NotificationAdapterListView extends ArrayAdapter<NotificationModel> {

    private List<NotificationModel> notificationModelList;
    private Context context;
    public static String LOGGED_IN_USER_UID;

    public NotificationAdapterListView(Context context, int resource, List<NotificationModel> items) {
        super(context, resource, items);
        this.notificationModelList = items;
        this.context=context ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.notification_single_row, null);
        }

        final NotificationModel notificationModel = notificationModelList.get(position);

        if (notificationModel != null) {

            TextView notificationText ;
            final ImageView notificationPhoto ;

            View parentView;

            parentView = v;

            notificationText = ((TextView) v.findViewById(R.id.notification_text)) ;
            notificationPhoto = ((ImageView) v.findViewById(R.id.notification_image)) ;

            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Do your task firs

                    DatabaseReference dbRefToDeleteNotification = FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseTreeConstants.USER)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(FirebaseTreeConstants.NOTIFICATIONS)
                            .child(notificationModel.getKey()) ;

                    dbRefToDeleteNotification.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent portfolioIntent=new Intent( context , PortfolioActivity.class);
                            Toast.makeText(context, ""+notificationModel.getUidOfPoster(), Toast.LENGTH_SHORT).show();
                            portfolioIntent.putExtra(FirebaseTreeConstants.UID,notificationModel.getUidOfPoster());
                            getContext().startActivity(portfolioIntent);
                        }
                    }) ;
                }
            });

            if(notificationText!=null){
                notificationText.setText(notificationModel.getText()) ;
            }
            if(notificationPhoto!=null){
                DatabaseReference dbRefToImage = FirebaseDatabase.getInstance().getReference(notificationModel.getPathForImage()) ;
                dbRefToImage.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String urlOfImage = dataSnapshot.getValue(String.class) ;
                        Glide.with(context).load(urlOfImage).into(notificationPhoto);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        return v;
    }

}

