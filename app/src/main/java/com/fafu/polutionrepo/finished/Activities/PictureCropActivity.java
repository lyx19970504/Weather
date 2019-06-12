package com.fafu.polutionrepo.finished.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.View.PictureCropView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureCropActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picturecrop_layout);

        String imagePath = getIntent().getStringExtra("bitmap");

        final PictureCropView pictureCropView = findViewById(R.id.picture_crop_view);
        pictureCropView.setCircleRadius(200 * Util.getDensity(this));
        if(imagePath != null) {
            Bitmap originBitmap = BitmapFactory.decodeFile(imagePath);
            int angle = Util.readPictureDegree(imagePath);
            Bitmap bitmap = Util.rotatingImageView(angle, originBitmap);
            pictureCropView.setPicture(bitmap);
        }

        findViewById(R.id.confirmed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = pictureCropView.cropPicture();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();
                Intent intent = new Intent();
                intent.putExtra("cropBitmap", bytes);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    public static Intent newIntent(Context context,String imagePath){
        Intent intent = new Intent(context,PictureCropActivity.class);
        intent.putExtra("bitmap", imagePath);
        return intent;
    }

}
