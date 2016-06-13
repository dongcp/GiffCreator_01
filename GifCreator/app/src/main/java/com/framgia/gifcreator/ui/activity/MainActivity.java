package com.framgia.gifcreator.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.util.BitmapWorkerTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CAMERA = 1;
    private final static int REQUEST_GALLERY = 2;
    private final static String CAMERA_IMAGE_TITLE = "Gif Creator";
    private final static String CAMERA_IMAGE_DESCRIPTION = "Image Description";
    private LinearLayout mFloatingMenu;
    private ImageView mImage;
    private Uri mImageUri;
    private ContentValues mValues;
    private View.OnClickListener mFabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_camera:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    break;
                case R.id.fab_gallery:
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_GALLERY);
                    break;
                case R.id.main_floating_button:
                    if (mFloatingMenu.getVisibility() == View.GONE) {
                        mFloatingMenu.setVisibility(View.VISIBLE);
                    } else mFloatingMenu.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        mValues = new ContentValues();
        mValues.put(MediaStore.Images.Media.TITLE, CAMERA_IMAGE_TITLE);
        mValues.put(MediaStore.Images.Media.DESCRIPTION, CAMERA_IMAGE_DESCRIPTION);
        mImageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mValues);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    try {
                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), mImageUri);
                        BitmapWorkerTask decodeBitmapTask = new BitmapWorkerTask(mImage);
                        decodeBitmapTask.execute(BitmapWorkerTask.TASK_DECODE_BITMAP, thumbnail);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_GALLERY:
                    Uri selectedImg = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodeString = cursor.getString(columnIndex);
                    cursor.close();
                    BitmapWorkerTask decodeFileTask = new BitmapWorkerTask(mImage);
                    decodeFileTask.execute(BitmapWorkerTask.TASK_DECODE_FILE, imgDecodeString);
                    break;
            }
        }
    }

    private void findViews() {
        mFloatingMenu = (LinearLayout) findViewById(R.id.floating_menu);
        mImage = (ImageView) findViewById(R.id.image);
        findViewById(R.id.fab_camera).setOnClickListener(mFabOnClickListener);
        findViewById(R.id.fab_gallery).setOnClickListener(mFabOnClickListener);
        findViewById(R.id.main_floating_button).setOnClickListener(mFabOnClickListener);
    }
}
