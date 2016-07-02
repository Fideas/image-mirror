package com.nicolascarrasco.www.imagemirror;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nicolascarrasco.www.imagemirror.services.Constants.Constants;
import com.nicolascarrasco.www.imagemirror.services.GetImageIntentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private Context mContext;
    private IntentFilter mIntentFilter;
    private ResponseReceiver mReceiver;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.button_get_image)
    Button mActionButton;

    @BindView(R.id.edit_text_image_url)
    EditText mUrlView;

    @BindView(R.id.image_view_container)
    ImageView mImageView;

    public MainFragment() {
    }

    // Lifecycle functions
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        mReceiver = new ResponseReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        // ProgressBars look terrible on OS before lollipop, this is a work around
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
    }

    // UI gesture listeners
    @SuppressWarnings("unused")
    @OnTextChanged(R.id.edit_text_image_url)
    public void validateUrl(CharSequence url) {
        // Super simple validation for now
        if (url.length() > 0) {
            mActionButton.setEnabled(true);
        } else {
            mActionButton.setEnabled(false);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_get_image)
    public void launchGetImageService() {
        // Change the UI to reflect the task is running
        mProgressBar.setVisibility(View.VISIBLE);
        mActionButton.setEnabled(false);
        mUrlView.setEnabled(false);

        //Start the service that fetches the image
        String url = mUrlView.getText().toString();
        GetImageIntentService.getImage(mContext, url);
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            // Restore the UI
            mProgressBar.setVisibility(View.GONE);
            mActionButton.setEnabled(true);
            mUrlView.setEnabled(true);

            String status = intent.getStringExtra(Constants.EXTENDED_DATA_STATUS);
            switch (status){
                case Constants.SUCCESS:
                    Bitmap image = intent.getParcelableExtra(Constants.EXTENDED_DATA_BITMAP);
                    mImageView.setImageBitmap(image);
                    break;
                case Constants.FAILURE:
                    // Tell the user something is wrong
                    Toast.makeText(mContext, intent.getStringExtra(Constants.EXTENDED_DATA_MESSAGE),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown status");
            }
        }
    }
}
