package com.framgia.gifcreator.ui.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.framgia.gifcreator.R;
<<<<<<< HEAD
import com.framgia.gifcreator.util.AppHelper;
=======
>>>>>>> show list created gif

/**
 * Created by yue on 28/06/2016.
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;

    public LinearItemDecoration(Context context) {
        mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = AppHelper.getDimen(mContext, R.dimen.common_size_5);
        } else {
            outRect.top = 0;
        }
        outRect.right = AppHelper.getDimen(mContext, R.dimen.common_size_5);
        outRect.left = AppHelper.getDimen(mContext, R.dimen.common_size_5);
        outRect.bottom = AppHelper.getDimen(mContext, R.dimen.common_size_5);
    }
}
