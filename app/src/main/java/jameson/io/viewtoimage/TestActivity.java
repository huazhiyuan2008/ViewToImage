package jameson.io.viewtoimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jameson.io.viewtoimage.util.LogUtils;
import jameson.io.viewtoimage.util.MeasureUtil;
import jameson.io.viewtoimage.util.ScreenUtil;

/**
 * Created by jameson on 1/27/16.
 */
public class TestActivity extends Activity {

    private ImageView mImageView;
    private TextView mTextView;
    private ViewGroup mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
    }

    private void initView() {
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mImageView = (ImageView) findViewById(R.id.image);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                capture(null);
            }
        }, 200);
    }

    public Bitmap getSimpleViewToBitmap(final View view, int width) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            LogUtils.e("onMeasure");
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            LogUtils.i(view.getPaddingLeft() + ", " + view.getPaddingTop());
            LogUtils.w(MeasureUtil.getViewSizeInfo(view));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            // view.layout(0, 0, 200, 200);
        }
        LogUtils.w(MeasureUtil.getViewSizeInfo(view));

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    public void capture(View view) {
        final int width = ScreenUtil.getScreenWidth(TestActivity.this); //textView.getWidth();
        mImageView.setImageBitmap(getSimpleViewToBitmap(mTextView, width));
    }

    public void captureInflate(View view) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.layout_textview, mRootView, false);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final TextView textView = (TextView) rootView.findViewById(R.id.textView);
        LogUtils.d(MeasureUtil.getViewSizeInfo(rootView));
        LogUtils.d(MeasureUtil.getViewSizeInfo(textView));

        final int width = ScreenUtil.getScreenWidth(TestActivity.this) - ScreenUtil.dip2px(this, 20); //textView.getWidth();
        mImageView.setImageBitmap(getSimpleViewToBitmap(textView, width));
    }
}
