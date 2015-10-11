package com.codepath.apps.twitterclient.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.twitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitterclient.fragments.TweetsListFragment;
import com.codepath.apps.twitterclient.models.User;

// Return the order of the fragments in the view pager
public class TweetPagerAdapter extends SmartFragmentStatePagerAdapter {
    private String[] tabTitles = {"Name", "Mentions"};
    private User loggedInUser;
    private TweetsListFragment fragment;

    private Bundle setupFragmentArgs() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("loggedInUser", loggedInUser);
        return bundle;
    }

    // Adapter gets the manager that is uses to insert
    // or remove fragments from the activity
    public TweetPagerAdapter(FragmentManager fm, User loggedInUser) {
        super(fm);
        this.loggedInUser = loggedInUser;
    }

    // Controls the order and creation of fragments within the pager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            fragment = new HomeTimelineFragment();
        } else if (position == 1) {
            fragment = new MentionsTimelineFragment();
        } else {
            return null;
        }
        fragment.setArguments(setupFragmentArgs());
        // FragmentManager fragmentManager = getSupportFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.fragment_timeline_frame, fragment).commit();
        return fragment;
    }

    // Return the number of fragments to swipe between
    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // Return the tab title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
