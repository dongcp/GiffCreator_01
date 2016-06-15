package com.framgia.gifcreator.data;

import android.graphics.Bitmap;

/**
 * Created by yue on 14/06/2016.
 */
public class Frame {

    private String mPhotoPath;
    private Bitmap mFrame;

    public Frame() {
    }

    public Frame(String photoPath) {
        mPhotoPath = photoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setFrame(Bitmap frame) {
        mFrame = frame;
    }

    public Bitmap getFrame() {
        return mFrame;
    }

    public void destroy() {
        if (mFrame != null) mFrame.recycle();
    }
}
