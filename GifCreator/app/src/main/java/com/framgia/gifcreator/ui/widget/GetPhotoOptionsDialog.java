package com.framgia.gifcreator.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.Window;

import com.framgia.gifcreator.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yue on 09/06/2016.
 */
public class GetPhotoOptionsDialog extends Dialog {

    public final static int BUTTON_GET_FACEBOOK_PHOTO = 1;
    public final static int BUTTON_GET_CAMERA_PHOTO = 2;
    public final static int BUTTON_GET_GALLERY_PHOTO = 3;
    private OnGetPhotoDialogButtonClickListener mListener;

    public GetPhotoOptionsDialog(Context context) {
        super(context);
    }

    public void setOnGetPhotoDialogButtonClickListener(OnGetPhotoDialogButtonClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_get_photo_options);
        findViewById(R.id.button_get_facebook_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onGetPhotoDialogButtonClick(BUTTON_GET_FACEBOOK_PHOTO);
                }
            }
        });
        findViewById(R.id.button_get_camera_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onGetPhotoDialogButtonClick(BUTTON_GET_CAMERA_PHOTO);
                }
            }
        });
        findViewById(R.id.button_get_gallery_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onGetPhotoDialogButtonClick(BUTTON_GET_GALLERY_PHOTO);
                }
            }
        });
    }

    public interface OnGetPhotoDialogButtonClickListener {
        void onGetPhotoDialogButtonClick(@GetPhotoDialogButtonDef int button);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            BUTTON_GET_FACEBOOK_PHOTO,
            BUTTON_GET_CAMERA_PHOTO,
            BUTTON_GET_GALLERY_PHOTO
    })
    public @interface GetPhotoDialogButtonDef {
    }
}
