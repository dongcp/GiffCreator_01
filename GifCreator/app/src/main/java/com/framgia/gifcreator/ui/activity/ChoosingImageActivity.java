package com.framgia.gifcreator.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.adapter.ImageAdapter;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.ImageItem;

import java.io.IOException;
import java.util.ArrayList;

public class ChoosingImageActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener,
        View.OnClickListener {

    private final String CAMERA_IMAGE_TITLE = "Gif Creator";
    private final String CAMERA_IMAGE_DESCRIPTION = "Image Description";
    private ImageAdapter mImageAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout mFloatingMenu;
    private ArrayList<ImageItem> mImageItems;
    private Uri mImageUri;
    private ContentValues mValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_image);
        findView();
        mValues = new ContentValues();
        mValues.put(MediaStore.Images.Media.TITLE, CAMERA_IMAGE_TITLE);
        mValues.put(MediaStore.Images.Media.DESCRIPTION, CAMERA_IMAGE_DESCRIPTION);
        mImageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mValues);
        Intent intent = getIntent();
        if (intent != null) {
            switch (intent.getIntExtra(Constants.INTENT_REQUEST, Constants.REQUEST_GALLERY)) {
                case Constants.REQUEST_CAMERA:
                    Intent getPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(getPhotoIntent, Constants.REQUEST_CAMERA);
                    break;
                case Constants.REQUEST_GALLERY:
                    getPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(getPhotoIntent, Constants.REQUEST_GALLERY);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CAMERA:
                    try {
                        Bitmap image = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), mImageUri);
                        ImageItem imageItem = new ImageItem(image);
                        mImageItems.add(imageItem);
                        mImageAdapter.notifyItemInserted(mImageItems.indexOf(imageItem));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.REQUEST_GALLERY:
                    Uri selectedImg = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodeString = cursor.getString(columnIndex);
                    cursor.close();
                    ImageItem imageItem = new ImageItem(imgDecodeString);
                    mImageItems.add(imageItem);
                    mImageAdapter.notifyItemInserted(mImageItems.indexOf(imageItem));
                    break;
            }
        }
    }

    @Override
    public void onRemoveItem(int position) {
        mImageItems.remove(position);
        mImageAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, Constants.REQUEST_CAMERA);
                break;
            case R.id.fab_gallery:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.REQUEST_GALLERY);
                break;
            case R.id.main_floating_button:
                mFloatingMenu.setVisibility(
                        mFloatingMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
        }
    }

    public void findView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choosing_image);
        mImageItems = new ArrayList<>();
        mImageAdapter = new ImageAdapter(this, mImageItems);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mImageAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mImageAdapter);
        mFloatingMenu = (LinearLayout) findViewById(R.id.floating_menu);
        findViewById(R.id.fab_camera).setOnClickListener(this);
        findViewById(R.id.fab_gallery).setOnClickListener(this);
        findViewById(R.id.main_floating_button).setOnClickListener(this);
    }
}
