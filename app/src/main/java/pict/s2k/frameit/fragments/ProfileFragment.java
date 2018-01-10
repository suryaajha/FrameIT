package pict.s2k.frameit.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabWidget;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pict.s2k.frameit.FirebaseTreeConstants;
import pict.s2k.frameit.R;
import pict.s2k.frameit.models.User;
import pict.s2k.frameit.subactivities.SetupFont;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;

    //UI Components
    SimpleDraweeView profileImage;
    TextView userName;
    TextView postsCount;
    TextView followersCount;
    TextView ratingView;
    private FragmentTabHost  mTabHost;
    private TabWidget tabWidget;

    //Firebase classes
    FirebaseDatabase mDatabase;
    DatabaseReference rootRef;
    DatabaseReference userRef;
    Query postsRef;
    DatabaseReference followersRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private int[] tabIcons = {
            R.drawable.ic_grid_view,
            R.drawable.ic_list_view,
    };

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile, container, false);
        View gridIconView=inflater.inflate(R.layout.custom_tab_grid,container,false);
        View linearIconView=inflater.inflate(R.layout.custom_tab_list,container,false);
        mTabHost=(FragmentTabHost) v.findViewById(android.R.id.tabhost);
        tabWidget=v.findViewById(android.R.id.tabs);

        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tab_content);

        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("",getResources().getDrawable(R.drawable.ic_grid_view)),
                PostsGridView.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("",getResources().getDrawable(R.drawable.ic_list_view)),
                PostsLinearView.class, null);

        /*viewPager=v.findViewById(R.id.container);
        setupViewPager(viewPager);*/

        setupUI(v);
        loadWithFirebaseData();

        /*tabLayout =v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();*/


        SetupFont.setupDefaultFont(getActivity());
        return v;
    }

    private void loadWithFirebaseData() {
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        rootRef=mDatabase.getReference();
        rootRef.keepSynced(true);
        userRef=rootRef.child(FirebaseTreeConstants.USER).child(currentUser.getUid());
        followersRef=rootRef.child(FirebaseTreeConstants.FOLLOWERS);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userName.setText(user.getDisplay_name());
                Uri uri=Uri.parse(user.getProfile_pic_url());
                profileImage.setImageURI(uri);
                /*Glide.with(getActivity()).load(user.getProfile_pic_url())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profileImage);*/
                followersCount.setText(String.valueOf(user.getFollowers()));
                postsCount.setText(String.valueOf(user.getPosts()));
                ratingView.setText(String.valueOf(user.getRating()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    //Test
    private void setupUI(View v) {
        profileImage=v.findViewById(R.id.profile_image);
        userName=v.findViewById(R.id.txt_name);
        postsCount=v.findViewById(R.id.txt_posts);
        followersCount=v.findViewById(R.id.txt_followers);
        ratingView=v.findViewById(R.id.txt_rating);
    }

    private void setupTabIcons() {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_grid,null);
        tabLayout.getTabAt(0).setCustomView(view);
        view=LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_list,null);
        tabLayout.getTabAt(1).setCustomView(view);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        //adapter.addFragment(new TimeCheck(), "Time Comparision");
        adapter.addFragment(new PostsGridView(), "Grid View");
        adapter.addFragment(new PostsLinearView(), "List View");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
