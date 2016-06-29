package com.framgia.gifcreator.ui.base;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.framgia.gifcreator.R;

/**
 * Created by yue on 20/06/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            if (enableToolbar()) {
                setSupportActionBar(mToolbar);
                mToolbar.setTitle(getActivityTitle());
                mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
            } else mToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuResId() != -1) getMenuInflater().inflate(getMenuResId(), menu);
        return true;
    }

    public void enableBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    protected String getActivityTitle() {
        return getString(R.string.app_name);
    }

    protected boolean enableToolbar() {
        return true;
    }

    protected abstract int getLayoutResId();

    protected int getMenuResId() {
        return -1;
    }
}
