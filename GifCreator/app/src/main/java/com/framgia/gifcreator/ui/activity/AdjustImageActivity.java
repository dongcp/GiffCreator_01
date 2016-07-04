package com.framgia.gifcreator.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.Frame;
import com.framgia.gifcreator.effect.ColorEffect;
import com.framgia.gifcreator.effect.ContrastEffect;
import com.framgia.gifcreator.effect.EditingEffect;
import com.framgia.gifcreator.effect.GrayScaleEffect;
import com.framgia.gifcreator.effect.NegativeEffect;
import com.framgia.gifcreator.effect.RotationEffect;
import com.framgia.gifcreator.ui.base.BaseActivity;
import com.framgia.gifcreator.ui.widget.AdjustImageBar;
import com.framgia.gifcreator.ui.widget.BottomNavigationItem;
import com.framgia.gifcreator.util.BitmapHelper;
import com.framgia.gifcreator.util.BitmapWorkerTask;
import com.framgia.gifcreator.util.FileUtil;
import com.framgia.gifcreator.util.HandlingImageAsyncTask;
import com.framgia.gifcreator.util.ImageProcessing;
import com.framgia.gifcreator.util.PermissionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdjustImageActivity extends BaseActivity implements
        BottomNavigationItem.OnBottomNavigationItemClickListener,
        HandlingImageAsyncTask.OnProgressListener,
        AdjustImageBar.OnAdjustImageBarItemInteractListener {

    private final int BOTTOM_NAVIGATION_LEVEL_1 = 1;
    private final int BOTTOM_NAVIGATION_LEVEL_2 = 2;
    private final float IMAGE_VALUE = 127;
    private final int RED = 0;
    private final int GREEN = 1;
    private final int BLUE = 2;
    private final int ALPHA = 3;
    private final int CONTRAST = 4;
    private final int START_PROGRESS = 127;
    private final int MAX_PROGRESS = 255;
    private ImageView mAdjustImage;
    private LinearLayout mBottomNavigationContainer;
    private Frame mFrame;
    private Bitmap mProcessedImage;
    private MenuItem mItemApplyEffect;
    private List<BottomNavigationItem> mBottomNavigationMainItems;
    private List<BottomNavigationItem> mBottomNavigationAdjustItems;
    private List<BottomNavigationItem> mBottomNavigationColorItems;
    private Bitmap mOriginImage;
    private AdjustImageBar mAdjustmentImageBar;
    private HandlingImageAsyncTask mHandlingImageAsynctask;
    private ProgressDialog mProgressDialog;
    private RotationEffect mRotationEffect;
    private GrayScaleEffect mGrayScaleEffect;
    private NegativeEffect mNegativeEffect;
    private ColorEffect mColorEffect;
    private ContrastEffect mContrastEffect;
    private int mBottomNavigationLevel;
    private int mPosition;
    private boolean mIsFirst;
    private boolean mIsProcessing;
    private int mEffectType;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        getData();
        mIsFirst = true;
        init();
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
        mAdjustmentImageBar.setOnAdjustImageBarItemInteractListener(this);
        mOriginImage = mFrame.getFrame();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_adjust_image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adjust_image, menu);
        mItemApplyEffect = menu.findItem(R.id.action_apply_effect);
        mItemApplyEffect.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.title_adjust_image_activity);
    }

    @Override
    public void onItemClick(BottomNavigationItem item) {
        String title = item.getTitle();
        mAdjustImage.setImageBitmap(mProcessedImage);
        if (title.equals(getString(R.string.bottom_navigation_main_item_adjust))) {
            makeBottomNavigation(mBottomNavigationAdjustItems);
            mBottomNavigationLevel = BOTTOM_NAVIGATION_LEVEL_2;
            mIsProcessing = false;
        } else if (title.equals(getString(R.string.bottom_navigation_adjust_item_blur))) {
            showItemApplyEffect();
            mProcessedImage = ImageProcessing.blurImage(this, mFrame.getFrame());
            mAdjustImage.setImageBitmap(mProcessedImage);
            mIsProcessing = true;
            closeSeekbar();
        } else if (title.equals(getString(R.string.bottom_navigation_adjust_item_grayscale))) {
            setEffect(mGrayScaleEffect);
            mIsProcessing = true;
            closeSeekbar();
        } else if (title.equals(getString(R.string.bottom_navigation_adjust_item_negative))) {
            setEffect(mNegativeEffect);
            mIsProcessing = true;
            closeSeekbar();
        } else if (title.equals(getString(R.string.bottom_navigation_main_item_orientation))) {
            setEffect(mRotationEffect);
            mIsProcessing = true;
            closeSeekbar();
        } else if (title.equals(getString(R.string.bottom_navigation_main_item_color))) {
            makeBottomNavigation(mBottomNavigationColorItems);
            mBottomNavigationLevel = BOTTOM_NAVIGATION_LEVEL_2;
            mIsProcessing = false;
        } else if (title.equals(getString(R.string.bottom_navigation_blue_color))) {
            setupSeekbar();
            mEffectType = BLUE;
            mIsProcessing = true;
        } else if (title.equals(getString(R.string.bottom_navigation_red_color))) {
            setupSeekbar();
            mEffectType = RED;
            mIsProcessing = true;
        } else if (title.equals(getString(R.string.bottom_navigation_green_color))) {
            setupSeekbar();
            mEffectType = GREEN;
            mIsProcessing = true;
        } else if (title.equals(getString(R.string.bottom_navigation_alpha_color))) {
            setupSeekbar();
            mEffectType = ALPHA;
            mIsProcessing = true;
        } else if (title.equals(getString(R.string.bottom_navigation_adjust_item_contrast))) {
            setupSeekbar();
            mEffectType = CONTRAST;
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
                } else {
                    saveProcessedImage();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mIsProcessing) {
            if (mItemApplyEffect != null) mItemApplyEffect.setVisible(false);
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
                                saveProcessedImage();
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
            closeSeekbar();
        }
    }

    private void setupSeekbar() {
        mAdjustmentImageBar.setVisibility(View.VISIBLE);
        mAdjustmentImageBar.setProgress(START_PROGRESS);
        mAdjustmentImageBar.setMaxValue(MAX_PROGRESS);
    }

    private void closeSeekbar() {
        mAdjustmentImageBar.setVisibility(View.INVISIBLE);
    }

//    private void saveImage() {
//        Intent intent = new Intent();
//        intent.putExtra(Constants.EXTRA_POSITION, mPosition);
//        try {
//            intent.putExtra(Constants.EXTRA_PHOTO_PATH, FileUtil.saveImage(this, mFrame.getFrame()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        setResult(RESULT_OK, intent);
//        finish();
//    }

    private void findViews() {
        mAdjustImage = (ImageView) findViewById(R.id.adjust_image);
        mBottomNavigationContainer = (LinearLayout) findViewById(R.id.bottom_navigation_container);
        mAdjustmentImageBar = (AdjustImageBar) findViewById(R.id.adjust_image_bar);
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
        initBottomColorItems();
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
            mBottomNavigationMainItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_adjust_item_contrast), R.drawable.ic_contrast));
        }
    }

    private void initBottomNavigationAdjustItems() {
        if (mBottomNavigationAdjustItems == null || mBottomNavigationAdjustItems.size() == 0) {
            mBottomNavigationAdjustItems = new ArrayList<>();
            mBottomNavigationAdjustItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_adjust_item_blur), R.drawable.ic_blur));
            mBottomNavigationAdjustItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_adjust_item_grayscale), R.drawable.ic_blur));
            mBottomNavigationAdjustItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_adjust_item_negative), R.drawable.ic_blur));
        }
    }

    private void initBottomColorItems() {
        if (mBottomNavigationColorItems == null || mBottomNavigationColorItems.size() == 0) {
            mBottomNavigationColorItems = new ArrayList<>();
            mBottomNavigationColorItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_green_color), R.drawable.ic_color_red));
            mBottomNavigationColorItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_blue_color), R.drawable.ic_color_blue));
            mBottomNavigationColorItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_red_color), R.drawable.ic_color_green));
            mBottomNavigationColorItems.add(new BottomNavigationItem(this,
                    getString(R.string.bottom_navigation_alpha_color), R.drawable.ic_color_alpha));
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

    private void showItemApplyEffect() {
        if (mItemApplyEffect != null && !mItemApplyEffect.isVisible()) {
            mItemApplyEffect.setVisible(true);
        }
    }

    private void saveProcessedImage() {
        if (PermissionUtil.isStoragePermissionGranted(this)) {
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
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error).setMessage(R.string.cannot_save).show();
        }
    }

    public void setEffect(EditingEffect effect) {
        if (mProcessedImage == null) mProcessedImage = mFrame.getFrame();
        mHandlingImageAsynctask = new HandlingImageAsyncTask(effect, mProcessedImage, mProgressDialog);
        mHandlingImageAsynctask.setOnProgressListener(this);
        mHandlingImageAsynctask.execute();
    }

    @Override
    public void onFinish(Bitmap bitmap) {
        mOriginImage = bitmap;
        mAdjustImage.setImageBitmap(mOriginImage);
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.loading_effect));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mRotationEffect = new RotationEffect();
        mGrayScaleEffect = new GrayScaleEffect();
        mNegativeEffect = new NegativeEffect();
    }

    @Override
    public void onButtonClickListener(@AdjustImageBar.AdjustImageBarButtonDef int button) {
        switch (button) {
            case AdjustImageBar.BUTTON_COMPLETE:
//                saveImage();
                break;
            case AdjustImageBar.BUTTON_CANCEL:
                mAdjustmentImageBar.setVisibility(View.INVISIBLE);
                mProcessedImage = mFrame.getFrame();
                mAdjustImage.setImageBitmap(mProcessedImage);
                break;
        }
    }

    @Override
    public void onSeekBarValueChange(int progress) {
        if (mProcessedImage == null) mProcessedImage = mFrame.getFrame();
        float progressColorValue = progress - IMAGE_VALUE;
        switch (mEffectType) {
            case RED:
                mColorEffect = new ColorEffect(progressColorValue, ColorEffect.Type.RED);
                break;
            case BLUE:
                mColorEffect = new ColorEffect(progressColorValue, ColorEffect.Type.BLUE);
                break;
            case GREEN:
                mColorEffect = new ColorEffect(progressColorValue, ColorEffect.Type.GREEN);
                break;
            case ALPHA:
                mColorEffect = new ColorEffect(progressColorValue, ColorEffect.Type.ALPHA);
                break;
            case CONTRAST:
                mContrastEffect = new ContrastEffect(progress);
                break;
        }
        if (mEffectType == CONTRAST) {
            setEffect(mContrastEffect);
        } else {
            setEffect(mColorEffect);
        }
    }
}
