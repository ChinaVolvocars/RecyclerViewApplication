package com.looping.viewpager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class LoopScroller extends Scroller {
  private int mDuration = 800;

  public LoopScroller(Context context) {
    super(context);
  }

  public LoopScroller(Context context, Interpolator interpolator) {
    super(context, interpolator);
  }

  public LoopScroller(Context context, Interpolator interpolator, boolean flywheel) {
    super(context, interpolator, flywheel);
  }

  @Override
  public void startScroll(int startX, int startY, int dx, int dy, int duration) {
    super.startScroll(startX, startY, dx, dy, mDuration);
  }

  @Override
  public void startScroll(int startX, int startY, int dx, int dy) {
    super.startScroll(startX, startY, dx, dy, mDuration);
  }

  public void setDuration(int time) {
    mDuration = time;
  }

}