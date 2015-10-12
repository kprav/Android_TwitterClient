package com.codepath.apps.twitterclient.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.application.TwitterApplication;
import com.codepath.apps.twitterclient.application.TwitterClient;
import com.codepath.apps.twitterclient.fragments.ComposeTweetFragment;
import com.codepath.apps.twitterclient.helpers.DeviceDimensionsHelper;
import com.codepath.apps.twitterclient.helpers.NetworkAvailabilityCheck;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class TweetDetailActivity extends AppCompatActivity implements ComposeTweetFragment.OnFragmentInteractionListener {

    private Context context;
    private TwitterClient client;
    private ArrayList<Tweet> tweetsList;
    private Tweet tweet;
    private User user;
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

        client = TwitterApplication.getRestClient();
        context = getApplicationContext();

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
        user = getIntent().getParcelableExtra("user");
        tweetsList = getIntent().getParcelableArrayListExtra("tweetsList");

        displayDetailedTweet();
    }

    private void displayDetailedTweet() {
        tvUserNameDetail.setTypeface(roboto_bold);
        tvScreenNameDetail.setTypeface(roboto_light);
        tvTweetDetail.setTypeface(roboto_regular);

        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImageDetail);
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
            String mediaUrl = tweet.getEntity().getMediaUrlLarge();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_retweet) {
            if (!tweet.getRetweeted()) {
                if (NetworkAvailabilityCheck.isNetworkAvailable(this)) {
                    client.reTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.setRetweeted(true);
                            Toast.makeText(context, "Retweeted", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("RETWEET FAIL", errorResponse.toString());
                        }
                    });
                }
            }
        }

        if (id == R.id.action_reply) {
            if (NetworkAvailabilityCheck.isNetworkAvailable(this)) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment settingsFragment = ComposeTweetFragment.newInstance(user.getProfileImageUrl(), true, tweet.getUser().getScreenName(), tweet.getTweetId());
                settingsFragment.show(fm, "compose_tweet_fragment_for_reply");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishComposeTweetFragment(String tweetBody, long replyToTweetId) {
        client.reply(tweetBody, replyToTweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = Tweet.fromJSON(response);
                tweetsList.add(0, tweet);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                Log.d("TWEET FAIL", Integer.toString(responseString.length()));
                String first = responseString.toString().substring(0, 4000);
                String second = responseString.toString().substring(4000, responseString.toString().length());
                Log.d("FIRST", first);
                Log.d("SECOND", second);
                finish();
            }
        });
    }

}
