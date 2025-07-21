package com.example.intrack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PathView extends View {

    private float startX, startY, endX, endY;
    private boolean hasPath = false;

    private Paint paint;

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF2196F3); // Blue color
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
    }

    public void setPath(float fromX, float fromY, float toX, float toY) {
        startX = fromX;
        startY = fromY;
        endX = toX;
        endY = toY;
        hasPath = true;
        invalidate(); // Triggers redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasPath) {
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }
}
