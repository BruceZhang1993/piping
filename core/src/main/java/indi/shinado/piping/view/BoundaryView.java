package indi.shinado.piping.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import indi.shinado.piping.settings.Preferences;

public class BoundaryView extends View {

    private int mLeft, mRight, mTop, mBottom;
    private Paint mPaint;

    public BoundaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        Preferences preferences = new Preferences(context);
        mPaint.setColor(preferences.getColor());
        mPaint.setStrokeWidth(preferences.getBoundaryWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (Math.abs(mPaint.getStrokeWidth()) < 0.01f){
            return;
        }
        //draw boundary
        canvas.drawLine(mLeft, mTop, mRight, mTop, mPaint);
        canvas.drawLine(mRight, mTop, mRight, mBottom, mPaint);
        canvas.drawLine(mRight, mBottom, mLeft, mBottom, mPaint);
        canvas.drawLine(mLeft, mBottom, mLeft, mTop, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLeft = left;
        this.mRight = right;
        this.mTop = top;
        this.mBottom = bottom;
    }

    public void setBoundaryWidth(float width){
        mPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setBoundaryColor(int color){
        mPaint.setColor(color);
        invalidate();
    }
}
