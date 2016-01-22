package jameson.io.viewtoimage.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * View转换图片util
 * Created by jameson on 1/21/16.
 */
public class ViewToImageUtil {

    /**
     * 生成图片，返回图片本地路径
     * 建议viewGroup渲染完成后调用
     */
    public static void generateImage(final ViewGroup viewGroup, final OnImageSavedCallback mOnImageSavedCallback) {
        generateImage(viewGroup, 0, mOnImageSavedCallback);
    }

    /**
     * 生成图片，返回图片本地路径, width为0则用viewGroup.getMeasuredWidth
     * 建议Inflate出来的视图设置width
     */
    public static void generateImage(final ViewGroup viewGroup, final int width, final OnImageSavedCallback mOnImageSavedCallback) {
        final Bitmap bitmap = generateBigBitmap(viewGroup, width);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String destPath = FileUtil.createImageFile(viewGroup.getContext()).getAbsolutePath();
                LogUtils.d("destPath====" + destPath);
                FileUtil.compressBitmapToFile(bitmap, destPath);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnImageSavedCallback != null) {
                            mOnImageSavedCallback.onFinishCallback(destPath);
                        }
                    }
                });

                bitmap.recycle();
                System.gc();
            }
        }).start();
    }

    public interface OnImageSavedCallback {
        public void onFinishCallback(String path);
    }

    /**
     * viewGroup转换Bitmap
     *
     * @param viewGroup
     * @return
     */
    public static Bitmap generateBigBitmap(final ViewGroup viewGroup, int width) {
        if (width > 0) {
            viewGroup.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        } else if (viewGroup.getWidth() <= 0) {
            viewGroup.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        } else {
            width = viewGroup.getWidth();
        }

        width = width > 0 ? width : viewGroup.getMeasuredWidth();
        LogUtils.d(width + "");
        LogUtils.d(String.format("width=%s, measuredWidth=%s, height=%s, measuredHeight=%s",
                viewGroup.getWidth(), viewGroup.getMeasuredWidth(), viewGroup.getHeight(), viewGroup.getMeasuredHeight()));
        int height = 0;
        List<BitmapWithHeight> list = getWholeViewToBitmap(viewGroup);
        for (BitmapWithHeight item : list) {
            height += item.height;
        }
        return generateBigBitmap(list, width, height);
    }

    /**
     * List<BitmapWithHeight>转换Bitmap
     *
     * @param list
     * @param width
     * @param height
     * @return
     */
    public static Bitmap generateBigBitmap(List<BitmapWithHeight> list, int width, int height) {
        final Bitmap bigBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas bigCanvas = new Canvas(bigBitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (BitmapWithHeight item : list) {
            Bitmap bmp = item.bitmap;
            bigCanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            //bmp.recycle();
        }

        return bigBitmap;
    }

    /**
     * viewGroup 转换为 bitmap集合
     *
     * @param viewGroup
     * @return
     */
    public static List<BitmapWithHeight> getWholeViewToBitmap(final ViewGroup viewGroup) {
        int width = viewGroup.getMeasuredWidth();
        List<BitmapWithHeight> list = new ArrayList<>();
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ListView) {
                list.addAll(getWholeListViewItemsToBitmap((ListView) child));
            } else if (child instanceof AbsListView) {
                // TODO scrollView, GridView等
            } else {
                list.add(getSimpleViewToBitmap(child, width));
            }
        }

        return list;
    }

    /**
     * 普通View转换成bitmap, 如TextView，ImageView，Button等非ViewGroup, 非AbsListView
     *
     * @param view
     * @return Bitmap
     */
    public static BitmapWithHeight getSimpleViewToBitmap(final View view, int width) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return new BitmapWithHeight(view.getDrawingCache(), view.getMeasuredHeight());
    }

    /**
     * ListView转换成bitmap
     *
     * @param listView
     * @return List<Bitmap>
     */
    public static List<BitmapWithHeight> getWholeListViewItemsToBitmap(final ListView listView) {
        List<BitmapWithHeight> list = new ArrayList<>();
        if (listView == null || listView.getAdapter() == null) {
            return list;
        }

        ListAdapter adapter = listView.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View childView = adapter.getView(i, null, listView);
            list.add(getSimpleViewToBitmap(childView, listView.getMeasuredWidth()));
        }

        return list;
    }

    public static class BitmapWithHeight {
        public Bitmap bitmap;
        public int height;

        public BitmapWithHeight() {
        }

        public BitmapWithHeight(Bitmap bitmap, int height) {
            this.bitmap = bitmap;
            this.height = height;
        }
    }
}
