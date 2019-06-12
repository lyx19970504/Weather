package com.fafu.polutionrepo.finished.ZoomToFullScreenUtil;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.io.ByteArrayOutputStream;

public class FullScreenImageFragment extends DialogFragment {

    @InjectView(R.id.fragment_full_screen_imageView)
    ImageView imageView;
    @InjectView(R.id.fragment_full_screen_top_border)
    View topBorderView;
    @InjectView(R.id.fragment_full_screen_bottom_border)
    View bottomBorderView;
    @InjectView(R.id.fragment_full_screen_left_border)
    View leftBorderView;
    @InjectView(R.id.fragment_full_screen_right_border)
    View rightBorderView;

    private static final String ARGUMENT_BITMAP = "ARGUMENT_BITMAP";
    private static final String ARGUMENT_LOADING_BLUR = "ARGUMENT_LOADING_BLUR";

    // TODO : Handle rotation

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_full_screen_image, null);
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } catch (NullPointerException e) {
            // Do nothing
        }
        Injector.injectInto(this,view);
        initialiseViews(view);

        handleArguments((savedInstanceState != null ) ? savedInstanceState : getArguments());

        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        if (getDialog() != null) {
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap());
            byte[] bytes = Util.writeBitmapIntoByte(bitmap);
            outState.putByteArray(ARGUMENT_BITMAP,bytes);
        }
    }

    public void show(@NonNull final FragmentManager fragmentManager) {
        show(fragmentManager, "FullScreenImageFragment");
    }

    private void initialiseViews(@NonNull final View view) {
        // TODO - I know, this is really ugly. Keep in mind, it's an MVP.


        leftBorderView.setOnDragListener(new DismissibleOnDragListener(new DismissibleOnDragListener.OnDropListener() {
            @Override
            void onDragStarted() {
                imageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrop() {
                dismiss();
            }

            @Override
            void onDragEnded() {
                imageView.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
            }

            @Override
            void onDragLocation(float x, float y) {
                view.setAlpha(x / leftBorderView.getWidth());
            }
        }));

        rightBorderView.setOnDragListener(new DismissibleOnDragListener(new DismissibleOnDragListener.OnDropListener() {
            @Override
            void onDragStarted() {
                imageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrop() {
                dismiss();
            }

            @Override
            void onDragEnded() {
                imageView.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
            }

            @Override
            void onDragLocation(float x, float y) {
                view.setAlpha(1f - x / rightBorderView.getWidth());
            }
        }));

        topBorderView.setOnDragListener(new DismissibleOnDragListener(new DismissibleOnDragListener.OnDropListener() {
            @Override
            void onDragStarted() {
                imageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrop() {
                dismiss();
            }

            @Override
            void onDragEnded() {
                imageView.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
            }

            @Override
            void onDragLocation(float x, float y) {
                view.setAlpha(y / topBorderView.getHeight());
            }
        }));

        bottomBorderView.setOnDragListener(new DismissibleOnDragListener(new DismissibleOnDragListener.OnDropListener() {
            @Override
            void onDragStarted() {
                imageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrop() {
                dismiss();
            }

            @Override
            void onDragEnded() {
                imageView.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
            }

            @Override
            void onDragLocation(float x, float y) {
                view.setAlpha(1f - y / topBorderView.getHeight());
            }
        }));

        imageView.setAdjustViewBounds(true);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        final Point offset = new Point((int) event.getX(), (int) event.getY());

                        final View.DragShadowBuilder shadowBuilder = new DismissibleDragShadowBuilder(imageView, offset);
                        imageView.startDrag(null, shadowBuilder, imageView, 0);
                        return true;
                }

                return false;
            }
        });
    }

    private void handleArguments(@NonNull final Bundle bundle) {
        byte[] bytes = bundle.getByteArray(ARGUMENT_BITMAP);
        final Bitmap bitmap = bytes != null ? BitmapFactory.decodeByteArray(bytes, 0, bytes.length) : null;

        if (bitmap != null) {
            // If you have the final bitmap what's the point of loading behaviour?
            loadBitmap(bitmap);
        }

    }

    private void clearImageView() {
        imageView.setImageDrawable(null);
    }

    private void loadBitmap(@NonNull final Bitmap bitmap) {
        clearImageView();
        imageView.setImageBitmap(bitmap);
    }



    // todo document Bitmap > Url
    public static class Builder {

        private Bitmap bitmap;
        private boolean loadingBlur = true;

        public Builder(@NonNull final Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Builder withLoadingBlur(final boolean loadingBlur) {
            this.loadingBlur = loadingBlur;
            return this;
        }

        public FullScreenImageFragment build() {
            final FullScreenImageFragment fragment = new FullScreenImageFragment();
            final Bundle bundle = new Bundle();
            if (this.bitmap != null) {
                byte[] bytes = Util.writeBitmapIntoByte(this.bitmap);
                bundle.putByteArray(ARGUMENT_BITMAP, bytes);
            }

            bundle.putBoolean(ARGUMENT_LOADING_BLUR, loadingBlur);

            fragment.setArguments(bundle);
            return fragment;
        }
    }


}
