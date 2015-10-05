package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.application.TwitterApplication;
import com.codepath.apps.twitterclient.application.TwitterClient;
import com.codepath.apps.twitterclient.fragments.ComposeTweetFragment;
import com.codepath.apps.twitterclient.helpers.EndlessScrollListener;
import com.codepath.apps.twitterclient.helpers.NetworkAvailabilityCheck;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnFragmentInteractionListener {

    private SwipeRefreshLayout swipeContainer;
    private TwitterClient client;
    private User loggedInUser;
    private ArrayList<Tweet> tweetsList;
    private TweetsArrayAdapter tweetsAdapter;
    private ListView lvTweets;
    private boolean status;

    private ImageView scrollToTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55ACEE")));
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            setupScrollToTop(getSupportActionBar());
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweetsList = new ArrayList<Tweet>();
        tweetsAdapter = new TweetsArrayAdapter(this, tweetsList);
        lvTweets.setAdapter(tweetsAdapter);

        client = TwitterApplication.getRestClient();
//        getRateLimit();
        setupListView();
        setupSwipeRefresh();
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
                status = false;
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", errorResponse.toString());
                status = false;
            }
        });
    }

    private void setupScrollToTop(ActionBar actionBar) {
        scrollToTop = new ImageView(this);
        scrollToTop.setClickable(true);
        scrollToTop.setEnabled(true);
        scrollToTop.setBackgroundResource(R.drawable.ic_twitter_white);
        ;
        RelativeLayout relative = new RelativeLayout(this);
        relative.addView(scrollToTop);
        actionBar.setCustomView(relative);
        scrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvTweets.smoothScrollToPosition(0);
            }
        });
    }

    private void setupListView() {
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = tweetsList.get(position);
                Intent intent = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                intent.putExtra("tweet", tweet);
                intent.putExtra("loggedInUser", loggedInUser);
                intent.putExtra("tweetsList", tweetsList);
                if (isNetworkAvailable()) {
                    startActivity(intent);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    populateTimeline(true);
                }
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            // Triggered only when new data needs to be appended to the list
            // Load more data for paginating and append the new data items to the adapter.
            // Use the page/totalItemsCount value to retrieve paginated data.
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount <= 800) {
                    // Twitter returns about 800 tweets for the home timeline.
                    // We query 50 results with each call.

                    Log.d("totalItemsCount", Integer.toString(totalItemsCount));

                    return (populateTimeline(false));

                    // True ONLY if more data is actually being loaded; false otherwise.
                    // return true;
                }
                return false;
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Make sure to call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void getLoggedInUserInfo() {
        if (isNetworkAvailable()) {
            client.getLoggedInUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("USER", response.toString());
                    loggedInUser = User.fromJSON(response);
                    populateTimeline(true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("ERROR", errorResponse.toString());
                }
            });
        }
    }

    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    private boolean populateTimeline(boolean reset) {
        if (!isNetworkAvailable()) {
            // hideProgressBar();
            swipeContainer.setRefreshing(false);
            return false;
        }
        if (reset) {
            tweetsAdapter.clear();
        }
        client.getHomeTimeline(reset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                tweetsAdapter.addAll(Tweet.fromJSONArray(response));
                status = true;
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", errorResponse.toString());
                status = false;
            }
        });
        swipeContainer.setRefreshing(false);
        return status;
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
                tweetsList.add(0, tweet);
                tweetsAdapter.notifyDataSetChanged();
                populateTimeline(true);
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
