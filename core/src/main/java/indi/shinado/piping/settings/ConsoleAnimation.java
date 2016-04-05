package indi.shinado.piping.settings;

import android.view.animation.Animation;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "animation")
public class ConsoleAnimation extends Model{

    public static final int TYPE_TRANS = 1;
    public static final int TYPE_SCALE = 2;
    public static final int TYPE_ROTATE = 3;
    public static final int TYPE_ALPHA = 4;
    public static final int TYPE_TYPING = 5;

    @Column(name = "cType")
    public int type = TYPE_TRANS;

    @Column(name = "cRelate")
    public int relateTo = Animation.RELATIVE_TO_SELF;

    @Column(name = "startX")
    public float startX;

    @Column(name = "startY")
    public float startY;

    @Column(name = "endX")
    public float endX;

    @Column(name = "endY")
    public float endY;

    @Column(name = "duration")
    public int duration = 50;

    public static boolean isTypeCorrect(int type){
        return type == TYPE_ALPHA || type == TYPE_ROTATE || type == TYPE_SCALE ||
                type == TYPE_TRANS || type == TYPE_TYPING;
    }

    public ConsoleAnimation() {
    }

    public ConsoleAnimation(String string) {
        String[] split = string.split(", ");
        if (split.length == 6){
            try {
                type = Integer.parseInt(split[0]);
                startX = Float.parseFloat(split[1]);
                endX = Float.parseFloat(split[2]);
                startY = Float.parseFloat(split[3]);
                endY = Float.parseFloat(split[4]);
                duration = Integer.parseInt(split[5]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public ConsoleAnimation(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public ConsoleAnimation(int type, int relateTo, float startX, float endX, float startY, float endY, int duration) {
        this.type = type;
        this.relateTo = relateTo;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.duration = duration;
    }

    public void clearTable(){
        new Delete().from(ConsoleAnimation.class).execute();
    }

    public static ConsoleAnimation get(){
        ConsoleAnimation animation = new Select().from(ConsoleAnimation.class).executeSingle();
        if (animation == null){
            animation = new ConsoleAnimation(TYPE_TRANS, Animation.RELATIVE_TO_PARENT, 0, 0, 0.5f, 0, 75);
        }
        return animation;
    }

    @Override
    public String toString() {
        return  type + ", " + startX + ", " + endX + ", " + startY + ", " + endY + ", " + duration;
    }
}
