package com.framgia.gifcreator.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.ui.widget.BottomNavigationItem;
import com.framgia.gifcreator.util.BitmapHelper;
import com.framgia.gifcreator.util.BitmapWorkerTask;

import java.util.ArrayList;

public class AdjustImageActivity extends AppCompatActivity implements
        BottomNavigationItem.OnBottomNavigationItemClickListener {

    private final String BOTTOM_NAVIGATION_ADJUST_ITEM = "Adjust";
    private ImageView mAdjustImage;
    private LinearLayout mBottomNavigationContainer;
    private Bitmap mOriginImage;
    private String mPhotoPath;
    private ArrayList<BottomNavigationItem> mBottomNavigationMainItems;
    private ArrayList<BottomNavigationItem> mBottomNavigationAdjustItems;
    private boolean mIsFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_image);
        findViews();
        getData();
        mIsFirst = true;
        initBottomNavigation();
        mAdjustImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mIsFirst) {
                            mOriginImage = BitmapHelper.decodeFile(mPhotoPath,
                                    mAdjustImage.getWidth(), mAdjustImage.getHeight());
                            BitmapWorkerTask worker = new BitmapWorkerTask(mAdjustImage);
                            worker.execute(BitmapWorkerTask.TASK_RESIZE_BITMAP, mOriginImage);
                            mIsFirst = false;
                        }
                    }
                }
        );
    }

    @Override
    public void onItemClick(BottomNavigationItem item) {
        switch (item.getTitle()) {
            case BOTTOM_NAVIGATION_ADJUST_ITEM:
                makeBottomNavigation(mBottomNavigationAdjustItems);
                break;
        }
    }

    private void findViews() {
        mAdjustImage = (ImageView) findViewById(R.id.adjust_image);
        mBottomNavigationContainer = (LinearLayout) findViewById(R.id.bottom_navigation_container);
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            mPhotoPath = intent.getStringExtra(Constants.EXTRA_PHOTO_PATH);
        }
    }

    private void initBottomNavigation() {
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

    private void makeBottomNavigation(ArrayList<BottomNavigationItem> bottomNavigationItems) {
        mBottomNavigationContainer.removeAllViews();
        final int size = bottomNavigationItems.size();
        for (int i = 0; i < size; i++) {
            mBottomNavigationContainer.addView(bottomNavigationItems.get(i).getView());
            bottomNavigationItems.get(i).setOnBottomNavigationItemClickListener(this);
            if (i == size - 1) {
                bottomNavigationItems.get(i).hideSeparator();
            }
        }
    }
}
