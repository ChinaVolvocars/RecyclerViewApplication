package com.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.view.R;

import java.util.ArrayList;

public class HomeAdvAdapter extends PagerAdapter {

  private Context context;
  private final LayoutInflater layoutInflater;
  private ArrayList<String> pages = new ArrayList<>();

  public void setPages(ArrayList<String> pages) {
    this.pages = pages;
    notifyDataSetChanged();
  }

  public HomeAdvAdapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, final int position) {
    View itemView = layoutInflater.inflate(R.layout.item_home_adv_new, container, false);
    container.addView(itemView);
    // Find and populate data into the page (i.e set the image)
    RoundedImageView image = (RoundedImageView) itemView.findViewById(R.id.imageView);

    Glide.with(context)
            .load(pages.get(position))
            .into(image);

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != listener) {
          listener.onItemClick(v, position);
        }
      }
    });

    return itemView;
  }

  @Override
  public int getCount() {
    return pages == null ? 0 : pages.size();
  }


  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
    return view == o;
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }


  private OnItemClickListener listener;

  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.listener = listener;
  }

}
