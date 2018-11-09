package com.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import com.view.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeServiceAdapter extends RecyclerView.Adapter<HomeServiceAdapter.HomeServiceViewHolder> {

  private OnItemClickListener listener;

  private Context context;
  private final LayoutInflater layoutInflater;
  private ArrayList<String> pages = new ArrayList<>();

  public void setPages(ArrayList<String> pages) {
    this.pages = pages;
    notifyDataSetChanged();
  }

  public HomeServiceAdapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public HomeServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new HomeServiceViewHolder(layoutInflater.inflate(R.layout.item_service, parent, false));
  }

  @Override
  public void onBindViewHolder(HomeServiceViewHolder holder, final int position) {
    RoundedImageView imageView = holder.imageView;

    Glide.with(context)
            .load(pages.get(position))
            .into(imageView);

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (null != listener) {
          listener.onItemClick(view, position);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return pages.size();
  }

  public class HomeServiceViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imageView)
    RoundedImageView imageView;

    public HomeServiceViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    this.listener = listener;
  }


}
