package jameson.io.viewtoimage.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jameson on 1/19/16.
 */
public class ViewUtil {

    /**
     * 返回View的矩阵
     *
     * @param rect
     * @param view
     * @return
     */
    public static RectF getOnScreenRectRelativeParent(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    /**
     * 返回View的矩阵(减去statusBar高度)
     *
     * @param rect
     * @param view
     * @return
     */
    public static Rect getOnScreenRect(Rect rect, View view) {
        return getOnScreenRect(rect, view, false);
    }

    /**
     * 返回View的矩阵(减去statusBar高度)
     *
     * @param view
     * @return
     */
    public static Rect getOnScreenRect(View view) {
        Rect rect = new Rect();
        return getOnScreenRect(rect, view, false);
    }

    /**
     * 返回View的矩阵(减去statusBar高度)
     *
     * @param rect
     * @param view
     * @return
     */
    public static Rect getOnScreenRect(Rect rect, View view, boolean removePadding) {
        final int[] location = new int[2];
        view.getLocationOnScreen(location);

        Rect windowRect = new Rect();
        view.getWindowVisibleDisplayFrame(windowRect);
        int statusBarHeight = windowRect.top;

        rect.set(location[0], location[1] - statusBarHeight, location[0] + view.getWidth(), location[1] - statusBarHeight + view.getHeight());

        if (removePadding) {
            rect.set(rect.left - view.getPaddingLeft(), rect.top - view.getPaddingTop(), rect.right - view.getPaddingRight(), rect.bottom - view.getPaddingBottom());
        }
        return rect;
    }

    /**
     * 获取可见的headerViewCount
     *
     * @param listView
     * @return
     */
    public static int getVisibleHeaderViewCount(ListView listView) {
        int headerViewCount = listView.getHeaderViewsCount();
        int visibleCount = 0;
        for (int i = 0; i < headerViewCount; i++) {
            View view = listView.getChildAt(i);
            if (view.getVisibility() == View.VISIBLE) {
                visibleCount++;
            }
        }

        return visibleCount;
    }

    /**
     * 获取可见的footerViewCount
     *
     * @param listView
     * @return
     */
    public static int getVisibleFooterViewCount(ListView listView) {
        int footerViewsCount = listView.getFooterViewsCount();
        int visibleCount = 0;
        for (int i = 0; i < footerViewsCount; i++) {
            View view = listView.getChildAt(i);
            if (view.getVisibility() == View.VISIBLE) {
                visibleCount++;
            }
        }

        return visibleCount;
    }

    /**
     * 返回ListView高度
     * @param listView
     * @return
     */
    public static int getListViewHeight(ListView listView) {
        int totalHeight = 0;
        ListAdapter adapter = listView.getAdapter();
        for (int i = 0, len = adapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }
        totalHeight += (listView.getDividerHeight() * (listView.getCount() - 1));

        return totalHeight;
    }

    public static int getViewMeasuredHeight(ViewGroup viewGroup) {
        int totalHeight = 0;
        int height;
        int size = viewGroup.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ListView) {
                height = getListViewHeight((ListView) child);
            } else {
                height = child.getMeasuredHeight();
            }

            totalHeight += height;
        }

        return totalHeight;
    }

    /**
     * viewGroup转换Bitmap
     * @param viewGroup
     * @return
     */
    public static Bitmap generateBigBitmap(final ViewGroup viewGroup) {
        List<BitmapWithHeight> list = getWholeViewToBitmap(viewGroup);

        int width = viewGroup.getMeasuredWidth();
        int height = 0;
        for (BitmapWithHeight item : list) {
            height += item.height;
        }

        return generateBigBitmap(list, width, height);
    }

    /**
     * List<BitmapWithHeight>转换Bitmap
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
            bmp.recycle();
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
        List<BitmapWithHeight> list = new ArrayList<>();
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ListView) {
                list.addAll(getWholeListViewItemsToBitmap((ListView) child));
            } else if (child instanceof AbsListView) {
                // TODO scrollView, GridView等
            } else {
                list.add(getSimpleViewToBitmap(child));
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
    public static BitmapWithHeight getSimpleViewToBitmap(final View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
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
            childView.measure(View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();

            list.add(new BitmapWithHeight(childView.getDrawingCache(), childView.getMeasuredHeight()));
//            list.add(Bitmap.createBitmap(childView.getDrawingCache(true).copy(Bitmap.Config.ARGB_4444, false)));
//            list.add(childView.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false));
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
