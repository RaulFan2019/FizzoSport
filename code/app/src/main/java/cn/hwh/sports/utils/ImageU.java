package cn.hwh.sports.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import cn.hwh.sports.R;
import cn.hwh.sports.config.FileConfig;
import cn.hwh.sports.config.MapEnum;

/**
 * Created by Administrator on 2016/7/31.
 * 图片处理相关工具
 */
public class ImageU {


    /**
     * 缓冲用户头像图片
     *
     * @param avatar
     * @param imageView
     */
    public static void loadUserImage(final String avatar, final ImageView imageView) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCrop(true)
                .setLoadingDrawableId(R.drawable.ic_user_default)
                .setFailureDrawableId(R.drawable.ic_user_default)
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                .build();
        if (avatar != null && !avatar.startsWith("http://wx.qlogo.cn/") && !avatar.equals("")) {
            x.image().bind(imageView, avatar + "?imageView2/1/w/200/h/200", imageOptions);
        } else {
            x.image().bind(imageView, avatar, imageOptions);
        }
    }

    /**
     * 缓冲地图图片
     *
     * @param mapUrl
     * @param imageView
     */
    public static void loadRunMapImage(final String mapUrl, final ImageView imageView) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCrop(true)
                .setLoadingDrawableId(R.drawable.ic_map_default)
                .setFailureDrawableId(R.drawable.ic_map_default)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .build();
        if (mapUrl != null && !mapUrl.startsWith("http://wx.qlogo.cn/") && !mapUrl.equals("")) {
            x.image().bind(imageView, mapUrl + "?imageView2/1", imageOptions);
        } else {
            x.image().bind(imageView, mapUrl, imageOptions);
        }
    }


    /**
     * 进入裁剪图片页面
     */
    public static void startPhotoCut(final Activity activity, final Uri uri, final int RequestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
//        intent.putExtra("return-data", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileConfig.cutFileUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        activity.startActivityForResult(intent, RequestCode);
    }


    /**
     * 压缩图片到100kb
     *
     * @param
     * @return
     */
    public static Bitmap compressBitmap(final Bitmap image, final int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 20;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 从文件制作用户奔跑头像
     *
     * @param context
     * @param file
     * @return
     */
    public static BitmapDescriptor loadUserDesFromFile(final Context context, final File file) {
        float round = DeviceU.dpToPixel(29);
        Bitmap b = ImageU.RoundBitmap(context,
                ImageU.zoomImage(BitmapFactory.decodeFile(file.getAbsolutePath()), round, round));
        return BitmapDescriptorFactory.fromBitmap(b);
    }

    /**
     * 图片切圆
     *
     * @param src
     * @return
     */
    public static Bitmap RoundBitmap(Context context, Bitmap src) {
        //背景图片
        BitmapFactory.Options option = new BitmapFactory.Options();
        Bitmap bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_live_runner, option)
                .copy(Bitmap.Config.ARGB_8888, true);
        int srcRound = (int) (bg.getWidth() - DeviceU.dpToPixel(4f));

        Matrix matrix = new Matrix();
//        matrix.postScale(0.9f, 0.9f);
        src = Bitmap.createBitmap(src, 0, 0, srcRound, srcRound, matrix, true);

        // 获取圆形头像的Bitmap
        int radius = src.getWidth() / 2; // src为我们要画上去的图，跟上一个示例中的bitmap一样。
        Bitmap dest = Bitmap.createBitmap(srcRound, srcRound, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CW);
        c.clipPath(path); // 裁剪区域
        c.drawBitmap(src, 0, 0, paint);// 把图画上去

        // option.inJustDecodeBounds = false;
        Bitmap result = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas lastC = new Canvas(result);
        lastC.drawBitmap(bg, 0, 0, paint);
        lastC.drawBitmap(dest, DeviceU.dpToPixel(2f), DeviceU.dpToPixel(2f), paint);

        bg.recycle();
        dest.recycle();
        src.recycle();
        System.gc();
        return result;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        bgimage = null;
        return bitmap;
    }

    /**
     * 制作Split图片
     *
     * @param context
     * @param splite
     * @return
     */
    public static Bitmap GetSplitBitmap(Context context, int splite, final Typeface typeFace) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        Bitmap bgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_map_split)
                .copy(Bitmap.Config.ARGB_8888, true);
        // 获得图片的宽高
        int width = bgBitmap.getWidth();
        int height = bgBitmap.getHeight();

        Canvas c = new Canvas(bgBitmap);
        //画数字
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(typeFace);
        paint.setColor(Color.WHITE);
        paint.setTextSize(MapEnum.FONT_SIZE_SPLITE);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字baseline
        float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.bottom;
        c.drawText(String.valueOf(splite), width / 2, textBaseY, paint);
        c.save();
        return bgBitmap;
    }

}
