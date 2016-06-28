package com.framgia.gifcreator.data;

/**
 * Created by yue on 28/06/2016.
 */
public class Gif {

    private String mGifPath;
    private boolean mIsPlaying;

    public Gif(String gifPath) {
        mGifPath = gifPath;
    }

    public String getGifPath() {
        return mGifPath;
    }

    public void setState(boolean isPlaying) {
        mIsPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }
}
