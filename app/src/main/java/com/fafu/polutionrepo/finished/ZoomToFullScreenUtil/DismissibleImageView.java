package com.fafu.polutionrepo.finished.ZoomToFullScreenUtil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

public class DismissibleImageView extends android.support.v7.widget.AppCompatImageView implements View.OnClickListener {

    private boolean blurOnLoading = true; // Defaults to true
    @Nullable private FragmentManager fragmentManager;

    public DismissibleImageView(Context context) {
        this(context, null);
    }

    public DismissibleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DismissibleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener(this);
        setDrawingCacheEnabled(true); // todo remember to clear it on set image
        setAdjustViewBounds(true);
    }


    //开启毛玻璃效果
    public void blurOnLoading(final boolean blurOnLoading) {
        this.blurOnLoading = blurOnLoading;
    }

    public void setSupportFragmentManager(@NonNull final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = null;

        if (this.fragmentManager != null) {
            fragmentManager = this.fragmentManager;
        } else if (getContext() instanceof FragmentActivity) {
            fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        }

        if (fragmentManager != null) {

            FullScreenImageFragment fragment;


            fragment = new FullScreenImageFragment.Builder(getDrawingCache(true))
                    .withLoadingBlur(blurOnLoading).build();


            fragment.show(fragmentManager);
        }
    }
}
