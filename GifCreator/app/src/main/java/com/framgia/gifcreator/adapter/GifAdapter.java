package com.framgia.gifcreator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.framgia.gifcreator.R;
import com.framgia.gifcreator.data.Gif;

import java.util.List;

/**
 * Created by yue on 27/06/2016.
 */
public class GifAdapter extends RecyclerView.Adapter<GifAdapter.ItemHolder> {

    private Context mContext;
    private List<Gif> mGifs;
    private boolean mIsPlayingGif;

    public GifAdapter(Context context, List<Gif> gifs) {
        mContext = context;
        mGifs = gifs;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gif, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final Gif gif = mGifs.get(position);
        if (gif.isPlaying()) {
            Glide.with(mContext).load(gif.getGifPath()).asGif().into(holder.mGif);
        } else {
            Glide.with(mContext).load(gif.getGifPath()).asBitmap().into(holder.mGif);
        }
        holder.mImageLoadGif.setVisibility(gif.isPlaying() ? View.GONE : View.VISIBLE);
        holder.mButtonPlayGif.setTag(position);
        holder.mButtonPlayGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayingGif && !gif.isPlaying()) {
                    resetGifState();
                    mIsPlayingGif = false;
                }
                if (!mIsPlayingGif && !gif.isPlaying()) {
                    setPlaying(gif, true);
                } else {
                    setPlaying(gif, false);
                }
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGifs != null ? mGifs.size() : 0;
    }

    private void resetGifState() {
        int size = mGifs.size();
        for (int i = 0; i < size; i++) {
            if (mGifs.get(i).isPlaying()) {
                mGifs.get(i).setState(false);
                notifyItemChanged(i);
                break;
            }
        }
    }

    private void setPlaying(Gif gif, boolean isPlaying) {
        gif.setState(isPlaying);
        mIsPlayingGif = isPlaying;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public ImageView mGif;
        public ImageView mImageLoadGif;
        public FrameLayout mButtonPlayGif;

        public ItemHolder(View itemView) {
            super(itemView);
            mGif = (ImageView) itemView.findViewById(R.id.image_gif);
            mImageLoadGif = (ImageView) itemView.findViewById(R.id.image_load_gif);
            mButtonPlayGif = (FrameLayout) itemView.findViewById(R.id.button_play_gif);
        }
    }
}
