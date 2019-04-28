/*
 * Copyright (C) 2015 David Lázaro Esparcia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hwh.sports.ui.wheelindicator;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.sports.R;

/**
 * Displays a graphic in style with Google Fit Application.
 *
 * @author David Lázaro.
 * @attr ref android.R.styleable#itemsLineWidth the width of the wheel indicator lines
 * @attr ref android.R.styleable#backgroundColor color for the inner circle
 * @attr ref android.R.styleable#filledPercent percent of circle filled by WheelItems values
 */
public class WheelIndicatorView extends View {

    private final static int ANGLE_INIT_OFFSET = -90;
    private final static int DEFAULT_FILLED_PERCENT = 100;
    private final static int DEFAULT_ITEM_LINE_WIDTH = 25;
    public static final int ANIMATION_DURATION = 1200;
    public static final int INNER_BACKGROUND_CIRCLE_COLOR = Color.parseColor("#ff4612"); // Color for

    private static final int ITEM_PADDING = 5;

    private Paint itemArcPaint;
    private Paint itemEndPointsPaint;
    private Paint innerBackgroundCirclePaint;
    private Paint startPointPaint;
    private Bitmap bitmap;
    private List<WheelIndicatorItem> wheelIndicatorItems;
    private int viewHeight;
    private int viewWidth;
    private int minDistViewSize;
    private int maxDistViewSize;
    private int traslationX;
    private int traslationY;
    private RectF wheelBoundsRectF;
    private Rect bitmapRect;
    private RectF wheelItemRectF;//内部线条
    private RectF bitmapDstRect;
    private Paint circleBackgroundPaint;
    private ArrayList<Float> wheelItemsAngles;     // calculated angle for each @WheelIndicatorItem
    private int filledPercent = 80;
    private int itemsLineWidth = 25;

    public WheelIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public WheelIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WheelIndicatorView(Context context) {
        super(context);
        init(null);
    }

    public void setWheelIndicatorItems(List<WheelIndicatorItem> wheelIndicatorItems) {
        if (wheelIndicatorItems == null)
            throw new IllegalArgumentException("wheelIndicatorItems cannot be null");
        this.wheelIndicatorItems = wheelIndicatorItems;
        recalculateItemsAngles();
        invalidate();
    }

    public void setFilledPercent(int filledPercent) {
        if (filledPercent < 0)
            this.filledPercent = 0;
        else if (filledPercent > 100)
            this.filledPercent = 100;
        else
            this.filledPercent = filledPercent;
        invalidate();
    }

    public int getFilledPercent() {
        return filledPercent;
    }

    public void setItemsLineWidth(int itemLineWidth) {
        if (itemLineWidth <= 0)
            throw new IllegalArgumentException("itemLineWidth must be greater than 0");
        this.itemsLineWidth = itemLineWidth;
        invalidate();
    }

    public void addWheelIndicatorItem(WheelIndicatorItem indicatorItem) {
        if (indicatorItem == null)
            throw new IllegalArgumentException("wheelIndicatorItems cannot be null");

        this.wheelIndicatorItems.add(indicatorItem);
        recalculateItemsAngles();
        invalidate();
    }

    public void notifyDataSetChanged() {
        recalculateItemsAngles();
        invalidate();
    }

    public void setBackgroundColor(int color) {
        circleBackgroundPaint = new Paint();
        circleBackgroundPaint.setColor(color);
        invalidate();
    }

    private void init(AttributeSet attrs) {
        TypedArray attributesArray = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.WheelIndicatorView, 0, 0);

        int itemsLineWidth = attributesArray.getDimensionPixelSize(R.styleable.WheelIndicatorView_itemsLineWidth, DEFAULT_ITEM_LINE_WIDTH);
        setItemsLineWidth(itemsLineWidth);

        int filledPercent = attributesArray.getInt(R.styleable.WheelIndicatorView_filledPercent, DEFAULT_FILLED_PERCENT);
        setFilledPercent(filledPercent);

        int bgColor = attributesArray.getColor(R.styleable.WheelIndicatorView_backgroundColor, -1);

        int innerBackgroundCircleStrokeWidth = attributesArray.getDimensionPixelSize(R.styleable.WheelIndicatorView_innerBackgroundCircleLineWidth, DEFAULT_ITEM_LINE_WIDTH);

        Drawable drawable = attributesArray.getDrawable(R.styleable.WheelIndicatorView_src);
        BitmapDrawable bd = (BitmapDrawable) drawable;
        bitmap = bd.getBitmap();

        if (bgColor != -1)
            setBackgroundColor(bgColor);

        this.wheelIndicatorItems = new ArrayList<>();
        this.wheelItemsAngles = new ArrayList<>();

        itemArcPaint = new Paint();
        itemArcPaint.setStyle(Paint.Style.STROKE);
        itemArcPaint.setStrokeWidth(itemsLineWidth);
        itemArcPaint.setAntiAlias(true);

        DashPathEffect pathEffect = new DashPathEffect(new float[]{1, 3}, 1);
        innerBackgroundCirclePaint = new Paint();
        innerBackgroundCirclePaint.setColor(INNER_BACKGROUND_CIRCLE_COLOR);
        innerBackgroundCirclePaint.setStyle(Paint.Style.STROKE);
        innerBackgroundCirclePaint.setStrokeWidth(innerBackgroundCircleStrokeWidth);
        innerBackgroundCirclePaint.setAntiAlias(true);
        innerBackgroundCirclePaint.setPathEffect(pathEffect);

        itemEndPointsPaint = new Paint();
        itemEndPointsPaint.setAntiAlias(true);

        startPointPaint = new Paint();
        startPointPaint.setAntiAlias(true);
        startPointPaint.setColor(Color.parseColor("#ff4612"));
        startPointPaint.setStyle(Paint.Style.FILL);
    }

    private void recalculateItemsAngles() {
        wheelItemsAngles.clear();
        float total = 0;
        float angleAccumulated = 0;

        for (WheelIndicatorItem item : wheelIndicatorItems) {
            total += item.getWeight();
        }
        for (int i = 0; i < wheelIndicatorItems.size(); ++i) {
            float normalizedValue = wheelIndicatorItems.get(i).getWeight() / total;
            float angle = 360 * normalizedValue * filledPercent / 100;
            wheelItemsAngles.add(angle + angleAccumulated);
            angleAccumulated += angle;
        }
    }

    public void startItemsAnimation() {
        ObjectAnimator animation = ObjectAnimator.ofInt(WheelIndicatorView.this, "filledPercent", 0, filledPercent);
        animation.setDuration(ANIMATION_DURATION);
        animation.setInterpolator(PathInterpolatorCompat.create(0.4F, 0.0F, 0.2F, 1.0F));
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                recalculateItemsAngles();
                invalidate();
            }
        });
        animation.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.viewHeight = getMeasuredHeight();
        this.viewWidth = getMeasuredWidth();
        this.minDistViewSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
        this.maxDistViewSize = Math.max(getMeasuredWidth(), getMeasuredHeight());

        if (viewWidth <= viewHeight) {
            this.traslationX = 0;
            this.traslationY = (maxDistViewSize - minDistViewSize) / 2;
        } else {
            this.traslationX = (maxDistViewSize - minDistViewSize) / 2;
            this.traslationY = 0;
        }
        // Adding artificial padding, depending on line width
        wheelBoundsRectF = new RectF(0 + itemsLineWidth, 0 + itemsLineWidth, minDistViewSize - itemsLineWidth, minDistViewSize - itemsLineWidth);
        wheelItemRectF = new RectF(ITEM_PADDING + itemsLineWidth, ITEM_PADDING + itemsLineWidth, minDistViewSize - itemsLineWidth - ITEM_PADDING, minDistViewSize - itemsLineWidth - ITEM_PADDING);
        bitmapRect = new Rect(0 + itemsLineWidth * 4, 0 + itemsLineWidth * 4, minDistViewSize - (itemsLineWidth * 4), minDistViewSize - (itemsLineWidth * 4));
        bitmapDstRect = new RectF(bitmapRect.left - bitmap.getWidth() / 2, bitmapRect.top - bitmap.getHeight() / 2,
                bitmapRect.right + bitmap.getWidth(), bitmapRect.bottom + bitmap.getWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(traslationX, traslationY);
        if (circleBackgroundPaint != null) {
            canvas.drawCircle(wheelBoundsRectF.centerX(), wheelBoundsRectF.centerY(), wheelBoundsRectF.width() / 2 - itemsLineWidth, circleBackgroundPaint);
        }
        canvas.drawArc(wheelBoundsRectF, ANGLE_INIT_OFFSET, 360, false, innerBackgroundCirclePaint);
        canvas.drawBitmap(bitmap, null, bitmapRect, null);

        if (this.filledPercent != 0) {
            drawIndicatorItems(canvas);
//            canvas.drawCircle(wheelBoundsRectF.centerX(), itemsLineWidth, 6, startPointPaint);
        }
    }

    private void drawIndicatorItems(Canvas canvas) {
        if (wheelIndicatorItems.size() > 0) {
            for (int i = wheelIndicatorItems.size() - 1; i >= 0; i--) { // Iterate backward to overlap larger items
                draw(wheelIndicatorItems.get(i), wheelItemRectF, wheelItemsAngles.get(i), canvas);
            }
        }
    }

    private void draw(WheelIndicatorItem indicatorItem, RectF surfaceRectF, float angle, Canvas canvas) {
        itemArcPaint.setColor(indicatorItem.getColor());
        itemEndPointsPaint.setColor(indicatorItem.getColor());
        // Draw arc
        canvas.drawArc(surfaceRectF, ANGLE_INIT_OFFSET, angle, false, itemArcPaint);
        // Draw top circle
        canvas.drawCircle(minDistViewSize / 2, ITEM_PADDING + itemsLineWidth, itemsLineWidth / 2, itemEndPointsPaint);
        int topPosition = minDistViewSize / 2 - itemsLineWidth - ITEM_PADDING;
        // Draw end circle
        canvas.drawCircle(
                (float) (Math.cos(Math.toRadians(angle + ANGLE_INIT_OFFSET)) * topPosition + topPosition + itemsLineWidth + ITEM_PADDING),
                (float) (Math.sin(Math.toRadians((angle + ANGLE_INIT_OFFSET))) * topPosition + topPosition + itemsLineWidth + ITEM_PADDING), itemsLineWidth / 2, itemEndPointsPaint);
    }
}
