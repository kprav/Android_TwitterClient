package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterclient.dao.DbOperations;
import com.codepath.apps.twitterclient.helpers.NetworkAvailabilityCheck;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.TweetType;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class UserTimelineFragment extends TweetsListFragment {
    private boolean status;
    private boolean networkFlag = true;

    public UserTimelineFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        super.user = (User) bundle.getParcelable("user");
        populateTimeline(true);
        return view;
    }

    @Override
    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    public boolean populateTimeline(boolean reset) {
        if (!isNetworkAvailable()) {
            // hideProgressBar();
            if (reset || networkFlag) {
                networkFlag = false;
                NetworkAvailabilityCheck.showToast(getActivity());
            }
            if (reset) {
                addAll(DbOperations.getTweets(TweetType.USER_TIMELINE));
            }
            swipeContainer.setRefreshing(false);
            return false;
        }
        if (reset) {
            clear();
        }
        networkFlag = true;
        client.getUserTimeline(reset, user.getScreenName().substring(1), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                List<Tweet> tweets = Tweet.fromJSONArray(TweetType.USER_TIMELINE, response);
                addAll(tweets);
                // DbOperations.insertTweets(tweets, TweetType.USER_TIMELINE.ordinal());
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
        return NetworkAvailabilityCheck.isNetworkAvailable(getActivity());
    }
}
