package pict.s2k.frameit.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import pict.s2k.frameit.R;
import pict.s2k.frameit.models.MessageModel;

/**
 * Created by suryaa on 14/9/17.
 */

public class ChattingAdapterListView extends ArrayAdapter<MessageModel> {

    private List<MessageModel> messageModelList;
    private Context context;
    public static String LOGGED_IN_USER_UID;

    public ChattingAdapterListView(Context context, int resource, List<MessageModel> items) {
        super(context, resource, items);
        this.messageModelList = items;
        this.context=context ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.chatting_single_row, null);
        }

        MessageModel messageModel = messageModelList.get(position);

        if (messageModel != null) {
            TextView chatStringLeft;
            TextView chatStringRight;
            ImageView dpOfChatLeft;
            ImageView dpOfChatRight;
            View parentView;

            parentView = v;
            chatStringLeft = ((TextView) v.findViewById(R.id.chatting_left).findViewById(R.id.chatting_text_left));
            chatStringRight = ((TextView) v.findViewById(R.id.chatting_right).findViewById(R.id.chatting_text_right));


            //dpOfChatLeft = ((ImageView) v.findViewById(R.id.chatting_left).findViewById(R.id.profile_image_left));
            //dpOfChatRight = ((ImageView) v.findViewById(R.id.chatting_right).findViewById(R.id.profile_image_right));


            if (messageModel.getSender_uid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                //Toast.makeText(context, "hello world", Toast.LENGTH_SHORT).show();
                parentView.findViewById(R.id.chatting_right).setVisibility(View.VISIBLE);
                parentView.findViewById(R.id.chatting_left).setVisibility(View.GONE);
                /*Uri uri=Uri.parse(messageModel.getReceiver_profile_uri());
                dpOfChatRight.setImageURI(uri);*/
                //Glide.with(context).load(messageModel.getSender_profile_uri()).into(dpOfChatRight);
                chatStringRight.setText(messageModel.getMessage());

            } else {
                //Toast.makeText(context, "hello world1", Toast.LENGTH_SHORT).show();
                parentView.findViewById(R.id.chatting_left).setVisibility(View.VISIBLE);
                parentView.findViewById(R.id.chatting_right).setVisibility(View.GONE);
                /*Uri uri=Uri.parse(messageModel.getReceiver_profile_uri());
                dpOfChatLeft.setImageURI(uri);*/
                //Glide.with(context).load(messageModel.getReceiver_profile_uri()).into(dpOfChatLeft);
                chatStringLeft.setText(messageModel.getMessage());
            }
        }

        return v;
    }

}

