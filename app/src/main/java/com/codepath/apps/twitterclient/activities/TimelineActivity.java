package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.adapters.TweetPagerAdapter;
import com.codepath.apps.twitterclient.application.TwitterApplication;
import com.codepath.apps.twitterclient.application.TwitterClient;
import com.codepath.apps.twitterclient.fragments.ComposeTweetFragment;
import com.codepath.apps.twitterclient.fragments.TweetsListFragment;
import com.codepath.apps.twitterclient.helpers.NetworkAvailabilityCheck;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnFragmentInteractionListener {

    private TwitterClient client;
    private User loggedInUser;
    private ImageView scrollToTop;

    private ViewPager vpPager;
    private TweetPagerAdapter viewPagerAdapter;
    PagerSlidingTabStrip tabsStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55ACEE")));
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            setupScrollToTopButton(getSupportActionBar());
        }
        // Get the fragment, only if it has not been inflated before
        // if (savedInstanceState == null) {
        //     fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        // }
        client = TwitterApplication.getRestClient();
        // getRateLimit();
        getLoggedInUserInfo();
    }

    private void getRateLimit() {
        client.getRateLimit(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String first = response.toString().substring(0, 4000);
                String second = response.toString().substring(4000, response.toString().length());
                Log.d("FIRST", first);
                Log.d("SECOND", second);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", errorResponse.toString());
            }
        });
    }

    private void setupScrollToTopButton(ActionBar actionBar) {
        scrollToTop = new ImageView(this);
        scrollToTop.setClickable(true);
        scrollToTop.setEnabled(true);
        scrollToTop.setBackgroundResource(R.drawable.ic_twitter_white);
        RelativeLayout relative = new RelativeLayout(this);
        relative.addView(scrollToTop);
        actionBar.setCustomView(relative);
    }

    private void getLoggedInUserInfo() {
        if (isNetworkAvailable()) {
            client.getLoggedInUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("USER", response.toString());
                    loggedInUser = User.fromJSON(response);
                    loadFragments();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("ERROR", errorResponse.toString());
                }
            });
        }
    }

    private void loadFragments() {
        // Get the viewpager and setup a PageChangeListener
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Get the view pager adapter for the pager
        viewPagerAdapter = new TweetPagerAdapter(getSupportFragmentManager(), loggedInUser);
        vpPager.setAdapter(viewPagerAdapter);
        // Find the sliding tabstrips
        tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the view pager
        tabsStrip.setViewPager(vpPager);
        setupPageChangeListener();
    }

    private void setupPageChangeListener() {
        tabsStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                Log.i("AAA", "Page Selected");
                scrollToTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TweetsListFragment) viewPagerAdapter.getRegisteredFragment(position)).scrollToTop();
                    }
                });
            }
        });
    }

    public void onProfileView(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", loggedInUser);
        if (NetworkAvailabilityCheck.isNetworkAvailable(this)) {
            startActivity(i);
        }
    }

    private boolean isNetworkAvailable() {
        return NetworkAvailabilityCheck.isNetworkAvailable(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
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
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetFragment settingsFragment = ComposeTweetFragment.newInstance(loggedInUser.getProfleImageUrl(), false, null, -1);
            settingsFragment.show(fm, "compose_tweet_fragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishComposeTweetFragment(String tweetBody, long replyToTweetId) {
        client.sendTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = Tweet.fromJSON(response);
                ((TweetsListFragment) viewPagerAdapter.getRegisteredFragment(0)).add(0, tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TWEET FAIL", Integer.toString(responseString.length()));
                String first = responseString.toString().substring(0, 4000);
                String second = responseString.toString().substring(4000, responseString.toString().length());
                Log.d("FIRST", first);
                Log.d("SECOND", second);
            }
        });
    }

}
