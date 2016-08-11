package cn.xuhongxu.xiaoya.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * Created by xuhon on 2016/8/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class YaHorizontalScrollView extends HorizontalScrollView {
    public YaHorizontalScrollView(Context context) {
        super(context);
    }
    public YaHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YaHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public YaHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface OnScrollChangedListener{
        public void onScrollChanged(int x, int y, int oldxX, int oldY);
    }

    private OnScrollChangedListener onScrollChangedListener;

    public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener){
        this.onScrollChangedListener=onScrollChangedListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY){
        super.onScrollChanged(x, y, oldX, oldY);
        if(onScrollChangedListener!=null){
            onScrollChangedListener.onScrollChanged(x, y, oldX, oldY);
        }
    }
}
