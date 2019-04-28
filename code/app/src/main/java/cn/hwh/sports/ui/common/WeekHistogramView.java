package cn.hwh.sports.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import cn.hwh.sports.R;
import cn.hwh.sports.utils.DeviceU;

/**
 * Created by Administrator on 2016/11/27.
 */

public class WeekHistogramView extends View {
    private Paint mXLinePaint;//X 方向坐标轴 画笔
    private Paint mHLinePaint;//箭头横向线
    private Paint mTextPaint;
    private Paint mRectPaint;//柱状图画笔

    private List<Integer> mData;
    private String[] mWeek = {"二", "三", "四", "五", "六", "日", "一"};
    private String[] mY = {"20k", "10k", "0"};
    private float mMaxValue = 10000;    //Y轴最大值，计算条形图高度
    private float mTargetValue = 10000;//目标标准线值

    private Bitmap mRectMap;//柱状图片
    private Bitmap mStandardMap;//梯形基准箭头图片

    public WeekHistogramView(Context context) {
        super(context);
        init();
    }

    public WeekHistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekHistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mXLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mXLinePaint.setColor(Color.WHITE);
        mHLinePaint.setColor(Color.WHITE);
        mRectPaint.setColor(Color.WHITE);
        mTextPaint.setColor(Color.WHITE);

        mRectMap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_monitor_column);
        mStandardMap = drawTextToBitmap(R.drawable.ic_monitor_target,"1k");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if(mData == null || mData.size() <= 0){
            mTextPaint.setTextSize(DeviceU.dpToPixel(24));
            canvas.drawText("暂无数据",width/2,height/2,mTextPaint);
            return;
        }

        int lAHeight = (int) (height - DeviceU.dpToPixel(70));

        //绘画指示基准线
        mHLinePaint.setStrokeWidth(3);
        int lTipValue = (int) (lAHeight-lAHeight * (mTargetValue/mMaxValue));
        float lTipHeight = DeviceU.dpToPixel(46) + lTipValue;

        Rect lYRect = new Rect();
        lYRect.left = (int) DeviceU.dpToPixel(7);//左上x
        lYRect.right = (int) DeviceU.dpToPixel(18) + lYRect.left;//右下x
        lYRect.top = (int) (lTipHeight - DeviceU.dpToPixel(5));//左上y
        lYRect.bottom = (int) (lTipHeight + DeviceU.dpToPixel(4));//右下y

        canvas.drawBitmap(mStandardMap, null, lYRect, mHLinePaint);
        canvas.drawLine(DeviceU.dpToPixel(26), lTipHeight, width - DeviceU.dpToPixel(10), lTipHeight, mHLinePaint);

        //绘画Y轴文字
        mXLinePaint.setStrokeWidth(3);
        mXLinePaint.setAlpha(100);
        mTextPaint.setTextSize(DeviceU.dpToPixel(12));
        int lYCount = mY.length -1;
        int lYStep = lAHeight / lYCount;
        for (int i = 0; i < mY.length; i++) {
            //Y轴文字
            canvas.drawText(mY[i], DeviceU.dpToPixel(6), DeviceU.dpToPixel(50) +  lYStep * i, mTextPaint);
            //Y轴基准线
            canvas.drawLine(DeviceU.dpToPixel(25), DeviceU.dpToPixel(46) + lYStep * i, width - DeviceU.dpToPixel(10), DeviceU.dpToPixel(46) + lYStep * i, mXLinePaint);
        }

        //绘画星期文字
        int lCount = mWeek.length;
        int step = (int) (width - DeviceU.dpToPixel(60)) / lCount;
        for (int i = 0; i < mWeek.length; i++) {
            canvas.drawText(mWeek[i], DeviceU.dpToPixel(50) + step * i, DeviceU.dpToPixel(30), mTextPaint);
        }

        //绘画柱子
        mRectPaint.setAntiAlias(true);// 抗锯齿效果
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setDither(true);
        for (int i = 0; i < mWeek.length; i++) {
            Rect rect = new Rect();
            rect.left = (int) (DeviceU.dpToPixel(42) + step * i);//左上x
            rect.right = (int) DeviceU.dpToPixel(27) + rect.left;//右下x
            int lValue = (int) (lAHeight-lAHeight * (mData.get(i)/mMaxValue));
            rect.top = (int) (lValue + DeviceU.dpToPixel(46));//左上y，控制柱状图柱子的高度
            rect.bottom = (int) DeviceU.dpToPixel(46) + lAHeight;
            canvas.drawBitmap(mRectMap, null, rect, mRectPaint);
        }
    }

    /**
     * 设置数据
     * @param data
     */
    public void setData(List<Integer> data,String[] week){
        this.mData = data;
        this.mWeek = week;
        invalidate();
    }

    /**
     * 设置最大值Y轴最大值
     * @param maxValue
     */
    public void setMaxValue(int maxValue){
        this.mMaxValue = maxValue;
        mY[0] = maxValue/1000 +"k";
        mY[1] = maxValue/2000 +"k";
        mY[2] = "0";
    }

    /**
     * 设置目标线值
     * @param targetValue
     */
    public void  setTargetValue(int targetValue){
        this.mTargetValue = targetValue;
    }


    public Bitmap drawTextToBitmap( int resId, String text) {
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =BitmapFactory.decodeResource(resources, resId);

        bitmap = scaleWithWH(bitmap, 300*scale, 300*scale);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();

        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (18 * scale));
        paint.setDither(true); //获取跟清晰的图像采样
        paint.setFilterBitmap(true);//过滤一些
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = 30;
        int y = 30;
        canvas.drawText(text, x * scale, y * scale, paint);
        return bitmap;
    }

    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }
}
