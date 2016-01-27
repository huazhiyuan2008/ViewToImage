package jameson.io.viewtoimage.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    public static int mBackgroundColor = Color.TRANSPARENT;

    /**
     * 生成图片，返回图片本地路径
     * 建议viewGroup渲染完成后调用
     */
    public static void generateImage(final ViewGroup viewGroup, final OnImageSavedCallback mOnImageSavedCallback) {
        generateImage(viewGroup, 0, Color.TRANSPARENT, mOnImageSavedCallback);
    }

    /**
     * 生成图片，返回图片本地路径, width为0则用viewGroup.getMeasuredWidth
     * 建议Inflate出来的视图设置width
     *
     * @param viewGroup
     * @param width
     * @param backgroundColor {@code AARRGGBB}如Color.RED, 0xFFFF0000
     * @param mOnImageSavedCallback
     */
    public static void generateImage(final ViewGroup viewGroup, final int width, @ColorInt final int backgroundColor, final OnImageSavedCallback mOnImageSavedCallback) {
        mBackgroundColor = backgroundColor;
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
        List<BitmapWithHeight> list = getWholeViewToBitmap(viewGroup, new ArrayList<BitmapWithHeight>());
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
        bigCanvas.drawColor(mBackgroundColor);
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
     * view转换为 bitmap
     *
     * @param view
     * @param width
     * @return
     */
    public static BitmapWithHeight getWholeViewToBitmap(final View view, int width) {
        return getSimpleViewToBitmap(view, width);
    }

    /**
     * viewGroup 转换为 bitmap集合
     *
     * @param viewGroup
     * @return
     */
    public static List<BitmapWithHeight> getWholeViewToBitmap(final ViewGroup viewGroup, List<BitmapWithHeight> list) {
        int width = viewGroup.getMeasuredWidth();
        if (viewGroup instanceof ListView) {
            list.addAll(getWholeListViewItemsToBitmap((ListView) viewGroup));
        } else if (viewGroup instanceof RecyclerView) {
//                RecyclerView recyclerView = (RecyclerView) child;
//                list.addAll(getWholeRecyclerViewItemsToBitmap(recyclerView));
            list.add(getWholeViewToBitmap(viewGroup, width));
        } else if (viewGroup instanceof AbsListView) {
            // TODO scrollView, GridView等
            list.add(getWholeViewToBitmap(viewGroup, width));
        } else {
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                if (isScrollableView(child)) {
                    getWholeViewToBitmap((ViewGroup) child, list);
                } else {
                    list.add(getWholeViewToBitmap(child, width));
                }
            }
        }

        return list;
    }

    /**
     * 是否是可滚动视图
     *
     * @param viewGroup
     * @return
     */
    private static boolean isScrollableView(View viewGroup) {
        return viewGroup instanceof ListView || viewGroup instanceof AbsListView;
    }

    /**
     * 普通View转换成bitmap, 如TextView，ImageView，Button等非ViewGroup, 非AbsListView
     *
     * @param view
     * @return Bitmap
     */
    public static BitmapWithHeight getSimpleViewToBitmap(final View view, int width) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return new BitmapWithHeight(view.getDrawingCache(), view.getMeasuredHeight());
    }

    /**
     * RecyclerView转换成bitmap
     *
     * @param recyclerView
     * @return
     */
    public static List<BitmapWithHeight> getWholeRecyclerViewItemsToBitmap(final RecyclerView recyclerView) {
        List<BitmapWithHeight> list = new ArrayList<>();
        if (recyclerView == null || recyclerView.getAdapter() == null) {
            return list;
        }

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                int count = manager.getItemCount();
                LogUtils.w(count + "");
                for (int i = 0; i < count; i++) {
                    View childView = manager.findViewByPosition(i);
                    // TODO: 1/25/16  childView不可见部分为null，无法截长图
                    if (childView != null) {
                        list.add(getSimpleViewToBitmap(childView, recyclerView.getMeasuredWidth()));
                    }
                }
            } else {
                list.add(getSimpleViewToBitmap(recyclerView, recyclerView.getMeasuredWidth()));
            }
        }

        return list;
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
