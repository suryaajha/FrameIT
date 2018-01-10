package pict.s2k.frameit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import pict.s2k.frameit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishListFragment extends Fragment {


    public WishListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_wish_list, container, false);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        ((TextView) v.findViewById(R.id.temp)).setText(mAuth.getCurrentUser().isEmailVerified()+"");
        return v;
    }

}
