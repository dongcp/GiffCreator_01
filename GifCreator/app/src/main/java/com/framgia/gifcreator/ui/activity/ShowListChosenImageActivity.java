package com.framgia.gifcreator.ui.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.adapter.ImageAdapter;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.Frame;
import com.framgia.gifcreator.ui.base.BaseActivity;
import com.framgia.gifcreator.ui.widget.GetPhotoDialog;
import com.framgia.gifcreator.util.AppHelper;
import com.framgia.gifcreator.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowListChosenImageActivity extends BaseActivity implements
        ImageAdapter.OnItemClickListener, View.OnClickListener, GetPhotoDialog.OnDialogItemChooseListener {

    private final int MIN_SIZE = 2;
    private final int MAX_SIZE = 10;
    private final int IMAGE_CAMERA = 1;
    private final int IMAGE_GALLERY = 2;
    private final String IMAGE_EXTENSION = ".jpg";
    private ImageAdapter mImageAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private CoordinatorLayout mCoordinatorLayout;
    private MenuItem mItemPreviewGif;
    private MenuItem mItemOpenListChosen;
    private List<Frame> mAllItemList;
    private List<Frame> mGalleryList;
    private List<Frame> mCameraList;
    private List<Frame> mChosenList;
    private String mCurrentPhotoPath;
    private int mRequestCode;
    private int mSourceType;
    private boolean isChosenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        // Setup recycler view
        mAllItemList = new ArrayList<>();
        mCameraList = new ArrayList<>();
        mGalleryList = new ArrayList<>();
        mChosenList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //mAllItemList.addAll(getImageListGallery());
        mImageAdapter = new ImageAdapter(this, mAllItemList);
        mRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.setOnItemClickListener(this);
        enableBackButton();
        // Call activity to get photo
        Intent intent = getIntent();
        if (intent != null) {
            mRequestCode = intent.getIntExtra(Constants.EXTRA_REQUEST, Constants.REQUEST_GALLERY);
            switch (mRequestCode) {
                case Constants.REQUEST_CAMERA:
                    mSourceType = IMAGE_CAMERA;
                    isChosenList = false;
                    Intent getPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (getPhotoIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (photoFile != null) {
                            getPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(getPhotoIntent, Constants.REQUEST_CAMERA);
                        }
                    }
                    break;
                case Constants.REQUEST_GALLERY:
                    mSourceType = IMAGE_GALLERY;
                    isChosenList = false;
                    if (mGalleryList.size() == 0) {
                        mGalleryList = getImageListGallery();
                    }
                    refresh(mGalleryList);
                    break;
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_choosing_image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chosen_image, menu);
        mItemPreviewGif = menu.findItem(R.id.action_preview_gif);
        mItemOpenListChosen = menu.findItem(R.id.action_open_list_chosen);
        mItemPreviewGif.setVisible(mRequestCode != Constants.REQUEST_GALLERY);
        mItemOpenListChosen.setVisible(mRequestCode == Constants.REQUEST_GALLERY);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.title_show_chosen_images_activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Frame frame;
            switch (requestCode) {
                case Constants.REQUEST_CAMERA:
                    isChosenList = true;
                    refreshToolbar();
                    frame = new Frame(mCurrentPhotoPath);
                    frame.setChecked(true);
                    mCameraList.add(frame);
                    refresh(mCameraList);
                    galleryAddPic();
                    break;
                case Constants.REQUEST_GALLERY:
                    Uri selectedImg = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            selectedImg, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    String imgDecodeString = cursor.
                            getString(cursor.getColumnIndex(filePathColumn[0]));
                    cursor.close();
                    frame = new Frame(imgDecodeString);
                    mAllItemList.add(frame);
                    mImageAdapter.notifyItemInserted(mAllItemList.indexOf(frame));
                    break;
                case Constants.REQUEST_ADJUST:
                    int position = data.getIntExtra(Constants.EXTRA_POSITION, 0);
                    String photoPath = data.getStringExtra(Constants.EXTRA_PHOTO_PATH);
                    if (!TextUtils.isEmpty(photoPath)) {
                        frame = mAllItemList.get(position);
                        frame.setPhotoPath(photoPath);
                        frame.setFrame(null);
                        mImageAdapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    private List<Frame> getImageListGallery() {
        List<Frame> imageItems = new ArrayList<>();
        CursorLoader imageLoader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                null, null, MediaStore.Images.Media._ID);
        Cursor imageCursor = imageLoader.loadInBackground();
        if (imageCursor.moveToLast()) {
            do {
                String imagePath = imageCursor.getString(
                        imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (isNormalImage(imagePath)) {
                    Frame frame = new Frame(imagePath);
                    imageItems.add(frame);
                }
            } while (imageCursor.moveToPrevious());
        }
        imageCursor.close();
        return imageItems;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview_gif:
                if (mAllItemList.size() < MIN_SIZE) {
                    Snackbar.make(mCoordinatorLayout,
                            getString(R.string.warning_make_gif), Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, PreviewGifActivity.class);
                    int size = mAllItemList.size();
                    String[] paths = new String[size];
                    for (int i = 0; i < size; i++) {
                        paths[i] = mAllItemList.get(i).getPhotoPath();
                    }
                    intent.putExtra(Constants.EXTRA_PATHS_LIST, paths);
                    startActivity(intent);
                }
                break;
            case R.id.action_open_list_chosen:
                if (getChosenList().size() > MAX_SIZE) {
                    Snackbar.make(mCoordinatorLayout,
                            getString(R.string.out_of_limit), Snackbar.LENGTH_SHORT).show();
                } else {
                    isChosenList = true;
                    mChosenList = getChosenList();
                    refresh(mChosenList);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button:
                GetPhotoDialog dialog = new GetPhotoDialog(this);
                dialog.setOnDialogItemChooseListener(this);
                dialog.showDialog();
                break;
        }
    }

    @Override
    public void onDialogItemChoose(int type) {
        switch (type) {
            case GetPhotoDialog.TYPE_CAMERA:
                if (mAllItemList.size() == Constants.MAXIMUM_FRAMES) {
                    AppHelper.showSnackbar(mCoordinatorLayout, R.string.out_of_limit);
                } else {
                    isChosenList = false;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (photoFile != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(intent, Constants.REQUEST_CAMERA);
                        }
                    }
                }
                break;
            case GetPhotoDialog.TYPE_GALLERY:
                if (mAllItemList.size() > Constants.MAXIMUM_FRAMES) {
                    AppHelper.showSnackbar(mCoordinatorLayout, R.string.out_of_limit);
                } else {
                    isChosenList = false;
                    if (mGalleryList.size() == 0) {
                        mGalleryList = getImageListGallery();
                    }
                    refresh(mGalleryList);
                }
                break;
        }
    }

    @Override
    public void onRemoveItem(int position) {
        if (isChosenList) {
            switch (mSourceType) {
                case IMAGE_CAMERA:
                    UpdateStateFromList(mCameraList, mChosenList.get(position));
                    mChosenList.remove(position);
                    break;
                case IMAGE_GALLERY:
                    UpdateStateFromList(mGalleryList, mChosenList.get(position));
                    mChosenList.remove(position);
                    break;
            }
            refresh(mChosenList);
        }
    }

    @Override
    public void showAlertNotification() {
        AppHelper.showSnackbar(mCoordinatorLayout, R.string.out_of_limit);
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choosing_image);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mFab = (FloatingActionButton) findViewById(R.id.floating_button);
        mFab.setOnClickListener(this);
    }

    private File createImageFile() throws IOException {
        String imageFileName = FileUtil.getImageName();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, IMAGE_EXTENSION, storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private List<Frame> getChosenList() {
        List<Frame> chosenList = new ArrayList<>();
        int length = mAllItemList.size();
        for (int i = 0; i < length; i++) {
            if (mAllItemList.get(i).isChosen()) {
                chosenList.add(mAllItemList.get(i));
            }
        }
        return chosenList;
    }

    public void refresh(List<Frame> frames) {
        mAllItemList.clear();
        mAllItemList.addAll(frames);
        mImageAdapter.notifyDataSetChanged();
        mFab.setVisibility(isChosenList ? View.VISIBLE : View.GONE);
        refreshToolbar();
    }

    private void refreshToolbar() {
        if (mItemPreviewGif != null && mItemOpenListChosen != null) {
            mItemPreviewGif.setVisible(isChosenList);
            mItemOpenListChosen.setVisible(!isChosenList);
        }
    }

    private void UpdateStateFromList(List<Frame> frames, Frame frame) {
        int length = frames.size();
        for (int i = 0; i < length; i++) {
            if (frame.getPhotoPath().equals(frames.get(i).getPhotoPath())) {
                frames.get(i).setChecked(false);
            }
        }
    }

    private boolean isNormalImage(String filePath) {
        boolean check = true;
        int length = filePath.length();
        int position = filePath.lastIndexOf(Constants.DOT);
        for (int i = 0; i < length; i++) {
            if (filePath.substring(position).equals(Constants.PNG) ||
                    filePath.substring(position).equals(Constants.JPG) &&
                            position > 0) {
                return true;
            }
        }
        return check;
    }
}
