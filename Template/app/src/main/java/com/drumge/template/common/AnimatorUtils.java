package com.drumge.template.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatorUtils {
    public static AnimatorSet dropDownAnimator(final View myView) {
		AnimatorSet set = new AnimatorSet();

        float height = 80.0f;
        if(myView.getHeight() != 0){
          //  height = myView.getHeight();
        }
		set.playTogether(  
		    //ObjectAnimator.ofFloat(myView, "alpha", 0.0f,1.0f),
		    ObjectAnimator.ofFloat(myView, "y",height * -1,0.0f)
		);  
		set.setDuration(300);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                myView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return set; 
	}
    public static AnimatorSet dropUpAnimator(final View myView) {
		AnimatorSet set = new AnimatorSet();
        float height = 80.0f;
        if(myView.getHeight() != 0){
          //  height = myView.getHeight();
        }
		set.playTogether(
                //ObjectAnimator.ofFloat(myView, "alpha", 1.0f,0.0f),
                ObjectAnimator.ofFloat(myView, "y", 0.0f, height * -1)
        );
		set.setDuration(300);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                myView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return set; 
	}

    public static AnimatorSet pushInFromLeftAnimator(final View myView) {
		AnimatorSet set = new AnimatorSet();
        if(myView.getContext()==null)return set;
        int width =ResolutionUtils.getScreenWidth(myView.getContext());
		set.playTogether(
//		    ObjectAnimator.ofFloat(myView, "alpha", 0f,1f),
		    ObjectAnimator.ofFloat(myView, "translationX", width, 0)
		);
		set.setDuration(300);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                myView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        set.start();
        return set;
	}

    public static AnimatorSet popOutFromRightAnimator(final View myView) {
		AnimatorSet set = new AnimatorSet();
        if(myView.getContext()==null)return set;
        int width =ResolutionUtils.getScreenWidth(myView.getContext());
		set.playTogether(
//                ObjectAnimator.ofFloat(myView, "alpha", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(myView, "translationX", 0, width)
        );
		set.setDuration(300);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                myView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        set.start();
        return set;
	}

	public static ObjectAnimator rotateRepeatAnimator(final Object myView) {  
		LinearInterpolator interpolator = new LinearInterpolator();
		ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(myView, "rotation", 0.0f,359.0f);
		rotateAnim.setDuration(5000);
		rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
		rotateAnim.setRepeatMode(ObjectAnimator.RESTART);
		rotateAnim.setInterpolator(interpolator);
		
        return rotateAnim; 
	}

    public static AnimatorSet scaleIn(final View myView){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(myView, "alpha", 0f,1),
                ObjectAnimator.ofFloat(myView,"scaleX",0.1f,1f),
                ObjectAnimator.ofFloat(myView,"scaleY",0.1f,1f)
                );
        set.setDuration(400);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                myView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return set;
    }

    public static AnimatorSet alphaShow(final View myView){
        AnimatorSet set = new AnimatorSet();

        set.playTogether(
                ObjectAnimator.ofFloat(myView, "alpha", 0.0f,1.0f)
                );
        set.setDuration(300);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                myView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return set;
    }

    public static AnimatorSet alphaHide(final View myView){
        AnimatorSet set = new AnimatorSet();

        set.playTogether(
                ObjectAnimator.ofFloat(myView, "alpha", 1.0f,0.0f)
        );
        set.setDuration(300);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                myView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return set;
    }
	public static ObjectAnimator alphaRepeatAnimator(final Object myView) {
        Keyframe kf0 = Keyframe.ofFloat(0, 0.0f);
        Keyframe kf1 = Keyframe.ofFloat(0.1f, 1.0f);
        Keyframe kf2 = Keyframe.ofFloat(0.5f, 1.0f);
        Keyframe kf3 = Keyframe.ofFloat(0.6f, 0.0f);
        Keyframe kf4 = Keyframe.ofFloat(1f, 0.0f);

        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1,kf2,kf3,kf4);
        ObjectAnimator rotationAnim = ObjectAnimator.ofPropertyValuesHolder(myView, pvhRotation);
        rotationAnim.setDuration(6000);
        rotationAnim.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnim.setRepeatMode(ObjectAnimator.RESTART);

        return rotationAnim;
    }

    public static ObjectAnimator alphaAnimator(final Object myView) {
            Keyframe kf0 = Keyframe.ofFloat(0, 0.0f);
            Keyframe kf1 = Keyframe.ofFloat(0.06f, 1.0f);
            Keyframe kf2 = Keyframe.ofFloat(0.94f, 1.0f);
            Keyframe kf3 = Keyframe.ofFloat(1f, 0.0f);

            PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1,kf2,kf3);
            ObjectAnimator rotationAnim = ObjectAnimator.ofPropertyValuesHolder(myView, pvhRotation);
            rotationAnim.setDuration(5600);

        return rotationAnim;
    }
    public static void AnimatorStart(ImageView view){
        if (view.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) view.getDrawable()).start();
        }
    }
    public static void AnimatorStop(ImageView view){
        if (view.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable)view.getDrawable()).stop();
        }
    }
}