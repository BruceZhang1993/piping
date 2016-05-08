package indi.shinado.piping.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.view.KeyEvent;

import com.shinado.annotation.TargetVersion;

import indi.shinado.piping.color.ColorPickerView;

public class Preferences {

    public static final int DEFAULT_TEXT_SIZE = 12;
    public static final int DEFAULT_BOUNDARY_WIDTH = 6;
    public static final int DEFAULT_COLOR = 0xff44d804;

    public static final String DEFAULT_INIT_TEXT = "wsp_stp_auth_build.bui\n"+
            "Account:******\n"+
            "Password:***************\n"+
            "Access granted";

    private static final String TEXT_SIZE = "text_size";
    private static final String BOUNDARY_WIDTH = "boundary_width";
    private static final String INIT_TEXT = "init_text";
    private static final String KEY_USER_NAME = "user.name";
    private static final String KEY_COLOR = "color";
//    private static final String KEY_HISTORY = "history";
    private static final String KEY_KEYBOARD = "keyboard";
    private static final String KEY_SHIFT = "shift";
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

    public int getColor(){
        return settings.getInt(KEY_COLOR, DEFAULT_COLOR);
    }

    public void setColor(int color) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_COLOR, color);
        editor.apply();
    }

    public String getInitText(){
        return settings.getString(INIT_TEXT, DEFAULT_INIT_TEXT);
    }

    public void setInitText(String text) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(INIT_TEXT, text);
        editor.apply();
    }

    public float getTextSize() {
        return settings.getFloat(TEXT_SIZE, DEFAULT_TEXT_SIZE);
    }

    public void setTextSize(float size) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(TEXT_SIZE, size);
        editor.apply();
    }

    public float getBoundaryWidth() {
        return settings.getFloat(BOUNDARY_WIDTH, DEFAULT_BOUNDARY_WIDTH);
    }

    public void setBoundaryWidth(int width) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(BOUNDARY_WIDTH, width);
        editor.apply();
    }

    @TargetVersion(4)
    public String getUserName(){
        return settings.getString(KEY_USER_NAME, null);
    }

    @TargetVersion(4)
    public void setUserName(String name) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    @TargetVersion(4)
    public int getKeyboardKey(){
        return settings.getInt(KEY_KEYBOARD, KeyEvent.KEYCODE_HOME);
    }

    @TargetVersion(4)
    public void getKeyboardKey(int keyCode){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_KEYBOARD, keyCode);
        editor.apply();

    }

    @TargetVersion(4)
    public int getShiftKey(){
        return settings.getInt(KEY_SHIFT, KeyEvent.KEYCODE_MENU);
    }

    @TargetVersion(4)
    public void setShiftKey(int keyCode){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_SHIFT, keyCode);
        editor.apply();

    }
}
