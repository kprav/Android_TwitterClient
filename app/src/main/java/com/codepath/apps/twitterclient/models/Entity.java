package com.codepath.apps.twitterclient.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Entity {
    private String mediaUrl;
    private String mediaUrlThumb;
    private String mediaUrlSmall;
    private String mediaUrlMedium;
    private String mediaUrlLarge;
    private String mediaType;

    public void setMediaUrlThumb(String mediaUrlThumb) {
        this.mediaUrlThumb = mediaUrlThumb;
    }

    public void setMediaUrlSmall(String mediaUrlSmall) {
        this.mediaUrlSmall = mediaUrlSmall;
    }

    public void setMediaUrlMedium(String mediaUrlMedium) {
        this.mediaUrlMedium = mediaUrlMedium;
    }

    public void setMedaiUrlLarge(String mediaUrlLarge) {
        this.mediaUrlLarge = mediaUrlLarge;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaUrlThumb() {
        return mediaUrlThumb;
    }

    public String getMediaUrlSmall() {
        return mediaUrlSmall;
    }

    public String getMediaUrlMedium() {
        return mediaUrlMedium;
    }

    public String getMedaiUrlLarge() {
        return mediaUrlLarge;
    }

    public String getMediaType() {
        return mediaType;
    }

    public static Entity fromJSON(JSONObject jsonObject) {
        Entity e = null;
        if (jsonObject != null) {
            try {
                if (jsonObject.optJSONArray("media") != null) {
                    JSONArray jsonArray = jsonObject.getJSONArray("media");
                    JSONObject mediaJSON = jsonArray.getJSONObject(0);
                    if (mediaJSON.optString("media_url") != null) {
                        e = new Entity();
                        e.mediaUrl = mediaJSON.getString("media_url");
                        e.mediaUrlThumb = e.mediaUrl + ":thumb";
                        e.mediaUrlSmall = e.mediaUrl + ":small";
                        e.mediaUrlMedium = e.mediaUrl + ":medium";
                        e.mediaUrlLarge = e.mediaUrl + ":large";
                        e.mediaType = "photo"; // currently twitter returns only photos
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return e;
    }
}
