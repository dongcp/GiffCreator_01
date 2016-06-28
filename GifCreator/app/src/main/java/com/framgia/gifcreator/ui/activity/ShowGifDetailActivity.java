package com.framgia.gifcreator.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.adapter.ThumbnailPagerAdapter;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.Frame;
import com.framgia.gifcreator.ui.base.BaseActivity;
import com.framgia.gifcreator.ui.widget.GetPhotoDialog;
import com.framgia.gifcreator.util.BitmapWorkerTask;
import com.framgia.gifcreator.util.listener.OnThumbnailPagerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue on 09/06/2016.
 */
public class ShowGifDetailActivity extends BaseActivity implements
        OnThumbnailPagerItemClickListener {

    private final int OFF_SCREEN_PAGE_LIMIT = 3;
    private ImageView mLargeImage;
    private ViewPager mThumbnailPager;
    private ThumbnailPagerAdapter mAdapter;
    private List<Frame> mFrames;
    private int mCurrentPosition;
    private boolean mIsFirst = true;
    private boolean mIsFrameChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        getData();
        mAdapter = new ThumbnailPagerAdapter(this, mThumbnailPager, mFrames);
        mAdapter.setOnThumbnailPagerItemClickListener(this);
        mThumbnailPager.setClipToPadding(false);
        mThumbnailPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.common_size_15));
        mThumbnailPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
        mThumbnailPager.setAdapter(mAdapter);
        mLargeImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mIsFirst) {
                            mIsFirst = false;
                            Frame currentFrame = mFrames.get(mCurrentPosition);
                            BitmapWorkerTask task = new BitmapWorkerTask(mLargeImage, currentFrame,
                                    mLargeImage.getWidth(), mLargeImage.getHeight(), false);
                            task.execute(
                                    BitmapWorkerTask.TASK_DECODE_FILE, currentFrame.getPhotoPath());
                        }
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_show_gif_detail;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_show_gif_detail;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_image:
                Intent intent = new Intent(this, AdjustImageActivity.class);
                intent.putExtra(Constants.EXTRA_PHOTO_PATH,
                        mFrames.get(mCurrentPosition).getPhotoPath());
                intent.putExtra(Constants.EXTRA_POSITION, mCurrentPosition);
                startActivityForResult(intent, Constants.REQUEST_ADJUST);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_ADJUST) {
            if (resultCode == RESULT_OK) {
                String photoPath = data.getStringExtra(Constants.EXTRA_PHOTO_PATH);
                mCurrentPosition = data.getIntExtra(Constants.EXTRA_POSITION, mCurrentPosition);
                if (!TextUtils.isEmpty(photoPath)) {
                    mIsFrameChanged = true;
                    Frame currentFrame = mFrames.get(mCurrentPosition);
                    currentFrame.destroy();
                    currentFrame.setPhotoPath(photoPath);
                    mAdapter.refresh(mCurrentPosition);
                    BitmapWorkerTask task = new BitmapWorkerTask(mLargeImage, currentFrame,
                            mLargeImage.getWidth(), mLargeImage.getHeight(), false);
                    task.execute(BitmapWorkerTask.TASK_DECODE_FILE, currentFrame.getPhotoPath());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onThumbnailClick(int position) {
        mCurrentPosition = position;
        Frame frame = mFrames.get(position);
        mLargeImage.setImageBitmap(frame.getFrame());
    }

    @Override
    public void onThumbnailLongClick() {
        GetPhotoDialog dialog = new GetPhotoDialog(this);
        dialog.showDialog();
    }

    @Override
    public void onBackPressed() {
        if (mIsFrameChanged) {
            Intent intent = new Intent();
            int size = mFrames.size();
            String[] photoPaths = new String[size];
            for (int i = 0; i < size; i++) {
                photoPaths[i] = mFrames.get(i).getPhotoPath();
            }
            intent.putExtra(Constants.EXTRA_PATHS_LIST, photoPaths);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    private void findViews() {
        mLargeImage = (ImageView) findViewById(R.id.large_image);
        mThumbnailPager = (ViewPager) findViewById(R.id.thumbnail_pager);
    }

    private void getData() {
        mFrames = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            String[] paths = intent.getStringArrayExtra(Constants.EXTRA_PATHS_LIST);
            if (paths.length > 0) {
                for (String path : paths) {
                    mFrames.add(new Frame(path));
                }
            }
        }
    }
}
