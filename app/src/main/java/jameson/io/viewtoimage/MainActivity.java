package jameson.io.viewtoimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jameson.io.viewtoimage.util.LogUtils;
import jameson.io.viewtoimage.util.ScreenUtil;
import jameson.io.viewtoimage.util.ViewToImageUtil;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mCaptureView;
    private ListView mListView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        captureInflateView();
    }

    private void initViews() {
        mCaptureView = (ViewGroup) findViewById(R.id.capture_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            list.add("item " + i);
        }
        MainAdapter adapter = new MainAdapter(this, list);
        mListView.setAdapter(adapter);
        mCaptureView.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(String.format("width=%s, height=%s, measureWidth=%s, measureHeight=%s", mCaptureView.getWidth(), mCaptureView.getHeight(), mCaptureView.getMeasuredWidth(), mCaptureView.getMeasuredHeight()));
            }
        });
    }

    /**
     * 截屏已渲染完视图
     */
    public void capture(View view) {
        int width = 0;//ScreenUtil.getScreenWidth(this);
        ViewToImageUtil.generateImage(mCaptureView, width, 0xfff8f8f8, new ViewToImageUtil.OnImageSavedCallback() {
            @Override
            public void onFinishCallback(String path) {
                Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                LogUtils.d(path);
            }
        });
    }

    /**
     * 截屏inflate视图
     */
    private void captureInflateView() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.layout_listview, null, false);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(this), LinearLayout.LayoutParams.WRAP_CONTENT));
        // rootView.getLayoutParams().width = ScreenUtil.getScreenWidth(this);
        rootView.requestLayout();

        final ViewGroup mCaptureView = (ViewGroup) rootView.findViewById(R.id.capture_layout);
        ListView mListView = (ListView) rootView.findViewById(R.id.listView);
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("item " + i);
        }
        MainAdapter adapter = new MainAdapter(this, list);
        mListView.setAdapter(adapter);

        mCaptureView.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(String.format("width=%s, height=%s, measureWidth=%s, measureHeight=%s", mCaptureView.getWidth(), mCaptureView.getHeight(), mCaptureView.getMeasuredWidth(), mCaptureView.getMeasuredHeight()));
            }
        });

        int width = ScreenUtil.getScreenWidth(this);
        ViewToImageUtil.generateImage(mCaptureView, width, 0xfff8f8f8, new ViewToImageUtil.OnImageSavedCallback() {
            @Override
            public void onFinishCallback(String path) {
                Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                LogUtils.d(path);
            }
        });
    }

    public void measure(View view) {

    }
}
