package com.framgia.gifcreator.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.gifcreator.R;

/**
 * Created by yue on 08/06/2016.
 */
public class BottomNavigationItem {

    private View mView;
    private ImageView mNavigationItemImage;
    private TextView mNavigationItemTitle;
    private View mItemSeparator;
    private OnBottomNavigationItemClickListener mListener;

    public BottomNavigationItem(Context context) {
        mView = View.inflate(context, R.layout.item_bottom_navigation, null);
        findViews(mView);
    }

    public BottomNavigationItem(Context context, String title, int imageResource) {
        mView = View.inflate(context, R.layout.item_bottom_navigation, null);
        findViews(mView);
        mNavigationItemTitle.setText(title);
        mNavigationItemImage.setImageResource(imageResource);
    }

    public View getView() {
        return mView;
    }

    public String getTitle() {
        return mNavigationItemTitle.getText().toString();
    }

    public void setTitle(String title) {
        mNavigationItemTitle.setText(title);
    }

    public void setImage(int imageResource) {
        mNavigationItemImage.setImageResource(imageResource);
    }

    public void hideSeparator() {
        mItemSeparator.setVisibility(View.GONE);
    }

    public void setOnBottomNavigationItemClickListener(
            OnBottomNavigationItemClickListener onBottomNavigationItemClickListener) {
        mListener = onBottomNavigationItemClickListener;
    }

    private void findViews(View rootView) {
        mNavigationItemImage = (ImageView) rootView.findViewById(R.id.navigation_item_image);
        mNavigationItemTitle = (TextView) rootView.findViewById(R.id.navigation_item_title);
        mItemSeparator = rootView.findViewById(R.id.item_separator);
        rootView.findViewById(R.id.navigation_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(BottomNavigationItem.this);
                }
            }
        });
    }

    public interface OnBottomNavigationItemClickListener {
        void onItemClick(BottomNavigationItem item);
    }
}
