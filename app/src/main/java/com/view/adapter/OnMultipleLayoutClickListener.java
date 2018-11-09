package com.view.adapter;

import android.view.View;

/**
 * 自定义点击事件的接口、此接口只做示例，具体的根据项目来定义
 */
public interface OnMultipleLayoutClickListener {

  void onViewPagerItemClick(View view, int position);

  void onServiceItemClick(View view, int position);

  void onActivityItemClick(View view, int position);

  void onOtherItemClick(View view, String tag);

}
