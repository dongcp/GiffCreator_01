package com.framgia.gifcreator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.gifcreator.R;
import com.framgia.gifcreator.util.BitmapWorkerTask;
import com.framgia.gifcreator.util.ImageItem;

import java.util.ArrayList;

/**
 * Created by VULAN on 6/6/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<ImageItem> mListImage;
    private Context mContext;
    private OnItemClicklistener mOnItemClicklistener;

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
        BitmapWorkerTask decodeFileTask = new BitmapWorkerTask(holder.mImageView);
        decodeFileTask.execute(BitmapWorkerTask.TASK_DECODE_FILE, imageItem.getImagePath());
        holder.mImageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClicklistener != null) {
                    holder.mImageRemove.setVisibility(View.VISIBLE);
                    mOnItemClicklistener.onRemoveItem(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListImage.size() > 0) return mListImage.size();
        return 0;
    }

    public void setOnItemClicklistener(OnItemClicklistener mOnItemClicklistener) {
        this.mOnItemClicklistener = mOnItemClicklistener;
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

    public interface OnItemClicklistener {
        void onRemoveItem(int position);
    }
}
