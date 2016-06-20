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
import android.support.v7.app.AlertDialog;
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
import com.framgia.gifcreator.util.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowListChosenImageActivity extends BaseActivity implements
        ImageAdapter.OnItemClickListener, View.OnClickListener, GetPhotoDialog.OnDialogItemChooseListener {

    public static CoordinatorLayout sCoordinatorLayout;
    public static int sNumberOfFrames;
    public static boolean sCanAdjustFrame;
    private final int MIN_SIZE = 2;
    private final int MAX_SIZE = 10;
    private final String IMAGE_EXTENSION = ".jpg";
    private ImageAdapter mImageAdapter;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private MenuItem mItemPreviewGif;
    private MenuItem mItemOpenListChosen;
    private List<Frame> mAllItemList;
    private List<Frame> mGalleryList;
    private List<Frame> mCameraList;
    private List<Frame> mChosenList;
    private String mCurrentPhotoPath;
    private int mRequestCode;
    private boolean mIsChosenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        // Setup recycler view
        mAllItemList = new ArrayList<>();
        mCameraList = new ArrayList<>();
        mGalleryList = new ArrayList<>();
        mChosenList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(this, mAllItemList);
        mImageAdapter.setOnItemClickListener(this);
        // Call activity to get photo
        Intent intent = getIntent();
        if (intent != null) {
            mRequestCode = intent.getIntExtra(Constants.EXTRA_REQUEST, Constants.REQUEST_GALLERY);
            switch (mRequestCode) {
                case Constants.REQUEST_CAMERA:
                    mIsChosenList = false;
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
                    mIsChosenList = false;
                    if (mGalleryList.size() == 0) {
                        mGalleryList = getImageListGallery();
                    }
                    refresh(mGalleryList);
                    break;
            }
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mImageAdapter);
        enableBackButton();
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
        sCanAdjustFrame = true;
        if (resultCode == RESULT_OK) {
            Frame frame;
            switch (requestCode) {
                case Constants.REQUEST_CAMERA:
                    mIsChosenList = true;
                    refreshToolbar();
                    frame = new Frame(mCurrentPhotoPath);
                    frame.setChecked(true);
                    mCameraList.add(frame);
                    refresh(mCameraList);
                    galleryAddPic();
                    break;
                case Constants.REQUEST_ADJUST:
                    int position = data.getIntExtra(Constants.EXTRA_POSITION, 0);
                    String photoPath = data.getStringExtra(Constants.EXTRA_PHOTO_PATH);
                    if (!TextUtils.isEmpty(photoPath)) {
                        frame = mAllItemList.get(position);
                        frame.setPhotoPath(photoPath);
                        frame.setFrame(null);
                        switch (mRequestCode) {
                            case Constants.REQUEST_CAMERA:
                                mCameraList.get(position).setPhotoPath(photoPath);
                                break;
                            case Constants.REQUEST_GALLERY:
                                mGalleryList.get(position).setPhotoPath(photoPath);
                                break;
                        }
                        mImageAdapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview_gif:
                int size = mAllItemList.size();
                if (size < MIN_SIZE) {
                    AppHelper.showSnackbar(sCoordinatorLayout, R.string.warning_make_gif);
                } else {
                    Intent intent = new Intent(this, PreviewGifActivity.class);
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
                    Snackbar.make(sCoordinatorLayout,
                            getString(R.string.out_of_limit), Snackbar.LENGTH_SHORT).show();
                } else {
                    sCanAdjustFrame = true;
                    mIsChosenList = true;
                    mChosenList = getChosenList();
                    refresh(mChosenList);
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mIsChosenList) {
            mIsChosenList = true;
            refresh(mChosenList);
        } else {
            super.onBackPressed();
        }
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
                if (PermissionUtil.isCameraPermissionGranted(this)) {
                    if (mAllItemList.size() == Constants.MAXIMUM_FRAMES) {
                        AppHelper.showSnackbar(sCoordinatorLayout, R.string.out_of_limit);
                    } else {
                        mRequestCode = Constants.REQUEST_CAMERA;
                        mIsChosenList = false;
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error).
                            setMessage(R.string.cannot_access_camera).show();
                }
                break;
            case GetPhotoDialog.TYPE_GALLERY:
                if (PermissionUtil.isStoragePermissionGranted(this)) {
                    if (mAllItemList.size() > Constants.MAXIMUM_FRAMES) {
                        AppHelper.showSnackbar(sCoordinatorLayout, R.string.out_of_limit);
                    } else {
                        sCanAdjustFrame = false;
                        mRequestCode = Constants.REQUEST_GALLERY;
                        mIsChosenList = false;
                        if (mGalleryList.size() == 0) {
                            mGalleryList = getImageListGallery();
                        }
                        refresh(mGalleryList);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error).
                            setMessage(R.string.cannot_access_gallery).show();
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        if (sCanAdjustFrame) {
            Intent intent = new Intent(this, AdjustImageActivity.class);
            intent.putExtra(Constants.EXTRA_POSITION, position);
            intent.putExtra(Constants.EXTRA_PHOTO_PATH, mAllItemList.get(position).getPhotoPath());
            startActivityForResult(intent, Constants.REQUEST_ADJUST);
        } else {
            Frame frame = mAllItemList.get(position);
            if (sNumberOfFrames < Constants.MAXIMUM_FRAMES) {
                frame.setChecked(!frame.isChosen());
                if (frame.isChosen()) sNumberOfFrames++;
                else sNumberOfFrames--;
            } else {
                AppHelper.showSnackbar(sCoordinatorLayout, R.string.out_of_limit);
                frame.setChecked(false);
            }
            mImageAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onRemoveItem(int position) {
        sNumberOfFrames--;
        if (mIsChosenList) {
            switch (mRequestCode) {
                case Constants.REQUEST_CAMERA:
                    mAllItemList.remove(position);
                    mCameraList.remove(position);
                    mImageAdapter.notifyItemRemoved(position);
                    mImageAdapter.notifyItemRangeChanged(position, mAllItemList.size());
                    break;
                case Constants.REQUEST_GALLERY:
                    UpdateStateFromList(mGalleryList, mChosenList.get(position));
                    mChosenList.remove(position);
                    refresh(mChosenList);
                    break;
            }
        }
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choosing_image);
        sCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
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
        if (mAllItemList.size() > 0) mAllItemList.clear();
        mAllItemList.addAll(frames);
        mImageAdapter.notifyDataSetChanged();
        mFab.setVisibility(mIsChosenList ? View.VISIBLE : View.GONE);
        refreshToolbar();
    }

    private void refreshToolbar() {
        if (mItemPreviewGif != null && mItemOpenListChosen != null) {
            mItemPreviewGif.setVisible(mIsChosenList);
            mItemOpenListChosen.setVisible(!mIsChosenList);
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

    private boolean isNormalImage(String filePath) {
        int position = filePath.lastIndexOf(Constants.DOT);
        return (position > 0 && (filePath.substring(position + 1).equals(Constants.PNG) ||
                filePath.substring(position + 1).equals(Constants.JPG)));
    }
}
