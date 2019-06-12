package com.fafu.polutionrepo.finished.Activities;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fafu.polutionrepo.finished.Beans.Pollution;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.ZoomToFullScreenUtil.FullScreenImageFragment;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class TopicDetailedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TopicDetailedActivity";
    public static final String PUBLISH_TOPIC_URL = Util.URL_PREFIX + "/green/auth/publishTopic?polId=";
    public static final String UPDATE_PORTRAIT_URL = Util.URL_PREFIX + "/green/auth/upload";

    @InjectView(R.id.confirm)
    Button confirmButton;
    @InjectView(R.id.cancel)
    Button cancelButton;
    @InjectView(R.id.input_text)
    EditText editText;
    @InjectView(R.id.input_title)
    EditText editTitle;
    @InjectView(R.id.image_show)
    ImageView imageView;
    private AlertDialog alertDialog;
    private File outputImage;
    private Uri imageUri;
    private static final String AUTHORITY = "com.fafu.polutionrepo.finished.fileprovider";
    private static final int CAMERA_REQUEST = 0;
    private static final int ALBUM_REQUEST = 1;
    public static final String POLLUTION = "pollution";
    private Pollution mPollution;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_detailed_layout);
        Injector.injectInto(this);
        initViews();
    }

    public void initViews(){
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        editTitle.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editTitle.setSingleLine(false);
        editTitle.setHorizontallyScrolling(false);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setSingleLine(false);
        editText.setHorizontallyScrolling(false);

        mPollution = (Pollution) getIntent().getSerializableExtra(POLLUTION);
        Log.d(TAG, "initViews: "+mPollution.getId());
    }

    public void openDialog(){
        View photoDialog = LayoutInflater.from(this).inflate(R.layout.camera_album, null,false);
        photoDialog.findViewById(R.id.camera).setOnClickListener(this);
        photoDialog.findViewById(R.id.album).setOnClickListener(this);
        TextView title = new TextView(this);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/char.ttf"));
        title.setText("添加预览图");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(title);
        builder.setView(photoDialog);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void closeDialog(){
        if(alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera:
                closeDialog();
                outputImage = new File(getExternalCacheDir(), UUID.randomUUID().toString()+".jpg");
                try {
                    if(outputImage.exists()){
                        boolean m = outputImage.delete();
                        Log.d(TAG, "onClick: "+m);
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imageUri = FileProvider.getUriForFile(this, AUTHORITY, outputImage);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.album:
                closeDialog();
                Intent albumIntent = new Intent(Intent.ACTION_PICK,null);
                albumIntent.setType("image/*");
                startActivityForResult(albumIntent, ALBUM_REQUEST);
                break;
            case R.id.image_show:
                imageView.setDrawingCacheEnabled(true);
                FullScreenImageFragment fullScreenImageFragment =
                        new FullScreenImageFragment.Builder(imageView.getDrawingCache(true)).withLoadingBlur(true).build();
                fullScreenImageFragment.show(getSupportFragmentManager());
                imageView.setDrawingCacheEnabled(false);
                break;
            case R.id.confirm:
                if(TextUtils.isEmpty(editTitle.getText()) || TextUtils.isEmpty(editText.getText()) || imageView.getDrawable() == null){
                    Toast.makeText(this, "信息未完善...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mBitmap != null) {
                    final byte[] bytes = Util.writeBitmapIntoByte(mBitmap);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Util.sendImageRequest(UPDATE_PORTRAIT_URL, bytes, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "onFailure: "+"上传话题图片失败!");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if(response.body() != null){
                                        String imageUrl = response.body().string();
                                        Log.d(TAG, "onResponse: "+imageUrl);
                                        String[] infos = new String[]{editTitle.getText().toString(),editText.getText().toString(),imageUrl};
                                        Util.PublishTopicRequest(PUBLISH_TOPIC_URL + mPollution.getId(),infos,mPollution, new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.d(TAG, "onFailure: " + "发布话题失败!");
                                            }

                                            @Override
                                            public void onResponse(Call call, final Response response) throws IOException {
                                                if(response.body() != null) {
                                                    final String responseText = response.body().string();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(TextUtils.isEmpty(responseText)){
                                                                Toast.makeText(TopicDetailedActivity.this, "发布话题成功!", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Log.d(TAG, "run: "+responseText);
                                                                Toast.makeText(TopicDetailedActivity.this, "发布话题失败!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).start();
                }
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(outputImage.getPath());
                        ExifInterface exifInterface = new ExifInterface(outputImage.getPath());
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                        //判断方向
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            imageView.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getHeight() /4, bitmap.getWidth() /4));
                        } else if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                            imageView.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getWidth() / 4, bitmap.getHeight() / 4));
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    mBitmap = BitmapFactory.decodeFile(outputImage.getPath());
                    Glide.with(this).load(outputImage.getPath()).into(imageView);
                    imageView.setOnClickListener(this);
                    break;
                case ALBUM_REQUEST:
                    if(data != null) {
                        try {
                            String imagePath = handleImageOnKitKat(data);
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            ExifInterface exifInterface = new ExifInterface(imagePath);
                            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,-1);
                            if(data.getData() != null) {
                                if (bitmap.getWidth() > 3000 && bitmap.getHeight() > 3000) {
                                    //判断方向
                                    if(orientation == ExifInterface.ORIENTATION_ROTATE_90){
                                        imageView.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getHeight() / 4, bitmap.getWidth() / 4));
                                    }else if(orientation == ExifInterface.ORIENTATION_NORMAL){
                                        imageView.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getWidth() / 4, bitmap.getHeight() / 4));
                                    }
                                } else if(bitmap.getWidth() == bitmap.getHeight()){
                                    imageView.setLayoutParams(new FrameLayout.LayoutParams(754,754));
                                } else {
                                    imageView.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getWidth() * 2 / 3, bitmap.getHeight() * 2 / 3));
                                }
                            }
                            mBitmap = BitmapFactory.decodeFile(imagePath);
                            Glide.with(this).load(imagePath).into(imageView);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    imageView.setOnClickListener(this);
                    break;
            }

        }
    }

    public String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if(uri.getAuthority().equals("com.android.providers.media.documents")){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if(uri.getAuthority().equals("com.android.providers.download.documents")){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    public String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null,null);
        if(cursor != null){
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    public void choose_image(View view) {
        openDialog();
    }


    public static Intent newIntent(Context context, Pollution pollution){
        Intent intent = new Intent(context,TopicDetailedActivity.class);
        intent.putExtra(POLLUTION,pollution);
        return intent;
    }
}
