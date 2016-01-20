package jameson.io.viewtoimage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jameson on 1/19/16.
 */
public class MainAdapter extends HBaseAdapter<String> {
    public MainAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public int onNewItemViewRes() {
        return R.layout.item_text;
    }

    @Override
    public void onBindItemView(View convertView, String data, int position, ViewGroup parent) {
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(getList().get(position));
    }
}
