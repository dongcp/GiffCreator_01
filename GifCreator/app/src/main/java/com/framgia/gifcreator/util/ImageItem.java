package com.framgia.gifcreator.util;

/**
 * Created by VULAN on 6/3/2016.
 */
public class ImageItem {
    private String mImagePath;
    private String mAlbumId;

    public ImageItem(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public ImageItem(String mImagePath, String albumId) {
        this.mImagePath = mImagePath;
        this.mAlbumId = albumId;
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
}
