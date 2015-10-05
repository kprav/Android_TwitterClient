package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Entity implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaUrl);
        dest.writeString(this.mediaUrlThumb);
        dest.writeString(this.mediaUrlSmall);
        dest.writeString(this.mediaUrlMedium);
        dest.writeString(this.mediaUrlLarge);
        dest.writeString(this.mediaType);
    }

    public Entity() {
    }

    protected Entity(Parcel in) {
        this.mediaUrl = in.readString();
        this.mediaUrlThumb = in.readString();
        this.mediaUrlSmall = in.readString();
        this.mediaUrlMedium = in.readString();
        this.mediaUrlLarge = in.readString();
        this.mediaType = in.readString();
    }

    public static final Parcelable.Creator<Entity> CREATOR = new Parcelable.Creator<Entity>() {
        public Entity createFromParcel(Parcel source) {
            return new Entity(source);
        }

        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
}
