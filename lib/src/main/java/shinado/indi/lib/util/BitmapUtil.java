package shinado.indi.lib.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtil {

	public static final int NONE = 0;
	public static final int VERTICAL = 1;
	public static final int LANDSCAPE = 2;
	
	public static Bitmap adaptScreen(Bitmap bm, int sWidth, int sHeight, int ori){
		int w = bm.getWidth();
		int h = bm.getHeight();
		float scaleX = (float)sWidth/(float)w;
		float scaleY = (float)sHeight/(float)h;
		System.out.println("scale:"+scaleX+","+scaleY);
		Matrix matrix = new Matrix();

		if(ori == NONE){
			if(w == sWidth && h == sHeight){
				return bm;
			}
			if(Math.abs(scaleY-scaleX) < 0.01f){
				matrix.reset();
				matrix.setScale(scaleX, scaleY);
				return Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			}
			else if(scaleX > scaleY){
				matrix.reset();
				matrix.setScale(scaleX, scaleX);
				return Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			}
			else {
				matrix.reset();
				matrix.setScale(scaleY, scaleY);
				return Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			}	
		}else if(ori == LANDSCAPE){
			if(Math.abs(scaleY-1) <= 0.01f){
				return bm;
			}else{
				matrix.reset();
				matrix.setScale(scaleY, scaleY);
				return Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			}
		}else if(ori == VERTICAL){
			if(Math.abs(scaleX-1) <= 0.01f){
				return bm;
			}else{
				matrix.reset();
				matrix.setScale(scaleX, scaleX);
				return Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			}
		}
		return bm;
	}
	
	public static Bitmap roundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
          	left = 0;
           	right = width;
           	height = width;
          	dst_left = 0;
           	dst_top = 0;
           	dst_right = width;
           	dst_bottom = width;
        } else {
          	roundPx = height / 2;
           	float clip = (width - height) / 2;
           	left = clip;
           	right = width - clip;
           	top = 0;
          	bottom = height;
           	width = height;
          	dst_left = 0;
         	dst_top = 0;
           	dst_right = height;
          	dst_bottom = height;
        }
         
        Bitmap output = Bitmap.createBitmap(width,
                        height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
         
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);
         
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
	}
}
