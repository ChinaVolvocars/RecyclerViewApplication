package com.looping.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


import com.looping.viewpager.transformer.DefaultTransformer;

import java.lang.reflect.Field;

/**
 * A ViewPager subclass enabling infinte scrolling of the viewPager elements
 * <p>
 * When used for paginating views (in opposite to fragments), no code changes
 * should be needed only change xml's from <android.support.v4.view.ViewPager>
 * to <com.imbryk.viewPager.LoopViewPager>
 * <p>
 * If "blinking" can be seen when paginating to first or last view, simply call
 * seBoundaryCaching( true ), or change DEFAULT_BOUNDARY_CASHING to true
 * <p>
 * When using a FragmentPagerAdapter or FragmentStatePagerAdapter,
 * additional changes in the adapter must be done.
 * The adapter must be prepared to create 2 extra items e.g.:
 * <p>
 * The original adapter creates 4 items: [0,1,2,3]
 * The modified adapter will have to create 6 items [0,1,2,3,4,5]
 * with mapping realPosition=(position-1)%count
 * [0->3, 1->0, 2->1, 3->2, 4->3, 5->0]
 */
public class LoopViewPager extends ViewPager {

  private static final boolean DEFAULT_BOUNDARY_CASHING = true;

  OnPageChangeListener mOuterPageChangeListener;
  private LoopPagerAdapterWrapper mAdapter;
  private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;
  private boolean isAutoPlay = true;
  private long delayTime = 3000;
  private int count = 0;
  private int currentItem;

  private WeakHandler handler = new WeakHandler();

  private final Runnable task = new Runnable() {
    @Override
    public void run() {
      if (count > 1 && isAutoPlay) {
        currentItem = currentItem % (count + 1) + 1;
        LoopViewPager.this.setCurrentItem(currentItem, false);
        handler.postDelayed(task, delayTime);
      }
    }
  };
  private LoopScroller mScroller;


  /**
   * helper function which may be used when implementing FragmentPagerAdapter
   *
   * @param position
   * @param count
   * @return (position - 1)%count
   */
  public static int toRealPosition(int position, int count) {
    position = position - 1;
    if (position < 0) {
      position += count;
    } else {
      position = position % count;
    }
    return position;
  }

  /**
   * If set to true, the boundary views (i.e. first and last) will never be destroyed
   * This may help to prevent "blinking" of some views
   *
   * @param flag
   */
  public void setBoundaryCaching(boolean flag) {
    mBoundaryCaching = flag;
    if (mAdapter != null) {
      mAdapter.setBoundaryCaching(flag);
    }
  }

  @Override
  public void setAdapter(PagerAdapter adapter) {
    currentItem = 0;
    mAdapter = new LoopPagerAdapterWrapper(adapter);
    mAdapter.setBoundaryCaching(mBoundaryCaching);
    super.setAdapter(mAdapter);
    count = adapter.getCount();
    setCurrentItem(0, false);
    if (isAutoPlay) startAutoPlay();

  }

  @Override
  public PagerAdapter getAdapter() {
    return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
  }

  @Override
  public int getCurrentItem() {
    return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
  }

  public void setCurrentItem(int item, boolean smoothScroll) {
    int realItem = mAdapter.toInnerPosition(item);
    super.setCurrentItem(realItem, smoothScroll);
  }

  @Override
  public void setCurrentItem(int item) {
    if (getCurrentItem() != item) {
      setCurrentItem(item, false);
    }
  }

  @Override
  public void setOnPageChangeListener(OnPageChangeListener listener) {
    mOuterPageChangeListener = listener;
  }

  public LoopViewPager(Context context) {
    super(context);
    init();
    initViewPagerScroll();
    try {
      setPageTransformer(true, DefaultTransformer.class.newInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
//    setOffscreenPageLimit(count); //设置内存中预加载页面数
  }

  public LoopViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
    initViewPagerScroll();
    try {
      setPageTransformer(true, DefaultTransformer.class.newInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
//    setOffscreenPageLimit(count); //设置内存中预加载页面数
  }

  private void init() {
    super.setOnPageChangeListener(onPageChangeListener);
  }

  private void initViewPagerScroll() {
    try {
      Field mField = ViewPager.class.getDeclaredField("mScroller");
      mField.setAccessible(true);
      mScroller = new LoopScroller(this.getContext());
      mScroller.setDuration(800);
      mField.set(this, mScroller);
    } catch (Exception e) {
      Log.e("修复闪频", e.getMessage());
    }
  }


  private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

    private float mPreviousOffset = -1;
    private float mPreviousPosition = -1;

    @Override
    public void onPageSelected(int position) {
      int realPosition = mAdapter.toRealPosition(position);
      currentItem = realPosition;
      if (mPreviousPosition != realPosition) {
        mPreviousPosition = realPosition;
        if (mOuterPageChangeListener != null) {
          mOuterPageChangeListener.onPageSelected(realPosition);
        }
      }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      int realPosition = position;
      if (mAdapter != null) {
        realPosition = mAdapter.toRealPosition(position);
        if (positionOffset == 0 && mPreviousOffset == 0
                && (position == 0 || position == mAdapter.getCount() - 1)) {
          setCurrentItem(realPosition, false);
        }
      }
      mPreviousOffset = positionOffset;
      if (mOuterPageChangeListener != null) {
        if (realPosition != mAdapter.getRealCount() - 1) {
          mOuterPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
        } else {
          if (positionOffset > .5) {
            mOuterPageChangeListener.onPageScrolled(0, 0, 0);
          } else {
            mOuterPageChangeListener.onPageScrolled(realPosition, 0, 0);
          }
        }
      }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
      if (mOuterPageChangeListener != null) {
        mOuterPageChangeListener.onPageScrollStateChanged(state);
      }
      if (mAdapter != null) {
        int position = LoopViewPager.super.getCurrentItem();
        int realPosition = mAdapter.toRealPosition(position);
        if (state == ViewPager.SCROLL_STATE_IDLE &&
                (position == 0 || position == mAdapter.getCount() - 1)) {
          setCurrentItem(realPosition, false);
        }
      }
    }
  };

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isAutoPlay) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
          delayTime = 3000;
          stopAutoPlay();
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_OUTSIDE:
          startAutoPlay();
          break;
        default:
          break;
      }
    }
    return super.onTouchEvent(event);
  }

  private int preX;

  @Override
  public boolean onInterceptTouchEvent(MotionEvent even) {
    if (even.getAction() == MotionEvent.ACTION_DOWN) {
      preX = (int) even.getX();
    } else {
      if (Math.abs((int) even.getX() - preX) > 4) {
        return true;
      } else {
        preX = (int) even.getX();
      }
    }
    return super.onInterceptTouchEvent(even);
  }

  public void startAutoPlay() {
    handler.removeCallbacks(task);
    handler.postDelayed(task, delayTime);
  }

  public void stopAutoPlay() {
    handler.removeCallbacks(task);
  }

  public LoopViewPager isAutoPlay(boolean isAutoPlay) {
    this.isAutoPlay = isAutoPlay;
    return this;
  }

  public LoopViewPager setDelayTime(long delayTime) {
    this.delayTime = delayTime;
    return this;
  }


}