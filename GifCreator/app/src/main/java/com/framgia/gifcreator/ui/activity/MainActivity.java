package com.framgia.gifcreator.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.ui.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mFloatingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_camera:
                Intent intent = new Intent(MainActivity.this, ShowListChosenImageActivity.class);
                intent.putExtra(Constants.EXTRA_REQUEST, Constants.REQUEST_CAMERA);
                startActivity(intent);
                break;
            case R.id.fab_gallery:
                intent = new Intent(MainActivity.this, ShowListChosenImageActivity.class);
                intent.putExtra(Constants.EXTRA_REQUEST, Constants.REQUEST_GALLERY);
                startActivity(intent);
                break;
            case R.id.main_floating_button:
                mFloatingMenu.setVisibility(
                        mFloatingMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void findViews() {
        mFloatingMenu = (LinearLayout) findViewById(R.id.floating_menu);
        findViewById(R.id.fab_camera).setOnClickListener(this);
        findViewById(R.id.fab_gallery).setOnClickListener(this);
        findViewById(R.id.main_floating_button).setOnClickListener(this);
    }
}
