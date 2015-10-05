package com.codepath.apps.twitterclient.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeTweetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComposeTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetFragment extends DialogFragment {
    private OnFragmentInteractionListener mListener;

    private static String profilePicUrl;
    private static boolean isReply;
    private static String replyToUser;
    private static long replyToTweetId;
    private Button btnCancel;
    private ImageView ivProfilePic;
    private EditText etTweet;
    private TextView tvCharCount;
    private Button btnTweet;
    private int tvCharCountColor;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance(String profilePicUrl, boolean isReply, String replyToUser, long replyToTweetId) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        ComposeTweetFragment.profilePicUrl = profilePicUrl;
        ComposeTweetFragment.isReply = isReply;
        ComposeTweetFragment.replyToUser = replyToUser;
        ComposeTweetFragment.replyToTweetId = replyToTweetId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.create_tweet_fragment_theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
        tvCharCountColor = tvCharCount.getCurrentTextColor();
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        setCancelButtonListener();
        setProfilePic();
        setEditTextOnTextChangedListener();
        sendTweet();
        btnTweet.setClickable(false);
        btnTweet.setEnabled(true);
        if (ComposeTweetFragment.isReply) {
            etTweet.setText(replyToUser);
            etTweet.setSelection(etTweet.getText().length());
        }
        return view;
    }

    private void setCancelButtonListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetach();
                dismiss();
            }
        });
    }

    private void setProfilePic() {
        Picasso.with(getActivity()).load(profilePicUrl).into(ivProfilePic);
    }

    private void setEditTextOnTextChangedListener() {
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                int remaining = 140 - length;
                tvCharCount.setText(Integer.toString(remaining));
                if (remaining < 0) {
                    tvCharCount.setTextColor(Color.RED);
                    btnTweet.setClickable(false);
                    btnTweet.setEnabled(false);
                } else if (remaining == 140) {
                    btnTweet.setClickable(false);
                    btnTweet.setEnabled(true);
                } else {
                    tvCharCount.setTextColor(tvCharCountColor);
                    btnTweet.setClickable(true);
                    btnTweet.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendTweet() {
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null)
                    mListener = (OnFragmentInteractionListener) getActivity();
                Tweet tweet = new Tweet();
                mListener.onFinishComposeTweetFragment(etTweet.getText().toString().trim(), replyToTweetId);
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        Log.d("START", "START");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("RESUME", "RESUME");
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d("ATTACH", "ATTACH");
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.d("DETACH", "DETACH");
        mListener = null;
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFinishComposeTweetFragment(String tweetBody, long replyToTweetId);
    }

}
