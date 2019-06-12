package com.fafu.polutionrepo.finished.Activities;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.Fragments.MyInfoFragment;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.ZoomToFullScreenUtil.FullScreenImageFragment;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.fafu.polutionrepo.finished.Fragments.MyInfoFragment.IMAGE_PREFIX;

public class MyDetailInfoActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener {

    public static final String UPDATE_PORTRAIT_URL = Util.URL_PREFIX + "/green/auth/upload";
    private static final String TAG = "MyDetailInfoActivity";
    private CircleImageView circleImageView;

    private File outputImage;
    private Uri imageUri;
    private AlertDialog alertDialog;
    private static final String AUTHORITY = "com.fafu.polutionrepo.finished.fileprovider";
    public static final String IMAGE_PATH = "image_path";
    private static final int CAMERA_REQUEST = 0;
    private static final int ALBUM_REQUEST = 1;
    private static final int CROP_IMAGE = 2;
    private Bitmap bitmap;
    private ImageView CacheImageView;
    private LinearLayout mListView;
    private TextView nickName;
    private String[] info = new String[]{"用户名","手机","邮箱","生日"};
    private int[] iconId = new int[]{R.drawable.username,R.drawable.phone_icon,R.drawable.mail,R.drawable.icon_birthday};
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_detail_info);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        findViewById(R.id.back).setOnClickListener(this);
        mListView = findViewById(R.id.my_detailed_info);
        user = MyApplication.getUser();
        if(user != null) {
            String[] msg = new String[]{user.getUsername(),user.getPhone(),user.getEmail(),user.getBirthday()};
            nickName = findViewById(R.id.nickname);
            nickName.setText(user.getNickname());
            nickName.setOnClickListener(this);
            View view;
            for(int i=0;i<info.length;i++) {
                view = LayoutInflater.from(this).inflate(R.layout.my_detailed_info_item, null, true);
                if(i == 0){
                    view.findViewById(R.id.right).setVisibility(View.GONE);            //用户名无法修改
                }
                view.setId(i);
                view.setOnClickListener(this);
                TextView textView = view.findViewById(R.id.item_info);
                textView.setText(info[i]);
                TextView textView2 = view.findViewById(R.id.item_msg);
                textView2.setText(msg[i]);
                ImageView imageView = view.findViewById(R.id.item_icon);
                imageView.setImageResource(iconId[i]);
                mListView.addView(view);
            }
        }
        CacheImageView = findViewById(R.id.cache_image);
        circleImageView = findViewById(R.id.portrait_icon);
        circleImageView.setOnClickListener(this);
        circleImageView.setOnLongClickListener(this);

        String path  = getIntent().getStringExtra(IMAGE_PATH);
        if(!TextUtils.isEmpty(path)){
            Glide.with(this).load(IMAGE_PREFIX + user.getImageName()).into(circleImageView);
            Glide.with(this).load(IMAGE_PREFIX + user.getImageName()).into(CacheImageView);
        }else{
            circleImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.huaji));
            CacheImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.huaji));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                if(bitmap != null) {
                    Intent intent = new Intent();
                    byte[] bytes = Util.writeBitmapIntoByte(bitmap);
                    intent.putExtra("bitmap", bytes);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.portrait_icon:
                CacheImageView.setDrawingCacheEnabled(true);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FullScreenImageFragment fullScreenImageFragment =
                        new FullScreenImageFragment.Builder(CacheImageView.getDrawingCache(true)).withLoadingBlur(true).build();
                fullScreenImageFragment.show(fragmentManager);
                CacheImageView.setDrawingCacheEnabled(false);
                break;
            case R.id.camera:
                closeDialog();
                outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(this, AUTHORITY, outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
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
            case R.id.nickname:
                openDialog("修改昵称",v);
                break;
            case 0:
                Toast.makeText(this, "用户名无法更改!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                openDialog("修改手机号",v);
                break;
            case 2:
                openDialog("修改邮箱",v);
                break;
            case 3:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    cropImage(outputImage.getPath());
                    break;
                case ALBUM_REQUEST:
                    handleImageOnKitKat(data);
                    break;
                case CROP_IMAGE:
                    byte[] bytes = data.getByteArrayExtra("cropBitmap");
                    if(bytes != null) {
                        Util.sendImageRequest(UPDATE_PORTRAIT_URL, bytes, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: " + "更新头像失败!");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseText = response.body().string();
                                if (responseText != null) {
                                    user.setImageName(responseText);
                                    Util.updateUserRequest(MyInfoFragment.UPDATE_URL, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.d(TAG, "onFailure: "+"更新头像失败!");
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if(response.body() != null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(MyDetailInfoActivity.this, "更新成功!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if(bitmap != null){
                        CacheImageView.setImageBitmap(bitmap);
                        circleImageView.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }

    public void openDialog(){
        View photoDialog = LayoutInflater.from(this).inflate(R.layout.camera_album, null,false);
        photoDialog.findViewById(R.id.camera).setOnClickListener(this);
        photoDialog.findViewById(R.id.album).setOnClickListener(this);
        TextView title = new TextView(this);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/char.ttf"));
        title.setText("修改头像");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(title);
        builder.setView(photoDialog);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void openDialog(final String info,final View v){
        View view = LayoutInflater.from(this).inflate(R.layout.personal_info_modify, null,false);
        TextView title = view.findViewById(R.id.title_info);
        title.setText(info);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = view.findViewById(R.id.modify_info);
        builder.setView(view);
        builder.setPositiveButton("OK", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                switch (info){
                    case "修改昵称":
                        if(TextUtils.isEmpty(editText.getText().toString())){
                            Toast.makeText(MyDetailInfoActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        nickName.setText(editText.getText().toString());
                        alertDialog.dismiss();
                        Toast.makeText(MyDetailInfoActivity.this, "昵称修改成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case "修改手机号":
                        if(TextUtils.isEmpty(editText.getText().toString())){
                            Toast.makeText(MyDetailInfoActivity.this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ((TextView) v.findViewById(R.id.item_msg)).setText(editText.getText().toString());
                        alertDialog.dismiss();
                        Toast.makeText(MyDetailInfoActivity.this, "手机号修改成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case "修改邮箱":
                        if(!editText.getText().toString().matches("^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$")){
                            Toast.makeText(MyDetailInfoActivity.this, "邮箱格式不正确!", Toast.LENGTH_SHORT).show();
                        }else{
                            ((TextView) v.findViewById(R.id.item_msg)).setText(editText.getText().toString());
                            alertDialog.dismiss();
                        }
                        break;
                }
            }
        });
    }

    public void closeDialog(){
        if(alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public void handleImageOnKitKat(Intent data){
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
        cropImage(imagePath);
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

    public void cropImage(String imagePath){
        if(imagePath != null) {
            Intent intent = PictureCropActivity.newIntent(this, imagePath);
            startActivityForResult(intent,CROP_IMAGE);
        }else{
            Toast.makeText(this, "无法获取图片", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent newIntent(Context context,String path){
        Intent intent = new Intent(context,MyDetailInfoActivity.class);
        intent.putExtra(IMAGE_PATH,path);
        return intent;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.portrait_icon:
                openDialog();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(bitmap != null) {
            Intent intent = new Intent();
            byte[] bytes = Util.writeBitmapIntoByte(bitmap);
            intent.putExtra("bitmap", bytes);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}
