package com.framgia.gifcreator.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yue on 07/06/2016.
 */
public class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {

    public final static int TASK_DECODE_FILE = 1;
    public final static int TASK_DECODE_BITMAP = 2;
    private WeakReference<ImageView> mImage;
    private int mReqWidth;
    private int mReqHeight;

    public BitmapWorkerTask(ImageView image) {
        mImage = new WeakReference<ImageView>(image);
    }

    @Override
    protected void onPreExecute() {
        ImageView image = mImage.get();
        mReqWidth = image.getWidth();
        mReqHeight = image.getHeight();
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        switch ((int) params[0]) {
            case TASK_DECODE_FILE:
                return BitmapDecoder.decodeFile((String) params[1], mReqWidth, mReqHeight);
            case TASK_DECODE_BITMAP:
                return BitmapDecoder.decodeByteArray((Bitmap) params[1], mReqWidth, mReqHeight);
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImage != null && bitmap != null) {
            ImageView image = mImage.get();
            if (image != null) image.setImageBitmap(bitmap);
        }
    }
}
