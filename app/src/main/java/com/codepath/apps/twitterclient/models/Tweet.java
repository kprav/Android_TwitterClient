package com.codepath.apps.twitterclient.models;

import com.codepath.apps.twitterclient.application.TwitterClient;
import com.codepath.apps.twitterclient.helpers.DateUtilites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Parse the JSON + Store the data
// Encapsulate state logic or display logic
public class Tweet {

    // List out the attributes
    private User user;
    private long tweetId;
    private String body;
    private String createdAt;

    public void setUser(User user) {
        this.user = user;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public long getTweetId() {
        return tweetId;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Deserialize the JSON
    //  - Extract values from JSON
    //  - Construct the Tweet Object and return it
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            // TODO: Check for null
            tweet.body = jsonObject.getString("text");
            tweet.tweetId = jsonObject.getLong("id");
            tweet.createdAt = DateUtilites.getRelativeTime(jsonObject.getString("created_at"));
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    // Pass in an array of JSON items and return a list of tweets
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < jsonArray.length(); i++) {
            // TODO: check for null
            try {
                JSONObject tweetsJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetsJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
                TwitterClient.MAX_TWEET_ID = tweet.getTweetId();
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return tweets;
    }
}
