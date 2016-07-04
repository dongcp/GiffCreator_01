package com.framgia.gifcreator.util;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.framgia.gifcreator.effect.EditingEffect;

public class HandlingImageAsyncTask extends AsyncTask<Void, Void, Void> {

    private Bitmap mBaseBitmap;
    private EditingEffect mEditingEffect;
    private ProgressDialog mProgressDialog;
    private OnProgressListener mOnProgressListener;

    public HandlingImageAsyncTask(EditingEffect mEditingEffect, Bitmap mBaseBitmap, ProgressDialog mProgressDialog) {
        this.mEditingEffect = mEditingEffect;
        this.mBaseBitmap = mBaseBitmap;
        this.mProgressDialog = mProgressDialog;
    }

    @Override
    protected void onPreExecute() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mEditingEffect != null) {
            mBaseBitmap = mEditingEffect.applyEffect(mBaseBitmap);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mOnProgressListener != null) {
            mOnProgressListener.onHandleFinish(mBaseBitmap);
        }
        super.onPostExecute(aVoid);
    }

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }

    public interface OnProgressListener {
        void onHandleFinish(Bitmap bitmap);
    }
}
