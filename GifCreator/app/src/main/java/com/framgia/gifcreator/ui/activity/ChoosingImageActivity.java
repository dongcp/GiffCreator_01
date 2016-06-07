package com.framgia.gifcreator.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.adapter.ImageAdapter;
import com.framgia.gifcreator.util.ImageItem;

import java.util.ArrayList;

public class ChoosingImageActivity extends AppCompatActivity implements ImageAdapter.OnItemClicklistener {
    private ImageAdapter mImageAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<ImageItem> mImageItems;
    public static final int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_image);
        findView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chosen_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_adding_image:
                openGalerry();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void findView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_choosing_image);
        mImageItems = new ArrayList<>();
        mImageAdapter = new ImageAdapter(this, mImageItems);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mImageAdapter.setOnItemClicklistener(this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
            Uri selectedImg = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodeString = cursor.getString(columnIndex);
            ImageItem imageItem = new ImageItem(imgDecodeString);
            mImageItems.add(imageItem);
            mImageAdapter.notifyItemInserted(mImageItems.indexOf(imageItem));
            cursor.close();
        }
    }

    private void openGalerry() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMG);
    }

    @Override
    public void onRemoveItem(int position) {
        mImageItems.remove(position);
        mImageAdapter.notifyItemRemoved(position);
    }
}
