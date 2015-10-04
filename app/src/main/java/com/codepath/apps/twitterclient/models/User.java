package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
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
}
