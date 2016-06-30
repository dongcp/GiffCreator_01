package com.framgia.gifcreator.data;

import android.graphics.Bitmap;

/**
 * Created by yue on 14/06/2016.
 */
public class Frame {

    private String mPhotoPath;
    private Bitmap mFrame;
    private boolean mIsChosen;

    public Frame() {
    }

    public Frame(String photoPath) {
        mPhotoPath = photoPath;
    }

    public Frame(String photoPath, Bitmap frame) {
        mPhotoPath = photoPath;
        mFrame = frame;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public Bitmap getFrame() {
        return mFrame;
    }

    public void setFrame(Bitmap frame) {
        mFrame = frame;
    }

    public void destroy() {
        if (mFrame != null) {
            mFrame.recycle();
            mFrame = null;
        }
    }

    public void setChecked(boolean isChosen) {
        this.mIsChosen = isChosen;
    }

    public boolean isChosen() {
        return mIsChosen;
    }
}
