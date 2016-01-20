package jameson.io.viewtoimage.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 压缩图片util
 */
public class CompressImageUtil {

    // 图片最大大小 100KB 超过此大小要进行压缩
    public static int MAX_PIC_MEMORY_SIZE = 500;
    //最大尺寸 w
    public static int MAX_PIC_WIDTH = 720;
    //最大尺寸 h
    public static int MAX_PIC_HEIGHT = 480;
    //长图的最大宽度
    public static int LONG_PIC_MAX_WIDTH = 100;

    /**
     * 压缩图片
     * @param path 图片路径
     * @return 压缩后的图片路径
     */
    public static String compressFile(String path) {
        File picFile = new File(path);
        long kb = picFile.length() / 1024;
        if (kb > MAX_PIC_MEMORY_SIZE) {
            Uri tempUri = createImageFile();
            if (tempUri != null) {
                String tempPath = tempUri.getPath();
                CompressImageUtil.compressBitmapToFile(path, tempPath);
                return tempPath;
            } else {
                LogUtils.i("Create temp file fail.");
            }
        }
        return path;
    }

    private static Uri createImageFile() {
        return Uri.parse(FileUtil.createImageFile(null).getAbsolutePath());
    }

    public static void compressBitmapToFile(String inFile, String outFile) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(inFile, opts);
            opts.inSampleSize = calculateInSampleSize(opts, MAX_PIC_WIDTH, MAX_PIC_HEIGHT);
            opts.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(inFile, opts);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
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

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width < LONG_PIC_MAX_WIDTH) {
            return inSampleSize;
        }
        if ((width > height && reqWidth < reqHeight) || (width < height && reqWidth > reqHeight)) {
            // 交换reqWidth, reqHeight, 使大边对大边，小边对小边
            int temp = reqWidth;
            reqWidth = reqHeight;
            reqHeight = temp;
        }

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

}
