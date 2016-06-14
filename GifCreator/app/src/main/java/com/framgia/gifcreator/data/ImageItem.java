package com.framgia.gifcreator.data;

import android.graphics.Bitmap;

/**
 * Created by VULAN on 6/3/2016.
 */
public class ImageItem {

    private String mImagePath;
    private String mAlbumId;
    private Bitmap mImage;
    private int mRequestCode;

    public ImageItem(String mImagePath) {
        this.mImagePath = mImagePath;
        mRequestCode = Constants.REQUEST_GALLERY;
    }

    public ImageItem(Bitmap image) {
        mImage = image;
        mRequestCode = Constants.REQUEST_CAMERA;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(String mAlbumId) {
        this.mAlbumId = mAlbumId;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public int getRequestCode() {
        return mRequestCode;
    }
}
