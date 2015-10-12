package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "USERS")
public class UserDb extends Model {

    @Column(name = "REMOTE_ID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long remoteId;

    @Column(name = "USER_ID")
    public long userId;

    @Column(name = "USER_NAME")
    public String userName;

    @Column(name = "SCREEN_NAME")
    public String screenName;

    @Column(name = "PROFILE_IMAGE_URL")
    public String profileImageUrl;

    @Column(name = "PROFILE_IMAGE_URL_DISK")
    public String profileImageUrlDisk;

    @Column(name = "PROFILE_BANNER_URL")
    public String profileBannerUrl;

    @Column(name = "PROFILE_BANNER_URL_DISK")
    public String profileBannerUrlDisk;

    @Column(name = "TAG_LINE")
    public String tagLine;

    @Column(name = "FOLLOWERS")
    public int followers;

    @Column(name = "FOLLOWING")
    public int following;

    @Column(name = "NUM_TWEETS")
    public int numTweets;

    public UserDb() {
        super();
    }

    public UserDb(long remoteId, long userId, String userName, String screenName, String profileImageUrl,
                  String profileImageUrlDisk, String profileBannerUrl, String profileBannerUrlDisk,
                  String tagLine, int followers, int following, int numTweets) {
        this.remoteId = remoteId;
        this.userId = userId;
        this.userName = userName;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlDisk = profileImageUrlDisk;
        this.profileBannerUrl = profileBannerUrl;
        this.profileBannerUrlDisk = profileBannerUrlDisk;
        this.tagLine = tagLine;
        this.followers = followers;
        this.following = following;
        this.numTweets = numTweets;
    }

    public UserDb(long remoteId, User user) {
        this(remoteId,
                user.getUserId(),
                user.getUserName(),
                user.getScreenName(),
                user.getProfileImageUrl(),
                user.getProfileImageUrlDisk(),
                user.getProfileBannerUrl(),
                user.getProfileBannerUrlDisk(),
                user.getTagLine(),
                user.getFollowers(),
                user.getFollowing(),
                user.getNumTweets());
    }

    // Used to return items from another table based on the foreign key
    public List<TweetDb> getTweetsInDb() {
        return getMany(TweetDb.class, "UserDb");
    }

}
