package indi.shinado.piping.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import indi.shinado.piping.settings.ConsoleAnimation;

public class AnimationTextView extends TextView{

    private Animation animation;
    private ViewGroup contentContainer;

    public AnimationTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setConsoleAnimation(ConsoleAnimation anim){
        contentContainer = (ViewGroup) getRootView();
        if (this.animation == null){
            addListener();
        }

        switch (anim.type){
            case ConsoleAnimation.TYPE_ALPHA:
                animation = new AlphaAnimation(anim.startX, anim.endX);
                break;
            case ConsoleAnimation.TYPE_ROTATE:
                animation = new RotateAnimation(anim.startX, anim.endX,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                break;
            case ConsoleAnimation.TYPE_SCALE:
                animation = new ScaleAnimation(anim.startX, anim.endX, anim.startY, anim.endY,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                break;
            case ConsoleAnimation.TYPE_TRANS:
                animation = new TextAnimation(Animation.RELATIVE_TO_PARENT, anim.startX,
                        Animation.RELATIVE_TO_PARENT, anim.endX,
                        Animation.RELATIVE_TO_PARENT, anim.startY,
                        Animation.RELATIVE_TO_PARENT, anim.endY);
                break;
            case ConsoleAnimation.TYPE_TYPING:
                //TODO
                break;
        }
        if (animation != null){
            animation.setDuration(anim.duration);
        }
    }

    private void addListener(){
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (animation == null){
                    return;
                }
                String text = s.toString();
                if (!text.isEmpty()){
                    char last = text.charAt(text.length()-1);

                    doAnimation(last, count - before > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void doAnimation(char last, boolean showUp) {
        final TextView textView = new TextView(getContext());
        textView.setTextColor(getCurrentTextColor());
        textView.setTextSize(getTextSize() / getPaint().density);
        textView.setText(String.valueOf(last));
        textView.setTypeface(getTypeface());
        textView.setGravity(Gravity.CENTER);

        contentContainer.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.measure(0, 0);

        playAnimation(textView, last, showUp, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contentContainer.removeView(textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void playAnimation(TextView textView, char last, boolean showUp, Animation.AnimationListener listener) {
        Rect rect = new Rect();
        Layout layout = getLayout();
        if (layout == null){
            return;
        }
        layout.getLineBounds(layout.getLineCount()-1, rect);

        Rect single = new Rect();
        Paint textPaint = getPaint();
        textPaint.getTextBounds(""+last, 0, 1, single);

        float textX = layout.getLineWidth(layout.getLineCount()-1);
        float textY = getBottom();

        AnimationSet animationSet = new AnimationSet(false);
        if (animation instanceof TextAnimation){
            ((TextAnimation)animation).applyValue(textX, textY, getWidth(), contentContainer.getHeight());
        }else{
            //get to the right position
            TextAnimation textAnimation = new TextAnimation(Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0);
            textAnimation.setDuration(animation.getDuration());
            textAnimation.applyValue(textX, textY, getWidth(), contentContainer.getHeight());
            animationSet.addAnimation(textAnimation);
        }
        animationSet.addAnimation(animation);
        animationSet.setDuration(animation.getDuration());

        textView.startAnimation(animationSet);
//        animationSet.setAnimationListener(listener);
    }

}
