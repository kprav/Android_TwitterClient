package com.codepath.apps.twitterclient.application;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "FqTTRjDXwALWDy2UET2OQoXwG";       // Change this
    public static final String REST_CONSUMER_SECRET = "nkr3NLwhLNgFPoVNypFnXiwRo5BzIe0NZ4cIm75msQhPsBcK8k"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://kpravtweets"; // Change this (here and in manifest)
    public static long MAX_TWEET_ID_HOME = 0;
    public static long MAX_TWEET_ID_MENTIONS = 0;
    public static long MAX_TWEET_ID_USER = 0;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
    // Get tweets for the timeline
    public void getHomeTimeline(boolean reset, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        params.put("count", 50);
        params.put("since_id", 1);
        params.put("include_entities", true);
        if (!reset)
            params.put("max_id", MAX_TWEET_ID_HOME + 1);
        else
            MAX_TWEET_ID_HOME = 0;
        // Execute the request
        getClient().get(apiUrl, params, handler);
    }

    // Get mentions tweets for the timeline
    public void getMentionsTimeline(boolean reset, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        params.put("count", 50);
        params.put("include_entities", true);
        if (!reset)
            params.put("max_id", MAX_TWEET_ID_MENTIONS + 1);
        else
            MAX_TWEET_ID_MENTIONS = 0;
        // Execute the request
        getClient().get(apiUrl, params, handler);
    }

    // Get the tweets tweeted by a specific user
    public void getUserTimeline(boolean reset, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        // Specify the params
        RequestParams params = new RequestParams();
        params.put("count", 50);
        params.put("screen_name", screenName);
        params.put("include_entities", true);
        if (!reset)
            params.put("max_id", MAX_TWEET_ID_USER + 1);
        else
            MAX_TWEET_ID_USER = 0;
        // Execute the request
        getClient().get(apiUrl, params, handler);
    }

    // Get Logged in user information
    public void getLoggedInUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, handler);
    }

    // Send Tweet
    public void sendTweet(String status, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        getClient().post(apiUrl, params, handler);
    }

    // Retweet
    public void reTweet(long tweetId, AsyncHttpResponseHandler handler) {
        String subUrl = "statuses/retweet/" + tweetId + ".json";
        String apiUrl = getApiUrl(subUrl);
        getClient().post(apiUrl, handler);
    }

    // Retweet
    public void reply(String status, long inReplyToStatusId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        params.put("in_reply_to_status_id", inReplyToStatusId);
        getClient().post(apiUrl, params, handler);
    }

    public void getRateLimit(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("application/rate_limit_status.json");
        getClient().get(apiUrl, handler);
    }

    // Compose Tweet
}
