package com.framgia.gifcreator.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Frame;

import java.util.List;

/**
 * Created by yue on 28/06/2016.
 */
public class LoadFrameTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private List<Frame> mFrames;
    private ProgressDialog mProgressDialog;
    private OnLoadCompleteListener mOnLoadCompleteListener;

    public LoadFrameTask(Context context, List<Frame> frame) {
        mContext = context;
        mFrames = frame;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.reading_data));
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        mOnLoadCompleteListener = onLoadCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        int size = mFrames.size();
        for (int i = 0; i < size; i++) {
            Frame frame = mFrames.get(i);
            frame.setFrame(BitmapHelper.decodeFile(frame.getPhotoPath(),
                    AppHelper.getDimen(mContext, R.dimen.preview_gif_image_width),
                    AppHelper.getDimen(mContext, R.dimen.preview_gif_image_height)));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressDialog.dismiss();
        if (mOnLoadCompleteListener != null) {
            mOnLoadCompleteListener.onLoadComplete();
        }
    }

    public interface OnLoadCompleteListener {
        void onLoadComplete();
    }
}
