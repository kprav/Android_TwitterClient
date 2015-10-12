package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable, Comparable<User> {
    private long userId;
    private String userName;
    private String screenName;
    private String profileImageUrl;
    private String profileImageUrlDisk;
    private String profileBannerUrl;
    private String profileBannerUrlDisk;
    private String tagLine;
    private int followers;
    private int following;
    private int numTweets;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setProfileImageUrlDisk(String profileImageUrlDisk) {
        this.profileImageUrlDisk = profileImageUrlDisk;
    }

    public void setProfileBannerUrl(String profileBannerUrl) {
        this.profileBannerUrl = profileBannerUrl;
    }

    public void setProfileBannerUrlDisk(String profileBannerUrlDisk) {
        this.profileBannerUrlDisk = profileBannerUrlDisk;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public void setNumTweets(int numTweets) {
        this.numTweets = numTweets;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        if (profileImageUrl != null)
            return profileImageUrl;
        else
            return "";
    }

    public String getProfileImageUrlDisk() {
        if (profileImageUrlDisk != null)
            return profileImageUrlDisk;
        else
            return "";
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public String getProfileBannerUrlDisk() {
        if (profileBannerUrlDisk != null)
            return profileBannerUrlDisk;
        else
            return "";
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public int getNumTweets() {
        return numTweets;
    }

    // Deserialize the JSON
    //  - Extract values from JSON
    //  - Construct the User Object and return it
    public static User fromJSON(JSONObject jsonObject) {
        User u = new User();
        try {
            // TODO: Check for null
            u.userId = jsonObject.getLong("id");
            u.userName = jsonObject.getString("name");
            u.screenName = "@" + jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
            u.tagLine = jsonObject.getString("description");
            u.followers = jsonObject.getInt("followers_count");
            u.following = jsonObject.getInt("friends_count");
            u.numTweets = jsonObject.getInt("statuses_count");
            if (!jsonObject.optString("profile_banner_url").equals(""))
                u.profileBannerUrl = jsonObject.getString("profile_banner_url");
            else
                u.profileBannerUrl = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.profileImageUrlDisk);
        dest.writeString(this.profileBannerUrl);
        dest.writeString(this.profileBannerUrlDisk);
        dest.writeString(this.tagLine);
        dest.writeInt(this.followers);
        dest.writeInt(this.following);
        dest.writeInt(this.numTweets);
    }

    protected User(Parcel in) {
        this.userId = in.readLong();
        this.userName = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.profileImageUrlDisk = in.readString();
        this.profileBannerUrl = in.readString();
        this.profileBannerUrlDisk = in.readString();
        this.tagLine = in.readString();
        this.followers = in.readInt();
        this.following = in.readInt();
        this.numTweets = in.readInt();
    }

    public static final Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int compareTo(User another) {
        if (this.screenName.equals(another.screenName))
            return 0;
        else
            return 1;
    }
}
