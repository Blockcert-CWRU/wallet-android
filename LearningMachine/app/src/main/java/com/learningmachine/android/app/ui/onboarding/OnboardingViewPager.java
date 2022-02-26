package com.learningmachine.android.app.ui.onboarding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * ViewPager that disables swiping.
 */
public class OnboardingViewPager extends ViewPager {

    public OnboardingViewPager(Context context) {
        super(context);
    }

    public OnboardingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
