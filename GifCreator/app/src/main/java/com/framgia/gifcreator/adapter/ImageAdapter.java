package com.framgia.gifcreator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Constants;
import com.framgia.gifcreator.data.ImageItem;
import com.framgia.gifcreator.util.BitmapWorkerTask;

import java.util.ArrayList;

/**
 * Created by VULAN on 6/6/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<ImageItem> mListImage;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public ImageAdapter(Context mContext, ArrayList<ImageItem> mListImage) {
        this.mListImage = mListImage;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageItem imageItem = mListImage.get(position);
        BitmapWorkerTask decodeFileTask = new BitmapWorkerTask(holder.mImageView,
                mContext.getResources().getDimensionPixelSize(R.dimen.image_item_width),
                mContext.getResources().getDimensionPixelSize(R.dimen.image_item_height));
        switch (imageItem.getRequestCode()) {
            case Constants.REQUEST_GALLERY:
                decodeFileTask.execute(BitmapWorkerTask.TASK_DECODE_FILE, imageItem.getImagePath());
                break;
            case Constants.REQUEST_CAMERA:
                decodeFileTask.execute(BitmapWorkerTask.TASK_DECODE_BITMAP, imageItem.getImage());
                break;
        }
        holder.mImageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    holder.mImageRemove.setVisibility(View.VISIBLE);
                    mOnItemClickListener.onRemoveItem(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListImage.size() > 0 ? mListImage.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ImageView mImageRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_choosing);
            mImageRemove = (ImageView) itemView.findViewById(R.id.image_remove);
        }
    }

    public interface OnItemClickListener {
        void onRemoveItem(int position);
    }
}
