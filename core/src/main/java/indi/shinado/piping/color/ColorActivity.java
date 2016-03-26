package indi.shinado.piping.color;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;

import com.shinado.core.R;

import indi.shinado.piping.GlobalDefs;
import indi.shinado.piping.settings.Preferences;

public class ColorActivity extends Activity{

	public static final String EXTRA_COLOR = "extra.color";
	private int mColor;
	private PointF mLeftPoint, mRightPoint;
	private Preferences pref;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_color);

		int color = getIntent().getIntExtra(EXTRA_COLOR, 0xff83f352);

		pref = new Preferences(this);
		mLeftPoint = pref.getLeftPoint();
		mRightPoint = pref.getRightPoint();
		mColor = pref.getColor(color);
		
		ColorPickerView picker = (ColorPickerView) findViewById(R.id.picker);
		picker.setSelectedColor(mLeftPoint, mRightPoint);
		picker.setOnColorChangedListenner(new ColorPickerView.OnColorChangedListener() {
			
			@Override
			public void onColorChanged(int color, PointF left, PointF right) {
				mColor = color;
			}
		});
	}
	
	public void cancel(View v){
		finish();
	}
	
	public void done(View v){
		pref.selectedColorPoint(mLeftPoint, mRightPoint);
		Intent intent = new Intent();
		intent.putExtra(GlobalDefs.EXTRA_COLOR, mColor);
		setResult(RESULT_OK, intent);
		finish();
	}
}
