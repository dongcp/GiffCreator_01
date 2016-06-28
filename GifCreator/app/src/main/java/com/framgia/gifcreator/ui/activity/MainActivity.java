package com.framgia.gifcreator.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.adapter.GifAdapter;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.Gif;
import com.framgia.gifcreator.ui.base.BaseActivity;
import com.framgia.gifcreator.ui.decoration.GridItemDecoration;
import com.framgia.gifcreator.ui.decoration.LinearItemDecoration;
import com.framgia.gifcreator.ui.widget.GetPhotoDialog;
import com.framgia.gifcreator.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        GetPhotoDialog.OnDialogItemChooseListener {

    private final int NUMBER_OF_COLUMNS = 2;
    private final String DOT = ".";
    private RecyclerView mGifRecyclerView;
    private GifAdapter mGifAdapter;
    private List<Gif> mGifs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        mGifs = new ArrayList<>();
        getGifs();
        mGifAdapter = new GifAdapter(this, mGifs);
        if (mGifs.size() > 1) {
            mGifRecyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));
            mGifRecyclerView.addItemDecoration(new GridItemDecoration(this));
        } else {
            mGifRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mGifRecyclerView.addItemDecoration(new LinearItemDecoration(this));
        }
        mGifRecyclerView.setAdapter(mGifAdapter);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
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
        Intent intent = null;
        switch (type) {
            case GetPhotoDialog.TYPE_CAMERA:
                intent = new Intent(MainActivity.this, ShowListChosenImageActivity.class);
                intent.putExtra(Constants.EXTRA_REQUEST, Constants.REQUEST_CAMERA);
                break;
            case GetPhotoDialog.TYPE_GALLERY:
                intent = new Intent(MainActivity.this, ShowListChosenImageActivity.class);
                intent.putExtra(Constants.EXTRA_REQUEST, Constants.REQUEST_GALLERY);
                break;
        }
        if (intent != null) startActivity(intent);
    }

    private void findViews() {
        mGifRecyclerView = (RecyclerView) findViewById(R.id.list_gif);
        findViewById(R.id.floating_button).setOnClickListener(this);
    }

    private void getGifs() {
        File folder = new File(FileUtil.getAppFolderPath(this));
        File[] files = folder.listFiles();
        int length = files.length;
        if (mGifs.size() > 0) mGifs.clear();
        for (int i = 0; i < length; i++) {
            String fileName = files[i].getName();
            int indexOfLastDot = fileName.lastIndexOf(DOT);
            if (indexOfLastDot > 0 &&
                    fileName.substring(indexOfLastDot).equals(Constants.GIF_EXTENSION)) {
                mGifs.add(new Gif(files[i].getPath()));
            }
        }
    }
}
