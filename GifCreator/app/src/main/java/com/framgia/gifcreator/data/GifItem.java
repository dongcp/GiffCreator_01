package com.framgia.gifcreator.data;

/**
 * Created by yue on 30/06/2016.
 */
public class GifItem extends Gif {

    private boolean mIsChosen;
    private boolean mEnableCheckbox;

    public GifItem(String gifPath) {
        super(gifPath);
    }

    public boolean isChosen() {
        return mIsChosen;
    }

    public void setChosen(boolean isChosen) {
        mIsChosen = isChosen;
    }

    public boolean isCheckboxEnabled() {
        return mEnableCheckbox;
    }

    public void showCheckbox(boolean isCheckboxEnabled) {
        mEnableCheckbox = isCheckboxEnabled;
    }
}
