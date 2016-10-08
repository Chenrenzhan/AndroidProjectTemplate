package com.drumge.template.view.pager;

import android.os.Bundle;

import com.drumge.template.view.fragment.BaseLinkFragment;


/**
 * Created by yuanxiaoming on 16/1/22.
 */
public class IPagerFragment extends BaseLinkFragment implements IPagerPosition {

    private int mPos = -1;
    public boolean isSelected = false;

     /* private View mBottomView;
     private View mContent;
     private Animation animaUp;
     private Animation animaDown;
     private GestureDetectorCompat mDetector;
     private boolean mIsAnimaUp = false;
     private boolean mIsAnimaDown = false;
     private Animation.AnimationListener animationListener;
     private int mBottomHeight;
     private ValueAnimator mValueAnimUp;
     private ValueAnimator mValueAnimDown;
     private ValueAnimator.AnimatorUpdateListener mValueUpdateListener;
     private Animator.AnimatorListener mValueListener;*/
     // protected ImageView mTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setPosition(int position) {
        mPos = position;
    }

    @Override
    public void onSelected(int position) {
        isSelected = true;
    }

    @Override
    public void onUnSelected(int position) {
        isSelected = false;
    }

    @Override
    public void onPageScrollComplete(int position) {

    }

    @Override
    public void onRestore() {

    }

   /* protected GestureDetectorCompat getGestureDetector() {
        return mDetector;
    }*/

    protected void initGesture() {
        /*if (getActivity() == null) {
            return;
        }*/
        //mBottomView = getActivity().findViewById(R.id.home_bottom);
        //mContent = getActivity().findViewById(R.id.container_fragment_content);
//        try {
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            Fragment main = fm.findFragmentByTag(MainActivity.TAG_LIVING_FRAGMENT);
//            mTop = (ImageView) main.getView().findViewById(R.id.one_key_to_top);
//        } catch (Throwable throwable) {
//            MLog.error(this, throwable);
//        }

        //initPropertyAnim();
       /* mDetector = new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                *//*if(velocityY > 100) {
                    if(mBottomView.getVisibility() == View.GONE && !mIsAnimaUp)
                        mValueAnimUp.start();
//                        mBottomView.startAnimation(animaUp);
                } else if(velocityY < -100) {
                    if(mBottomView.getVisibility() == View.VISIBLE && !mIsAnimaDown)
//                        mBottomView.startAnimation(animaDown);
                        mValueAnimDown.start();
                }*//*
                return false;
            }
        });*/
    }

 /*   private void initPropertyAnim() {
        mBottomHeight = DimenConverter.dip2px(YYMobileApp.gContext, 70);
        mValueUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                mBottomView.getLayoutParams().height = value;
                mBottomView.requestLayout();
            }
        };
        mValueListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(animation == mValueAnimUp) {
                    mIsAnimaUp = true;
                    mBottomView.setVisibility(View.VISIBLE);
//                    mTop.setVisibility(View.VISIBLE);
                } else if(animation == mValueAnimDown) {
                    mIsAnimaDown = true;
                    mBottomView.setVisibility(View.VISIBLE);
//                    mTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(animation == mValueAnimUp) {
                    mIsAnimaUp = false;
                    mIsAnimaDown = false;
                } else if(animation == mValueAnimDown) {
                    mBottomView.setVisibility(View.GONE);
                    mIsAnimaDown = false;
                    mIsAnimaUp = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
        mValueAnimUp = ValueAnimator.ofInt(0, mBottomHeight);
        mValueAnimUp.setDuration(300);
        mValueAnimUp.addUpdateListener(mValueUpdateListener);
        mValueAnimUp.addListener(mValueListener);
        mValueAnimDown = ValueAnimator.ofInt(mBottomHeight, 0);
        mValueAnimDown.setDuration(300);
        mValueAnimDown.addUpdateListener(mValueUpdateListener);
        mValueAnimDown.addListener(mValueListener);
    }*/

   /* private void initTweenAnim() {
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(animation == animaUp) {
                    mIsAnimaUp = true;
                    mBottomView.setVisibility(View.VISIBLE);
                } else if(animation == animaDown) {
                    mIsAnimaDown = true;
                    mBottomView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(animation == animaUp) {
                    mIsAnimaDown = false;
                    mIsAnimaUp = false;
                } else if(animation == animaDown) {
                    mBottomView.setVisibility(View.GONE);
                    mIsAnimaDown = false;
                    mIsAnimaUp = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        animaUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
        animaDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_bottom);
        animaUp.setAnimationListener(animationListener);
        animaDown.setAnimationListener(animationListener);
    }*/

}
