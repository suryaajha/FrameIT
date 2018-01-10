package pict.s2k.frameit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.R;
import pict.s2k.frameit.adapter.ChattingAdapterListView;
import pict.s2k.frameit.adapter.NotificationAdapterListView;
import pict.s2k.frameit.models.NotificationModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    public static ListView allNotificationsListView ;
    public static NotificationAdapterListView notificationAdapterListView ;
    public static List<NotificationModel> notificationList = new ArrayList<>() ;


    public NotificationFragment() {
        notificationList = new ArrayList<>();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_notification, container, false);

        allNotificationsListView = ((ListView) v.findViewById(R.id.notifcations_list_view)) ;

        notificationAdapterListView = new NotificationAdapterListView(getContext(), R.layout.notification_single_row, notificationList);
        allNotificationsListView.setAdapter(notificationAdapterListView);

        prepareNotifications();

        return v ;

    }

    static void prepareNotifications(){

        DatabaseReference dbRefToNotifications = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseTreeConstants.USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(FirebaseTreeConstants.NOTIFICATIONS) ;

        dbRefToNotifications.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotificationModel notificationModel = dataSnapshot.getValue(NotificationModel.class) ;
                notificationModel.setKey(dataSnapshot.getKey());
                notificationAdapterListView.add(notificationModel);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;

    }

}
