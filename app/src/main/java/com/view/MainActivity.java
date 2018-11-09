package com.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.view.adapter.MainAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
  @BindView(R.id.recycler_view)
  RecyclerView rv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    rv.setHasFixedSize(true);

    MainAdapter adapter = new MainAdapter(this);
    rv.setAdapter(adapter);

    adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
      @Override
      public void onViewPagerItemClick(View view, int position) {
        Log.e("广告的点击事件", "onViewPagerItemClick: " + position);
      }

      @Override
      public void onServiceItemClick(View view, int position) {
        Log.e("服务的点击事件", "onServiceItemClick: " + position);
      }

      @Override
      public void onActivityItemClick(View view, int position) {
        Log.e("活动的点击事件", "onActivityItemClick: " + position);
      }

      @Override
      public void onOtherItemClick(View view, String tag) {
        Log.e("其他的点击事件", "onOtherItemClick: " + tag);
      }
    });

  }
}
