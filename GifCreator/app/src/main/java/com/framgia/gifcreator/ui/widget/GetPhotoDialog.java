package com.framgia.gifcreator.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.framgia.gifcreator.R;

/**
 * Created by yue on 27/06/2016.
 */
public class GetPhotoDialog {

    public final static int TYPE_CAMERA = 0,
            TYPE_GALLERY = 1,
            TYPE_FACEBOOK = 2;
    private Context mContext;
    private OnDialogItemChooseListener mOnDialogItemChooseListener;

    public GetPhotoDialog(Context context) {
        mContext = context;
    }

    public void setOnDialogItemChooseListener(OnDialogItemChooseListener onDialogItemChooseListener) {
        mOnDialogItemChooseListener = onDialogItemChooseListener;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(R.array.choose_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOnDialogItemChooseListener != null) {
                    mOnDialogItemChooseListener.onDialogItemChoose(which);
                }
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public interface OnDialogItemChooseListener {
        void onDialogItemChoose(int type);
    }
}
