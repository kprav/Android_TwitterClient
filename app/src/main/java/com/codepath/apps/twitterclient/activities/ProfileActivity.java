package com.codepath.apps.twitterclient.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        populateUserHeader();
    }

    private void populateUserHeader() {
        final CustomRelativeLayout crlUserHeader = (CustomRelativeLayout) findViewById(R.id.crlUserHeader);
        ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        TextView tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvNumTweets = (TextView) findViewById(R.id.tvNumTweets);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfilePic);
        tvProfileName.setText(user.getUserName());
        tvTagLine.setText(user.getTagLine());
        String string = "<b>" + formatNumbers(user.getNumTweets()) + "</b><br /> TWEETS";
        tvNumTweets.setText(Html.fromHtml(string));
        string = "<b>" +  formatNumbers(user.getFollowers()) + "</b><br />  FOLLOWERS";
        tvFollowers.setText(Html.fromHtml(string));
        string = "<b>" +  formatNumbers(user.getFollowing()) + "</b><br />  FOLLOWING";
        tvFollowing.setText(Html.fromHtml(string));
        if (user.getProfileBannerUrl() != null) {
            Picasso.with(this).load(user.getProfileBannerUrl()).resize(DeviceDimensionsHelper.getDisplayWidth(this), 0).into(crlUserHeader);
        }
    }

    private String formatNumbers(int num) {
        Double formattedNum = 0.0;
        String prefix ="K";
        if (num >= 10000) {
            if (num >= 1000000) {
                prefix = "M";
                formattedNum = (double) num / 1000000;
            } else if (num >= 10000) {
                prefix = "K";
                formattedNum = (double) num / 1000;
            }
            formattedNum = (double) Math.round(formattedNum * 100.0) / 100.0;
        } else {
            return Integer.toString(num);
        }
        return Double.toString(formattedNum) + prefix;
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
        return super.onOptionsItemSelected(item);
    }
}
