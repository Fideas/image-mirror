package com.nicolascarrasco.www.imagemirror;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.nicolascarrasco.www.imagemirror.services.GetImageIntentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    Context mContext;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.button_get_image)
    Button mActionButton;

    @BindView(R.id.edit_text_image_url)
    EditText mUrlView;

    public MainFragment() {
    }

    // Lifecycle functions
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
}
