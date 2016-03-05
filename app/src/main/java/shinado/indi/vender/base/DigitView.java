package shinado.indi.vender.base;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import indi.shinado.piping.util.CommonUtil;

public class DigitView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

	private SurfaceHolder sfh;
	private boolean mRunning = false;
	private int screenWidth, screenHeight;
	private float density;
	
	public void setScreen(int w, int h, float d){
		this.screenHeight = h;
		this.screenWidth  = w;
		this.density = d;
	}
	
	public DigitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initSurface();
	}

	private void initSurface(){
		sfh = this.getHolder();
		sfh.addCallback(this);

		//͸��
		setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSLUCENT);
	}
	
	private int dragLength;
	private int SCALE_DRAG = 2;
	float startX = 0, startY = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		float x, y;
		x = event.getX();
		y = event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			startX = x;
			startY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			dragLength = (int) Math.sqrt(
					Math.pow(x-startX, 2) + 
					Math.pow(y-startY, 2) 
					);
			break;
		case MotionEvent.ACTION_UP:
			if(dragLength > screenWidth/SCALE_DRAG){
				if(mOnFinishListener != null){
					mOnFinishListener.onFinish();
				}
			}
			dragLength = 0;
			break;
		}
		return true;
	}
	
	private OnFinishListener mOnFinishListener;
	public void setOnFinishListener(OnFinishListener l){
		this.mOnFinishListener = l;
	}
	public interface OnFinishListener{
		public void onFinish();
	}
	
	public void start(){
		if(mRunning){
			return;
		}
		mRunning = true;
		new Thread(this).start();
	}
	
	public void stop(){
		mRunning = false;
	}
	
	@Override
	public void run() {
		int count = 0;
		while(mRunning){
			count ++;
			if(count >= 2){
				count = 0;
				if(flow.size() <= MAX_FLOW){
					flow.add(new TextRain());
				}
			}
			draw();
			try {
				Thread.sleep(FLOW_SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void draw(){
		Canvas canvas = sfh.lockCanvas();
		if(canvas == null){
			return;
		}
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		for(int i=0; i<flow.size(); i++){
			drawText(i, canvas);
		}
		sfh.unlockCanvasAndPost(canvas);
	}

	private final int FLOW_SLEEP = 100;
	private final int FLOW_SPEED = 20;
	private final int MAX_FLOW = 30;
	private ArrayList<TextRain> flow = new ArrayList<TextRain>();
	
	private void drawText(int index, Canvas canvas){
		TextRain rain = flow.get(index);
		int startY = rain.startY;
		for(int j=0; j<rain.size; j++){
			int d = CommonUtil.getRandom(2, 0);
			canvas.drawText(d+"", getDrawX(rain.x), startY, rain.paint);
			startY += getTextHeight(rain.paint) + 2*density;
		}
		rain.startY += FLOW_SPEED;
		if(rain.startY >= screenHeight){
			flow.remove(index);
			return;
		}
		rain.size++;
	}
	
	private int getColor(int fadeColor, float mag){
		final int baseAlpha = (fadeColor & 0xff000000) >>> 24;
        int imag = (int) (baseAlpha * mag);
        int color = imag << 24 | (fadeColor & 0xffffff);
        Log.v("hex", Integer.toHexString(color));
        return color;
	}
	
	private int getDrawX(int x){
		if(x > screenWidth/2){
			x -= dragLength*SCALE_DRAG;
		}else{
			x += dragLength*SCALE_DRAG;
		}
		return x;
	}
	
	private Paint getRandomTextPaint(){
		Paint paint = new Paint();
		float alpha = CommonUtil.getRandom(80, 20) / 100f;
		paint.setColor(getColor(0xff5fdc29, alpha));
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
		int textSize = (int) (CommonUtil.getRandom(30, 10) * density);
		paint.setTextSize(textSize);
		return paint;
	}

	private int getTextHeight(Paint paint){
		Rect rect = new Rect();
		paint.getTextBounds("1", 0, 1, rect);
		return rect.height();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		stop();
	}

	class TextRain{
		public TextRain(){
			x = CommonUtil.getRandom(screenWidth, 0);
			int tryXCount = 0;
			//to avoid overlap
			while(xToken(x)){
				x = CommonUtil.getRandom(screenWidth, 0);
				if(++tryXCount > 3){
					break;
				}
			}
			size = 2;
			startY = 0;
			paint = getRandomTextPaint();
		}
		
		int size;
		int x;
		int startY;
		Paint paint;
		
		private int getTextWidth(){
			Rect rect = new Rect();
			paint.getTextBounds("1", 0, 1, rect);
			return rect.width();
		}
		
		private boolean xToken(int x){
			for(TextRain rain:flow){
				if(x > rain.x && x < rain.x + rain.getTextWidth()){
					return true;
				}
			}
			return false;
		}
	}
}
