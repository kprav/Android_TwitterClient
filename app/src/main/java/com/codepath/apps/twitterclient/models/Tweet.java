package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.twitterclient.application.TwitterClient;
import com.codepath.apps.twitterclient.helpers.DateUtilites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Parse the JSON + Store the data
// Encapsulate state logic or display logic
public class Tweet implements Parcelable {

    // List out the attributes
    private User user;
    private Entity entity;
    private long tweetId;
    private String body;
    private String createdAt;
    private String formattedCreatedAt;
    private String relativeCreationTime;
    private boolean favorited;
    private boolean retweeted;
    private int retweetCount;

    public void setUser(User user) {
        this.user = user;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
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

    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }

    public void setRelativeCreationTime(String relativeCreationTime) {
        this.relativeCreationTime = relativeCreationTime;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public User getUser() {
        return user;
    }

    public Entity getEntity() {
        return entity;
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

    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }

    public String getRelativeCreationTime() {
        return relativeCreationTime;
    }

    public boolean getFavorited() {
        return favorited;
    }

    public boolean getRetwweted() {
        return retweeted;
    }

    public int getRetweetCount() {
        return retweetCount;
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
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.formattedCreatedAt = DateUtilites.getFormattedTime(jsonObject.getString("created_at"));
            tweet.relativeCreationTime = DateUtilites.getRelativeTime(jsonObject.getString("created_at"));
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.entity = Entity.fromJSON(jsonObject.getJSONObject("entities"));
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, 0);
        dest.writeParcelable(this.entity, 0);
        dest.writeLong(this.tweetId);
        dest.writeString(this.body);
        dest.writeString(this.createdAt);
        dest.writeString(this.formattedCreatedAt);
        dest.writeString(this.relativeCreationTime);
        dest.writeByte(favorited ? (byte) 1 : (byte) 0);
        dest.writeByte(retweeted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.retweetCount);
    }

    public Tweet() {
    }

    protected Tweet(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.entity = in.readParcelable(Entity.class.getClassLoader());
        this.tweetId = in.readLong();
        this.body = in.readString();
        this.createdAt = in.readString();
        this.formattedCreatedAt = in.readString();
        this.relativeCreationTime = in.readString();
        this.favorited = in.readByte() != 0;
        this.retweeted = in.readByte() != 0;
        this.retweetCount = in.readInt();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
