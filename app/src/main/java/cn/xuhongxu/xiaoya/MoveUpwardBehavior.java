package cn.xuhongxu.xiaoya;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by xuhongxu on 16/4/23.
 *
 * MoveUpwardBehavior
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class MoveUpwardBehavior extends AppBarLayout.ScrollingViewBehavior {

    public MoveUpwardBehavior() {
        super();
    }

    public MoveUpwardBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            return super.onDependentViewChanged(parent, child, dependency);
        } else {
            FrameLayout l = (FrameLayout) child;
            if (l.getChildCount() < 1) {
                return false;
            }
            View v = l.getChildAt(0);
            if (v instanceof SwipeRefreshListFragment.ListFragmentSwipeRefreshLayout) {
                int translationY = Math.round(Math.max(0, (float) dependency.getHeight()
                        - dependency.getTranslationY()));
                child.setPadding(0, 0, 0, translationY);
                return true;
            }
        }
        return false;
    }

}
