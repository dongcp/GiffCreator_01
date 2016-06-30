package com.framgia.gifcreator.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.GifItem;
import com.framgia.gifcreator.util.listener.OnListItemInteractListener;

import java.util.List;

/**
 * Created by yue on 27/06/2016.
 */
public class GifAdapter extends RecyclerView.Adapter<GifAdapter.ItemHolder> {

    private Context mContext;
    private List<GifItem> mGifs;
    private boolean mIsSelecting;
    private OnListItemInteractListener mListener;

    public GifAdapter(Context context, List<GifItem> gifs) {
        mContext = context;
        mGifs = gifs;
    }

    public void setOnListItemInteractListener(OnListItemInteractListener listener) {
        mListener = listener;
    }

    public void setState(boolean isSelecting) {
        mIsSelecting = isSelecting;
    }

    public boolean isSelecting() {
        return mIsSelecting;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gif, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        final GifItem gif = mGifs.get(position);
        if (gif.isPlaying()) {
            Glide.with(mContext).load(gif.getGifPath()).asGif().into(holder.mGif);
        } else {
            Glide.with(mContext).load(gif.getGifPath()).asBitmap().into(holder.mGif);
        }
        if (gif.isCheckboxEnabled()) {
            holder.mCheckbox.setVisibility(View.VISIBLE);
            holder.mCheckbox.setChecked(gif.isChosen());
        } else {
            holder.mCheckbox.setVisibility(View.GONE);
        }
        holder.mImageLoadGif.setVisibility(gif.isPlaying() ? View.GONE : View.VISIBLE);
        holder.mButtonPlayGif.setTag(position);
        holder.mButtonPlayGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = (int) v.getTag();
                    mListener.onListItemClick(position);
                }
            }
        });
        holder.mButtonPlayGif.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null && !mIsSelecting) {
                    int position = (int) v.getTag();
                    mListener.onListItemLongClick(position);
                }
                return false;
            }
        });
        holder.mCheckbox.setTag(position);
        holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                GifItem gifItem = mGifs.get(position);
                mGifs.get(position).setChosen(!gifItem.isChosen());
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGifs != null ? mGifs.size() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public ImageView mGif;
        public ImageView mImageLoadGif;
        public LinearLayout mButtonPlayGif;
        public AppCompatCheckBox mCheckbox;

        public ItemHolder(View itemView) {
            super(itemView);
            mGif = (ImageView) itemView.findViewById(R.id.image_gif);
            mImageLoadGif = (ImageView) itemView.findViewById(R.id.image_load_gif);
            mButtonPlayGif = (LinearLayout) itemView.findViewById(R.id.button_play_gif);
            mCheckbox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
