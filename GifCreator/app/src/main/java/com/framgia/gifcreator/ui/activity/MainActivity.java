package com.framgia.gifcreator.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mFloatingMenu;

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
