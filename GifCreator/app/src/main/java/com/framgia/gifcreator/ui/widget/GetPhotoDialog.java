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
            TYPE_REMOVE = 2;
    private Context mContext;
    private OnDialogItemChooseListener mOnDialogItemChooseListener;
    private CharSequence[] mDialogItems;

    public GetPhotoDialog(Context context) {
        this(context, false);
    }

    public GetPhotoDialog(Context context, boolean enableRemoveButton) {
        mContext = context;
        if (enableRemoveButton) {
            mDialogItems = new CharSequence[3];
            mDialogItems[0] = mContext.getString(R.string.get_photo_from_camera);
            mDialogItems[1] = mContext.getString(R.string.get_photo_from_gallery);
            mDialogItems[2] = mContext.getString(R.string.remove_frame);
        } else {
            mDialogItems = new CharSequence[2];
            mDialogItems[0] = mContext.getString(R.string.get_photo_from_camera);
            mDialogItems[1] = mContext.getString(R.string.get_photo_from_gallery);
        }
    }

    public void setOnDialogItemChooseListener(
            OnDialogItemChooseListener onDialogItemChooseListener) {
        mOnDialogItemChooseListener = onDialogItemChooseListener;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(mDialogItems, new DialogInterface.OnClickListener() {
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
