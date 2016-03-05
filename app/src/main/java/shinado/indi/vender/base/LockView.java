package shinado.indi.vender.base;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.shinado.piping.lock.BaseLockView;


public class LockView extends BaseLockView {

	private DisplayMetrics dm;
	DigitView view;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm); 
		
		view = new DigitView(this, null);
		view.setBackgroundColor(Color.BLACK);
		view.setScreen(dm.widthPixels, dm.heightPixels, dm.density);
		view.setOnFinishListener(new DigitView.OnFinishListener() {
			
			@Override
			public void onFinish() {
				finish();
			}
		});
//		view.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				Log.v("onTouch", event.getAction()+"");
//				return false;
//			}
//		});
		setContentView(view);
	}
	
	@Override
	public void screenOn(){
		view.start();
	}

	@Override
	public void screenOff(){
		view.stop();
	}
	
}
