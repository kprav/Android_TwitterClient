package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.helpers.DeviceDimensionsHelper;
import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private static Typeface arial;
    private static Typeface times;
    private static Typeface calibri;
    private static Typeface roboto_thin;
    private static Typeface roboto_light;
    private static Typeface roboto_regular;
    private static Typeface roboto_medium;
    private static Typeface roboto_bold;
    private static Typeface roboto_black;

    // View lookup cache for the view holder pattern
    // TODO: Improve viewholder during inflation of rlInlinePhoto
    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvCreationTime;
        TextView tvBody;
        RelativeLayout rlInlinePhoto;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
        arial = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");
        times = Typeface.createFromAsset(context.getAssets(), "fonts/times.ttf");
        calibri = Typeface.createFromAsset(context.getAssets(), "fonts/calibri.ttf");
        roboto_thin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        roboto_light = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        roboto_regular = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        roboto_medium = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        roboto_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        roboto_black = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvCreationTime = (TextView) convertView.findViewById(R.id.tvCreationTime);
            viewHolder.rlInlinePhoto = (RelativeLayout) convertView.findViewById(R.id.rlInlinePhoto);
            viewHolder.tvUserName.setTypeface(roboto_bold);
            viewHolder.tvScreenName.setTypeface(roboto_light);
            viewHolder.tvBody.setTypeface(roboto_regular);
            viewHolder.tvCreationTime.setTypeface(roboto_light);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // RelativeLayout rlInlinePhoto = (RelativeLayout) convertView.findViewById(R.id.rlInlinePhoto);
        // rlInlinePhoto.removeAllViews();

        viewHolder.rlInlinePhoto.removeAllViews();
        viewHolder.tvUserName.setText(tweet.getUser().getUserName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
        viewHolder.tvCreationTime.setText(tweet.getCreatedAt());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfleImageUrl()).into(viewHolder.ivProfileImage);

        if (tweet.getEntity() != null) {
            String mediaUrl = tweet.getEntity().getMedaiUrlLarge();
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inlinePhotoView = layoutInflater.inflate(R.layout.inline_photo, null);
            ImageView ivInlinePhoto = (ImageView) inlinePhotoView.findViewById(R.id.ivInlinePhoto);
            Picasso.with(getContext()).load(mediaUrl).resize(DeviceDimensionsHelper.getDisplayWidth(getContext()), 0).into(ivInlinePhoto);
            // rlInlinePhoto.addView(ivInlinePhoto);
            viewHolder.rlInlinePhoto.addView(ivInlinePhoto);
        } else {
            viewHolder.tvBody.setPadding(0, 0, 10, 5);
        }

        // viewHolder.rlInlinePhoto = rlInlinePhoto;

        return convertView;
    }
}
