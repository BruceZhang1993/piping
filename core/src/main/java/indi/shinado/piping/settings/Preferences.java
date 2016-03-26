package indi.shinado.piping.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;

import indi.shinado.piping.color.ColorPickerView;

public class Preferences {

    private static final String KEY_COLOR = "color";
    private static final String KEY_LEFT_X = "left_x";
    private static final String KEY_LEFT_Y = "left_y";
    private static final String KEY_RIGHT_X = "right_x";
    private static final String KEY_RIGHT_Y = "right_y";
    private static final String WALL_PAPER_SET = "set_wallpaper";
    private static final String NAME = "piping";

    private SharedPreferences settings;

    public Preferences(Context context){
        settings = context.getSharedPreferences(NAME, 0);
    }

    public boolean isWallpaperSet(){
        return settings.getBoolean(WALL_PAPER_SET, false);
    }

    public void setWallpaper(boolean set){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(WALL_PAPER_SET, set);
        editor.apply();
    }

    public boolean isVibrating(){
        return true;
    }

    public boolean isSoundPlayed(){
        return true;
    }


    public PointF getLeftPoint(){
        return new PointF(settings.getFloat(KEY_LEFT_X, ColorPickerView.SPLIT_WIDTH),
                settings.getFloat(KEY_LEFT_Y, ColorPickerView.SPLIT_WIDTH));
    }
    public PointF getRightPoint(){
        return new PointF(settings.getFloat(KEY_RIGHT_X, ColorPickerView.SPLIT_WIDTH),
                settings.getFloat(KEY_RIGHT_Y, ColorPickerView.SPLIT_WIDTH));
    }

    public void selectedColorPoint(PointF left, PointF right){
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(KEY_LEFT_X, left.x);
        editor.putFloat(KEY_LEFT_Y, left.y);
        editor.putFloat(KEY_RIGHT_X, right.x);
        editor.putFloat(KEY_RIGHT_Y, right.y);
        editor.apply();
    }

    public int getColor(int defaultColor){
        return settings.getInt(KEY_COLOR, defaultColor);
    }

    public void setColor(int color) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_COLOR, color);
        editor.apply();
    }
}
