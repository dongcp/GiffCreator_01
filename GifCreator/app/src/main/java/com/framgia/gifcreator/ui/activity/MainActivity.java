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
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.ui.widget.BottomNavigationItem;
import com.framgia.gifcreator.util.BitmapWorkerTask;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        BottomNavigationItem.OnBottomNavigationItemClickListener {

    private LinearLayout mFloatingMenu;
    private LinearLayout mBottomNavigationContainer;
    private ArrayList<BottomNavigationItem> mBottomNavigationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_camera:
                Intent intent = new Intent(MainActivity.this, ChoosingImageActivity.class);
                intent.putExtra(Constants.INTENT_REQUEST, Constants.REQUEST_CAMERA);
                startActivity(intent);
                break;
            case R.id.fab_gallery:
                intent = new Intent(MainActivity.this, ChoosingImageActivity.class);
                intent.putExtra(Constants.INTENT_REQUEST, Constants.REQUEST_GALLERY);
                startActivity(intent);
                break;
            case R.id.main_floating_button:
                mFloatingMenu.setVisibility(
                        mFloatingMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
        }
    }

    // Bottom navigation item click event
    @Override
    public void onItemClick(BottomNavigationItem item) {
    }

    private void findViews() {
        mFloatingMenu = (LinearLayout) findViewById(R.id.floating_menu);
        mBottomNavigationContainer = (LinearLayout) findViewById(R.id.bottom_navigation_container);
        findViewById(R.id.fab_camera).setOnClickListener(this);
        findViewById(R.id.fab_gallery).setOnClickListener(this);
        findViewById(R.id.main_floating_button).setOnClickListener(this);
    }

    private void initBottomNavigation() {
        mBottomNavigationItems = new ArrayList<>();
        mBottomNavigationItems.add(new BottomNavigationItem(this,
                getString(R.string.bottom_navigation_item_adjust), R.drawable.ic_adjust));
        mBottomNavigationItems.add(new BottomNavigationItem(this,
                getString(R.string.bottom_navigation_item_color), R.drawable.ic_color_palette));
        mBottomNavigationItems.add(new BottomNavigationItem(this,
                getString(R.string.bottom_navigation_item_crop), R.drawable.ic_crop));
        mBottomNavigationItems.add(new BottomNavigationItem(this,
                getString(R.string.bottom_navigation_item_effect), R.drawable.ic_effect));
        mBottomNavigationItems.add(new BottomNavigationItem(this,
                getString(R.string.bottom_navigation_item_orientation), R.drawable.ic_orientation));
        final int size = mBottomNavigationItems.size();
        for (int i = 0; i < size; i++) {
            mBottomNavigationContainer.addView(mBottomNavigationItems.get(i).getView());
            mBottomNavigationItems.get(i).setOnBottomNavigationItemClickListener(this);
            if (i == size - 1) {
                mBottomNavigationItems.get(i).hideSeparator();
            }
        }
    }
}
