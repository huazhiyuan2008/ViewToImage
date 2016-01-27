package jameson.io.viewtoimage;

import android.app.Application;

import java.io.File;

import jameson.io.viewtoimage.util.FileUtil;
import jameson.io.viewtoimage.util.LogUtils;
import jameson.io.viewtoimage.util.ScreenUtil;

/**
 * Created by jameson on 1/25/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.d("screen===" + ScreenUtil.getScreenWidth(this) + ", " + ScreenUtil.getScreenHeight(this));
        // 清空目录
        File file = new File(FileUtil.getBasePath(this));
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                childFile.delete();
            }
        }
    }
}
