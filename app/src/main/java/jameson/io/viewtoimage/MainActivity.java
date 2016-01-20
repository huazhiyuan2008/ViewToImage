package jameson.io.viewtoimage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jameson.io.viewtoimage.util.FileUtil;
import jameson.io.viewtoimage.util.LogUtils;
import jameson.io.viewtoimage.util.ScreenUtil;
import jameson.io.viewtoimage.util.ViewUtil;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mCaptureView;
    private ListView mListView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.d("screen===" + ScreenUtil.getScreenWidth(this) + ", " + ScreenUtil.getScreenHeight(this));
        initViews();
        // 清空目录
        File file = new File(FileUtil.getBasePath(this));
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                childFile.delete();
            }
        }
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

    private void saveFile(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String destPath = FileUtil.createImageFile(MainActivity.this).getAbsolutePath();
                LogUtils.i("destPath====" + destPath);
                FileUtil.compressBitmapToFile(bitmap, destPath);
                bitmap.recycle();
            }
        }).start();
    }

    public void capture(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ViewUtil.generateBigBitmap(mCaptureView);
                // mImageView.setImageBitmap(bitmap);
                saveFile(bitmap);
            }
        }).start();

    }

}
