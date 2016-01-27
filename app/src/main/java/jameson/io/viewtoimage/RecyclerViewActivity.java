package jameson.io.viewtoimage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import jameson.io.viewtoimage.util.LogUtils;
import jameson.io.viewtoimage.util.ViewToImageUtil;

/**
 * Created by jameson on 1/25/16.
 */
public class RecyclerViewActivity extends Activity {
    private RecyclerView mHorizontalRecyclerView;
    private RecyclerView mVerticalRecyclerView;
    private ViewGroup mHorizontalRecyclerViewLayout;
    private ViewGroup mVerticalRecyclerViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        mHorizontalRecyclerViewLayout = (ViewGroup) findViewById(R.id.recyclerview_horizontal_layout);
        mVerticalRecyclerViewLayout = (ViewGroup) findViewById(R.id.recyclerview_vertical_layout);
        initHorizontal();
        initVertical();
    }

    private void initHorizontal() {
        mHorizontalRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_horizontal);

        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mHorizontalRecyclerView.setLayoutManager(layoutManager);

        // 创建数据集
        String[] dataset = new String[100];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }
        // 创建Adapter，并指定数据集
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(dataset);
        // 设置Adapter
        mHorizontalRecyclerView.setAdapter(adapter);
    }

    public void initVertical() {
        mVerticalRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_vertical);

        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 默认是Vertical，可以不写
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 设置布局管理器
        mVerticalRecyclerView.setLayoutManager(layoutManager);

        // 创建数据集
        String[] dataset = new String[30];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }
        // 创建Adapter，并指定数据集
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(dataset);
        // 设置Adapter
        mVerticalRecyclerView.setAdapter(adapter);
    }

    /**
     * 截屏已渲染完视图
     */
    public void capture(View view) {
        int width = 0;//ScreenUtil.getScreenWidth(this);
        ViewToImageUtil.generateImage(mVerticalRecyclerViewLayout, width, 0xfff8f8f8, new ViewToImageUtil.OnImageSavedCallback() {
            @Override
            public void onFinishCallback(String path) {
                Toast.makeText(RecyclerViewActivity.this, path, Toast.LENGTH_SHORT).show();
                LogUtils.d(path);
            }
        });
    }
}
