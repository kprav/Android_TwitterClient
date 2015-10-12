package com.codepath.apps.twitterclient.dao;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.activeandroid.util.SQLiteUtils;
import com.codepath.apps.twitterclient.models.Entity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.TweetDb;
import com.codepath.apps.twitterclient.models.TweetType;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.models.UserDb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

// TODO: Hitting null pointer exception in constructTweetFromDb occasionally
public class DbOperations {

    private static TreeMap<User, HashSet<Tweet>> userToTweetsMap = new TreeMap<User, HashSet<Tweet>>();;

    private static synchronized void populateUserToTweetMap(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            HashSet<Tweet> tweetList = userToTweetsMap.get(tweet.getUser());
            if (tweetList == null)
                tweetList = new HashSet<Tweet>();
            tweetList.add(tweet);
            userToTweetsMap.put(tweet.getUser(), tweetList);
        }
    }

    // TODO: Also delete images stored to disk
    public static synchronized void clearDatabase() {
        new Delete().from(UserDb.class).execute();
        new Delete().from(TweetDb.class).execute();
    }

    // TODO: Also delete images stored to disk
    public static synchronized void clearDatabase(int tweetType) {
        new Delete().from(TweetDb.class).where("TWEET_TYPE = ?", tweetType).execute();
    }

    public static synchronized int getMaxRemoteIdUser() {
        int maxId = 0;
        String sqlQuery = "select max(remote_id) from users";
        try {
            maxId = SQLiteUtils.intQuery(sqlQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxId;
    }

    public static synchronized int getMaxRemoteIdTweet() {
        int maxId = 0;
        String sqlQuery = "select max(remote_id) from tweets";
        try {
            maxId = SQLiteUtils.intQuery(sqlQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxId;
    }

    public static synchronized UserDb createUserDbObject(User user) {
        int remoteId = getMaxRemoteIdUser() + 1;
        return new UserDb(remoteId, user);
    }

    public static synchronized boolean userExistsInDb(User user) {
        boolean exists = true;
        List<UserDb> userFromDb = new Select()
                .from(UserDb.class)
                .where("SCREEN_NAME = ?", user.getScreenName())
                .execute();
        if (userFromDb == null || userFromDb.size() == 0) {
            exists = false;
        }
        return exists;
    }

    public static synchronized UserDb insertUserToDatabase(User user) {
        UserDb userDb = null;
        if (!userExistsInDb(user)) {
            userDb = createUserDbObject(user);
            userDb.save();
        }
        return userDb;
    }

    public static synchronized boolean tweetExistsInDb(Tweet tweet) {
        boolean exists = true;
        List<TweetDb> tweetFromDb = new Select()
                .from(TweetDb.class)
                .where("TWEET_ID = ?", tweet.getTweetId())
                .execute();
        if (tweetFromDb == null || tweetFromDb.size() == 0) {
            exists = false;
        }
        return exists;
    }

    public static synchronized void updateTweetInDb(Tweet tweet) {
        if (tweet.getEntity() != null)
            new Update(TweetDb.class)
                    .set("mediaUrlLargeDisk = ?", tweet.getEntity().getMediaUrlLargeDisk())
                    .where("tweet_id = ?", tweet.getTweetId())
                    .execute();
    }

    public static synchronized void insertTweetsToDatabase() {
        String userName = "TEST";
        try {
            Iterator it = userToTweetsMap.entrySet().iterator();
            Set<User> users = userToTweetsMap.keySet();
            int remoteId = getMaxRemoteIdTweet();
            for (User user : users) {
                userName = user.getUserName();
                UserDb userDb = insertUserToDatabase(user);
                HashSet<Tweet> tweets = userToTweetsMap.get(user);
                for (Tweet tweet : tweets) {
                    if (!tweetExistsInDb(tweet)) {
                        TweetDb tweetDb = new TweetDb(++remoteId, tweet, userDb);
                        tweetDb.save();
                    } else {
                        // updateTweetInDb(tweet);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Exception", userName);
        }
    }

    public static synchronized List<TweetDb> getTweetsFromDb(TweetType tweetType) {
        return new Select()
                .from(TweetDb.class)
                .where("TWEET_TYPE = ?", tweetType.ordinal())
                .execute();
    }

    public static synchronized Tweet constructTweetFromDb(TweetDb tweetDb) {
        User user = new User();
        Tweet tweet = new Tweet();
        Entity entity = null;
        UserDb userDb = tweetDb.user;

        user.setUserId(userDb.userId);
        user.setUserName(userDb.userName);
        user.setScreenName(userDb.screenName);
        user.setProfileImageUrl(userDb.profileImageUrl);
        user.setProfileImageUrlDisk(userDb.profileImageUrlDisk);
        user.setProfileBannerUrl(userDb.profileBannerUrl);
        user.setProfileImageUrlDisk(userDb.profileBannerUrlDisk);
        user.setTagLine(userDb.tagLine);
        user.setFollowers(userDb.followers);
        user.setFollowing(userDb.following);
        user.setNumTweets(userDb.numTweets);

        if (tweetDb.mediaUrlLarge != null && !tweetDb.mediaUrlLarge.equals("")) {
            entity = new Entity();
            entity.setMediaType(tweetDb.mediaType);
            entity.setMediaUrl(tweetDb.mediaUrl);
            entity.setMediaUrlThumb(tweetDb.mediaUrlThumb);
            entity.setMediaUrlSmall(tweetDb.mediaUrlSmall);
            entity.setMediaUrlMedium(tweetDb.mediaUrlMedium);
            entity.setMediaUrlLarge(tweetDb.mediaUrlLarge);
            entity.setMediaUrlDisk(tweetDb.mediaUrlDisk);
            entity.setMediaUrlThumbDisk(tweetDb.mediaUrlThumbDisk);
            entity.setMediaUrlSmallDisk(tweetDb.mediaUrlSmallDisk);
            entity.setMediaUrlMediumDisk(tweetDb.mediaUrlMediumDisk);
            entity.setMediaUrlLargeDisk(tweetDb.mediaUrlLargeDisk);
        }

        tweet.setTweetId(tweetDb.tweetId);
        tweet.setTweetType(TweetType.values()[tweetDb.tweetType]);
        tweet.setBody(tweetDb.body);
        tweet.setCreatedAt(tweetDb.createdAt);
        tweet.setFormattedCreatedAt(tweetDb.formattedCreatedAt);
        tweet.setRelativeCreationTime(tweetDb.relativeCreationTime);
        tweet.setFavorited(tweetDb.favorited == 'Y');
        tweet.setRetweeted(tweetDb.retweeted == 'Y');
        tweet.setRetweetCount(tweetDb.retweetCount);

        tweet.setUser(user);
        tweet.setEntity(entity);
        return tweet;
    }

    public static synchronized void insertTweet(Tweet tweet) {
        userToTweetsMap = new TreeMap<User, HashSet<Tweet>>();
        HashSet<Tweet> tweets = new HashSet<Tweet>();
        tweets.add(tweet);
        userToTweetsMap.put(tweet.getUser(), tweets);
        insertTweetsToDatabase();
    }

    public static synchronized void insertTweets(List<Tweet> tweets, int tweetType) {
        populateUserToTweetMap(tweets);
        // clearDatabase(tweetType);
        insertTweetsToDatabase();
    }

    public static synchronized ArrayList<Tweet> getTweets(TweetType tweetType) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        try {
            List<TweetDb> tweetsDbs = getTweetsFromDb(tweetType);
            for (TweetDb tweetDb : tweetsDbs) {
                Tweet tweet = constructTweetFromDb(tweetDb);
                tweets.add(tweet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tweets;
    }
}
