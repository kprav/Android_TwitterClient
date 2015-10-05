package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable {
    private Long userId;
    private String userName;
    private String screenName;
    private String profleImageUrl;

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfleImageUrl() {
        return profleImageUrl;
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
            u.profleImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.screenName);
        dest.writeString(this.profleImageUrl);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.userName = in.readString();
        this.screenName = in.readString();
        this.profleImageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
