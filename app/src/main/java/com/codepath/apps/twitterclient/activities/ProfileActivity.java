package com.codepath.apps.twitterclient.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.twitterclient.helpers.DeviceDimensionsHelper;
import com.codepath.apps.twitterclient.layouts.CustomRelativeLayout;
import com.codepath.apps.twitterclient.models.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) getIntent().getParcelableExtra("user");
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = new UserTimelineFragment();
            fragmentUserTimeline.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentUserTimelineFrame, fragmentUserTimeline);
            ft.commit();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55ACEE")));
            getSupportActionBar().setTitle(user.getScreenName());
        }
        populateUserHeader();
    }

    private void populateUserHeader() {
        final CustomRelativeLayout crlUserHeader = (CustomRelativeLayout) findViewById(R.id.crlUserHeader);
        ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        TextView tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        Picasso.with(this).load(user.getProfleImageUrl()).into(ivProfilePic);
        tvProfileName.setText(user.getUserName());
        tvTagLine.setText(user.getTagLine());
        String string = user.getFollowers() + " Followers";
        tvFollowers.setText(string);
        string = user.getFollowing() + " Following";
        tvFollowing.setText(string);
        if (user.getProfileBannerUrl() != null) {
            Picasso.with(this).load(user.getProfileBannerUrl()).resize(DeviceDimensionsHelper.getDisplayWidth(this), 0).into(crlUserHeader);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {

        }

        return super.onOptionsItemSelected(item);
    }
}
