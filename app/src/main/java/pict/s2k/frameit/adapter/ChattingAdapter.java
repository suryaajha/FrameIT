package pict.s2k.frameit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import pict.s2k.frameit.R;
import pict.s2k.frameit.models.MessageModel;

/**
 * Created by suryaa on 13/9/17.
 */

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.MyViewHolder> {

    public List<MessageModel> messageModelList;
    private Context context ;
    public static String LOGGED_IN_USER_UID ;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView chatStringLeft ;
        public TextView chatStringRight ;
        public ImageView dpOfChatLeft ;
        public ImageView dpOfChatRight ;
        public View parentView ;

        public MyViewHolder(View view) {
            super(view);
            parentView = view ;
            chatStringLeft = ((TextView) view.findViewById(R.id.chatting_left).findViewById(R.id.chatting_text_left)) ;
            chatStringRight = ((TextView) view.findViewById(R.id.chatting_right).findViewById(R.id.chatting_text_right)) ;

            //dpOfChatLeft = ((ImageView) view.findViewById(R.id.chatting_left).findViewById(R.id.profile_image_left)) ;
            //dpOfChatRight = ((ImageView) view.findViewById(R.id.chatting_right).findViewById(R.id.profile_image_right)) ;

        }
    }

    public ChattingAdapter(List<MessageModel> messageModelList , Context context) {
        this.messageModelList = messageModelList;
        this.context = context ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatting_single_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageModel messageModel = messageModelList.get(position);
        if(messageModel.getSender_uid() == FirebaseAuth.getInstance().getCurrentUser().getUid()) {
            Toast.makeText(context, "0 " + messageModel.getSender_uid() , Toast.LENGTH_SHORT).show();
            holder.parentView.findViewById(R.id.chatting_right).setVisibility(View.VISIBLE);
            holder.parentView.findViewById(R.id.chatting_left).setVisibility(View.GONE);
            //Glide.with(context).load(messageModel.getSender_profile_uri()).into(holder.dpOfChatRight);
            holder.chatStringRight.setText(messageModel.getMessage());

        }else {
            Toast.makeText(context, "" + messageModel.getSender_uid() , Toast.LENGTH_SHORT).show();
            holder.parentView.findViewById(R.id.chatting_left).setVisibility(View.VISIBLE);
            holder.parentView.findViewById(R.id.chatting_right).setVisibility(View.GONE);
            //Glide.with(context).load(messageModel.getReceiver_profile_uri()).into(holder.dpOfChatLeft);
            holder.chatStringLeft.setText(messageModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }
}

