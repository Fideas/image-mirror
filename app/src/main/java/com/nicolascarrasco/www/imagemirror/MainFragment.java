package com.nicolascarrasco.www.imagemirror;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    Context mContext;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    public MainFragment() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
