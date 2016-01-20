package jameson.io.viewtoimage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jameson on 1/19/16.
 */
public class FileUtil {

    // 图片最大大小 100KB 超过此大小要进行压缩
    public static int MAX_PIC_MEMORY_SIZE = 1500;
    //最大尺寸 w
    public static int MAX_PIC_WIDTH = 720;
    //最大尺寸 h
    public static int MAX_PIC_HEIGHT = 480;
    //长图的最大宽度
    public static int LONG_PIC_MAX_WIDTH = 100;

    public static String getBasePath(Context context) {
        return getStoragePath("123");
    }

    public static String getStoragePath(String baseName) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        File file = new File(path + "/" + baseName);
        LogUtils.d(file.getPath());
        LogUtils.d(file.getAbsolutePath());
        file.mkdirs();
        return file.getAbsolutePath() + "/";
    }

    public static File createImageFile(Context context) {
        return createImageFile(context, null);
    }

    public static File createImageFile(Context context, @Nullable String name) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        name = TextUtils.isEmpty(name) ? timeStamp : "";
        File file = new File(getBasePath(context) + name + ".jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void compressBitmapToFile(Bitmap bmp, String outFile) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
            while (baos.toByteArray().length / 1024 > MAX_PIC_MEMORY_SIZE && options > 0) {
                baos.reset();
                options -= 10;
                bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
