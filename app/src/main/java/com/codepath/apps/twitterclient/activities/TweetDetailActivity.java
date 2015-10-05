package com.codepath.apps.twitterclient.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.helpers.DeviceDimensionsHelper;
import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

public class TweetDetailActivity extends AppCompatActivity {

    private Tweet tweet;
    private ImageView ivProfileImageDetail;
    private TextView tvUserNameDetail;
    private TextView tvScreenNameDetail;
    private TextView tvTweetDetail;
    private RelativeLayout rlInlinePhotoDetail;
    private static Typeface roboto_light;
    private static Typeface roboto_regular;
    private static Typeface roboto_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55ACEE")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Tweet");
        }

        ivProfileImageDetail = (ImageView) findViewById(R.id.ivProfileImageDetail);
        tvUserNameDetail = (TextView) findViewById(R.id.tvUserNameDetail);
        tvScreenNameDetail = (TextView) findViewById(R.id.tvScreenNameDetail);
        tvTweetDetail = (TextView) findViewById(R.id.tvTweetDetail);
        rlInlinePhotoDetail = (RelativeLayout) findViewById(R.id.rlInlinePhotoDetail);
        rlInlinePhotoDetail.removeAllViews();

        roboto_light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        roboto_regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        // Get Parcelable data from the intent
        tweet = getIntent().getParcelableExtra("tweet");

        displayDetailedTweet();
    }

    private void displayDetailedTweet() {
        tvUserNameDetail.setTypeface(roboto_bold);
        tvScreenNameDetail.setTypeface(roboto_light);
        tvTweetDetail.setTypeface(roboto_regular);

        Picasso.with(this).load(tweet.getUser().getProfleImageUrl()).into(ivProfileImageDetail);
        tvUserNameDetail.setText(tweet.getUser().getUserName());
        tvScreenNameDetail.setText(tweet.getUser().getScreenName());
        tvTweetDetail.setText(tweet.getBody());

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tweetDetailCreationTimeView = layoutInflater.inflate(R.layout.tweet_detail_formatted_time, null);
        TextView tvCreationTimeDetail = (TextView) tweetDetailCreationTimeView.findViewById(R.id.tvCreationTimeDetail);
        tvCreationTimeDetail.setTypeface(roboto_light);
        tvCreationTimeDetail.setText(tweet.getFormattedCreatedAt());
        rlInlinePhotoDetail.addView(tvCreationTimeDetail);

        if (tweet.getEntity() != null) {
            String mediaUrl = tweet.getEntity().getMedaiUrlLarge();
            View inlinePhotoView = layoutInflater.inflate(R.layout.inline_photo, null);
            ImageView ivInlinePhotoDetail = (ImageView) inlinePhotoView.findViewById(R.id.ivInlinePhoto);
            Picasso.with(this).load(mediaUrl).resize(DeviceDimensionsHelper.getDisplayWidth(this), 0).into(ivInlinePhotoDetail);
            rlInlinePhotoDetail.addView(ivInlinePhotoDetail);

            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.ivInlinePhoto);
            tvCreationTimeDetail.setLayoutParams(params);

        } else {
            tvTweetDetail.setPadding(0, 0, 0, 5);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
