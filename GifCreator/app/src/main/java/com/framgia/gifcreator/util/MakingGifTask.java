package com.framgia.gifcreator.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yue on 16/06/2016.
 */
public class MakingGifTask extends AsyncTask<String[], Void, String> {

    private final String GIF_PREFIX = "GIF";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private OnFinishMakingGif mOnFinishMakingGif;
    private Display mDisplay;
    private int mFps;
    private int mMinHeight;
    private int mMinWidth;

    public MakingGifTask(Context context, int fps) {
        mContext = context;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(context.getString(R.string.making_gif));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mFps = fps;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
        Point size = new Point();
        mDisplay.getSize(size);
        mMinWidth = size.x;
        mMinHeight = size.y;
    }

    public void setOnFinishMakingGif(OnFinishMakingGif onFinishMakingGif) {
        mOnFinishMakingGif = onFinishMakingGif;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String[]... params) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.setFrameRate(mFps);
        gifEncoder.start(bos);
        getAppropriateSize(params[0]);
        for (String photoPath : params[0]) {
            Bitmap frame;
            if (!TextUtils.isEmpty(photoPath)) {
                frame = BitmapHelper.decodeFile(photoPath, mMinWidth, mMinHeight);
                gifEncoder.addFrame(frame);
                frame.recycle();
            }
        }
        gifEncoder.finish();
        byte[] gifData = bos.toByteArray();
        try {
            String timeStamp = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
            String gifFilePath = FileUtil.getAppFolderPath(mContext) +
                    File.separator + GIF_PREFIX + timeStamp + Constants.GIF_EXTENSION;
            FileOutputStream outStream = new FileOutputStream(gifFilePath);
            outStream.write(gifData);
            outStream.close();
            return gifFilePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgressDialog.dismiss();
        if (mOnFinishMakingGif != null) {
            mOnFinishMakingGif.onFinishMakingGif(s);
        }
    }

    private void getAppropriateSize(String[] photoPaths) {
        for (String photoPath : photoPaths) {
            int[] size = BitmapHelper.getImageSize(photoPath);
            if (mMinWidth > size[0]) mMinWidth = size[0];
            if (mMinHeight > size[1]) mMinHeight = size[1];
        }
    }

    public interface OnFinishMakingGif {
        void onFinishMakingGif(String gifPath);
    }
}
