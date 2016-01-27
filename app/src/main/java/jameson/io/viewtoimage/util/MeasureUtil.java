package jameson.io.viewtoimage.util;

import android.view.View;

/**
 * Created by jameson on 1/10/16.
 */
public class MeasureUtil {

    public static int getSpecSize(int measureSpec) {
        return View.MeasureSpec.getSize(measureSpec);
    }

    public static String getSpecModeName(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        if (specMode == View.MeasureSpec.EXACTLY) {
            return "EXACTLY";
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            return "AT_MOST";
        } else {
            return "UNSPECIFIED";
        }
    }

    public static int resolveSize(int size, int measureSpec) {
        int result = size;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                result = size;
                break;

            case View.MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;

            case View.MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    /**
     * 获取View的width, height信息
     *
     * @param view
     * @return
     */
    public static String getViewSizeInfo(View view) {
        if (view == null) {
            return "null";
        }

        return String.format("width=%s, measuredWidth=%s, height=%s, measuredHeight=%s", view.getWidth(), view.getMeasuredWidth(), view.getHeight(), view.getMeasuredHeight());
    }
}
