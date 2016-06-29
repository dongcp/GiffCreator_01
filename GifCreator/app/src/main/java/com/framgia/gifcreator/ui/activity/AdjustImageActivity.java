package com.framgia.gifcreator.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.Frame;
import com.framgia.gifcreator.ui.base.BaseActivity;
import com.framgia.gifcreator.ui.widget.BottomNavigationItem;
import com.framgia.gifcreator.util.BitmapHelper;
import com.framgia.gifcreator.util.BitmapWorkerTask;
import com.framgia.gifcreator.util.FileUtil;
import com.framgia.gifcreator.util.ImageProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdjustImageActivity extends BaseActivity implements
        BottomNavigationItem.OnBottomNavigationItemClickListener {

    private final int BOTTOM_NAVIGATION_LEVEL_1 = 1;
    private final int BOTTOM_NAVIGATION_LEVEL_2 = 2;
    private ImageView mAdjustImage;
    private LinearLayout mBottomNavigationContainer;
    private Frame mFrame;
    private Bitmap mProcessedImage;
    private List<BottomNavigationItem> mBottomNavigationMainItems;
    private List<BottomNavigationItem> mBottomNavigationAdjustItems;
    private int mBottomNavigationLevel;
    private int mPosition;
    private boolean mIsFirst;
    private boolean mIsProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        getData();
        mIsFirst = true;
        initBottomNavigation();
        mAdjustImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mIsFirst) {
                            BitmapWorkerTask worker = new BitmapWorkerTask(mAdjustImage, mFrame, true);
                            worker.execute(BitmapWorkerTask.TASK_DECODE_FILE, mFrame.getPhotoPath());
                            mIsFirst = false;
                        }
                    }
                }
        );
        enableBackButton();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_adjust_image;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_adjust_image;
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.title_adjust_image_activity);
    }

    @Override
    public void onItemClick(BottomNavigationItem item) {
        String title = item.getTitle();
        if (title.equals(getString(R.string.bottom_navigation_main_item_adjust))) {
            makeBottomNavigation(mBottomNavigationAdjustItems);
            mBottomNavigationLevel = BOTTOM_NAVIGATION_LEVEL_2;
            mIsProcessing = false;
        } else if (title.equals(getString(R.string.bottom_navigation_adjust_item_blur))) {
            mProcessedImage = ImageProcessing.blurImage(this, mFrame.getFrame());
            mAdjustImage.setImageBitmap(mProcessedImage);
            mIsProcessing = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_apply_effect:
                if (mIsProcessing) {
                    mFrame.setFrame(mProcessedImage);
                    makeBottomNavigation(mBottomNavigationMainItems);
                    mBottomNavigationLevel = BOTTOM_NAVIGATION_LEVEL_1;
                    mIsProcessing = false;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mIsProcessing) {
            mAdjustImage.setImageBitmap(mFrame.getFrame());
            makeBottomNavigation(mBottomNavigationMainItems);
            mBottomNavigationLevel = BOTTOM_NAVIGATION_LEVEL_1;
            mIsProcessing = false;
        } else {
            if (mBottomNavigationLevel == BOTTOM_NAVIGATION_LEVEL_2) {
                makeBottomNavigation(mBottomNavigationMainItems);
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage(R.string.save_image).
                        setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.EXTRA_POSITION, mPosition);
                                Point size = new Point();
                                getWindowManager().getDefaultDisplay().getSize(size);
                                int screenWidth = size.x;
                                int screenHeight = size.y;
                                try {
                                    if (mFrame.getFrame() != null) {
                                        Bitmap bitmap = BitmapHelper.resizeBitmap(mFrame.getFrame(),
                                                screenWidth, screenHeight);
                                        intent.putExtra(Constants.EXTRA_PHOTO_PATH,
                                                FileUtil.saveImage(AdjustImageActivity.this, bitmap));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }).
                        setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        });
                dialogBuilder.show();
            }
        }
    }

    private void findViews() {
        mAdjustImage = (ImageView) findViewById(R.id.adjust_image);
        mBottomNavigationContainer = (LinearLayout) findViewById(R.id.bottom_navigation_container);
    }

    private void getData() {
        Intent intent = getIntent();
        mFrame = new Frame();
        if (intent != null) {
            mPosition = intent.getIntExtra(Constants.EXTRA_POSITION, 0);
            mFrame.setPhotoPath(intent.getStringExtra(Constants.EXTRA_PHOTO_PATH));
        }
    }

    private void initBottomNavigation() {
        mBottomNavigationLevel = BOTTOM_NAVIGATION_LEVEL_1;
        mIsProcessing = false;
        initBottomNavigationMainItems();
        initBottomNavigationAdjustItems();
        makeBottomNavigation(mBottomNavigationMainItems);
    }

    private void initBottomNavigationMainItems() {
        if (mBottomNavigationMainItems == null || mBottomNavigationMainItems.size() == 0) {
            mBottomNavigationMainItems = new ArrayList<>();
            mBottomNavigationMainItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_main_item_adjust), R.drawable.ic_adjust));
            mBottomNavigationMainItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_main_item_color), R.drawable.ic_color_palette));
            mBottomNavigationMainItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_main_item_crop), R.drawable.ic_crop));
            mBottomNavigationMainItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_main_item_effect), R.drawable.ic_effect));
            mBottomNavigationMainItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_main_item_orientation), R.drawable.ic_orientation));
        }
    }

    private void initBottomNavigationAdjustItems() {
        if (mBottomNavigationAdjustItems == null || mBottomNavigationAdjustItems.size() == 0) {
            mBottomNavigationAdjustItems = new ArrayList<>();
            mBottomNavigationAdjustItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_adjust_item_blur), R.drawable.ic_blur));
        }
    }

    private void makeBottomNavigation(List<BottomNavigationItem> bottomNavigationItems) {
        mBottomNavigationContainer.removeAllViews();
        final int size = bottomNavigationItems.size();
        for (int i = 0; i < size; i++) {
            mBottomNavigationContainer.addView(bottomNavigationItems.get(i).getView());
            bottomNavigationItems.get(i).setOnBottomNavigationItemClickListener(this);
            if (i == size - 1) bottomNavigationItems.get(i).hideSeparator();
        }
    }
}
