package com.framgia.gifcreator.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.framgia.gifcreator.data.Frame;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by yue on 07/06/2016.
 */
public class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {

    public final static int TASK_DECODE_FILE = 1;
    public final static int TASK_RESIZE_BITMAP = 2;
    private WeakReference<ImageView> mImage;
    private Frame mFrame;
    private int mReqWidth;
    private int mReqHeight;

    public BitmapWorkerTask(ImageView image, Frame frame) {
        mImage = new WeakReference<ImageView>(image);
        mFrame = frame;
    }

    public BitmapWorkerTask(ImageView image, Frame frame, int reqWidth, int reqHeight) {
        mImage = new WeakReference<ImageView>(image);
        mFrame = frame;
        mReqWidth = reqWidth;
        mReqHeight = reqHeight;
    }

    @Override
    protected void onPreExecute() {
        ImageView image = mImage.get();
        if (image.getWidth() != 0) mReqWidth = image.getWidth();
        if (image.getHeight() != 0) mReqHeight = image.getHeight();
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        switch ((int) params[0]) {
            case TASK_DECODE_FILE:
                return mFrame.getFrame() != null ? mFrame.getFrame() :
                        BitmapHelper.decodeFile((String) params[1], mReqWidth, mReqHeight);
            case TASK_RESIZE_BITMAP:
                try {
                    return mFrame.getFrame() != null ? mFrame.getFrame() :
                            BitmapHelper.resizeBitmap((Bitmap) params[1], mReqWidth, mReqHeight);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImage != null && bitmap != null) {
            ImageView image = mImage.get();
            mFrame.setFrame(bitmap);
            if (image != null) image.setImageBitmap(bitmap);
        }
    }
}
