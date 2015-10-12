package com.codepath.apps.twitterclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.TweetDetailActivity;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.application.TwitterApplication;
import com.codepath.apps.twitterclient.application.TwitterClient;
import com.codepath.apps.twitterclient.dao.DbOperations;
import com.codepath.apps.twitterclient.helpers.EndlessScrollListener;
import com.codepath.apps.twitterclient.helpers.NetworkAvailabilityCheck;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public abstract class TweetsListFragment extends Fragment {

    private TreeMap<User, Boolean> userStorageMap = new TreeMap<User, Boolean>();
    private ArrayList<Tweet> tweetsList;
    private TweetsArrayAdapter tweetsAdapter;
    private ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected TwitterClient client;
    protected User user;

    // Inflation Logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        lvTweets = (ListView) view.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(tweetsAdapter);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        setupListViewListeners();
        setupSwipeRefresh();
        return view;
    }

    // Creation LifeCycle Event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweetsList = new ArrayList<Tweet>();
        tweetsAdapter = new TweetsArrayAdapter(getActivity(), tweetsList);
        client = TwitterApplication.getRestClient();
    }

    public abstract boolean populateTimeline(boolean reset);

    public void add(int position, Tweet tweet) {
        tweetsList.add(position, tweet);
        tweetsAdapter.notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweets) {
        tweetsAdapter.addAll(tweets);
    }

    public void scrollToTop() {
        lvTweets.smoothScrollToPosition(0);
    }

    public void clear() {
        tweetsAdapter.clear();
        if (tweetsList.size() > 0)
            DbOperations.clearDatabase(tweetsList.get(0).getTweetType().ordinal());
    }

    public void setupSwipeRefresh() {
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

    public void setupListViewListeners() {
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = tweetsList.get(position);
                Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                intent.putExtra("tweet", tweet);
                intent.putExtra("user", user);
                intent.putExtra("tweetsList", tweetsList);
                if (NetworkAvailabilityCheck.isNetworkAvailable(getActivity())) {
                    startActivity(intent);
                    // try {
                    //     Thread.sleep(1000);
                    // } catch (InterruptedException e) {
                    //     e.printStackTrace();
                    // }
                    // populateTimeline(true);
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

    // TODO: Remove this if not necessary
    public void saveTweetsInDatabase(List<Tweet> tweets) {
        File filesDir = getActivity().getFilesDir();
        for (Tweet tweet : tweets) {
            String profileImageUrl = tweet.getUser().getProfileImageUrl();
            String profileBannerUrl = tweet.getUser().getProfileBannerUrl();
            String mediaUrlLarge = (tweet.getEntity() != null) ? tweet.getEntity().getMediaUrlLarge() : null;
            File profileImage = null;
            File bannerImage = null;
            File mediaImage = null;
            if (!userStorageMap.get(tweet.getUser())) {
                if (profileImageUrl != null && !profileImageUrl.equals("")) {
                    profileImage = new File(filesDir, tweet.getUser().getScreenName() + "_profile");
                }
                if (profileBannerUrl != null && !profileBannerUrl.equals("")) {
                     bannerImage = new File(filesDir, tweet.getUser().getScreenName() + "_banner");
                }
            }
            if (mediaUrlLarge != null && !mediaUrlLarge.equals("")) {
                mediaImage = new File(filesDir, tweet.getTweetId() + "_media");
            }
        }
    }

}
