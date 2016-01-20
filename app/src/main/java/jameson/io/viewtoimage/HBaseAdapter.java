package jameson.io.viewtoimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by jameson on 12/19/15.
 */
public abstract class HBaseAdapter<T> extends BaseAdapter {
    private Context mContext;
    private List<T> mList;

    public HBaseAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return this.mList != null ? mList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return this.mList != null ? this.mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(onNewItemViewRes(), parent, false);
        }

        onBindItemView(convertView, mList.get(position), position, parent);
        return convertView;
    }

    public abstract int onNewItemViewRes();

    public abstract void onBindItemView(View convertView, T data, int position, ViewGroup parent);

    public void notifyDataSetChanged(List<T> list) {
        this.mList = list;
        super.notifyDataSetChanged();
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getList() {
        return mList;
    }
}
