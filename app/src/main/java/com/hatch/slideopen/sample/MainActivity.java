package com.hatch.slideopen.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.hatch.slideopen.SlideInterface;
import com.hatch.slideopen.SlideOpenLayout;
import com.hatch.slideopen.sample.viewpagerindicator.CirclePageIndicator;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String[] URLS = {
            "http://img.shop.zainanjing365.com/image/2018-08/5omdcqezh61gyzxsjuj02mfqi.jpg"
            , "http://img.shop.zainanjing365.com/image/2018-08/4db5sjv76kbm13yuvj2js2rav.jpg"
            , "http://img.shop.zainanjing365.com/image/2018-08/1fxjggn0po7hqkebsgbc4djuz.jpg"
            , "http://img.shop.zainanjing365.com/image/2018-08/1vqri2mib2c155tqdbw33d3dl.jpg"
    };
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SlideInterface mSlideOpen = findViewById(R.id.slide_open_layout);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        final BannerAdapter adapter = new BannerAdapter(Arrays.asList(URLS));
        View view = makeItemView(adapter, new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //最后一页 SlideLayout才执行拦截
                mSlideOpen.setUnableToDrag(position != adapter.getCount() - 1);
            }
        });

        mSlideOpen.setOnOpenListener(new SlideOpenLayout.OnOpenListener() {
            @Override
            public void onOpen() {
                startActivity(new Intent(MainActivity.this, GoodsDetailActivity.class));
                mIndicator.setCurrentItem(0);
            }
        });

        mSlideOpen.setOnScrollListener(new SlideOpenLayout.OnScrollListener() {
            @Override
            public void onPageScrolled(int scrollX) {
                mIndicator.offsetX(scrollX);
            }
        });

        mSlideOpen.instantiateItem(view);
        mSlideOpen.setUnableToDrag(mPager.getCurrentItem() != adapter.getCount() - 1);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });
    }


    public View makeItemView(PagerAdapter adapter, ViewPager.OnPageChangeListener listener) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_banner, null);

        mPager = view.findViewById(R.id.pager);
        mIndicator = view.findViewById(R.id.indicator);

        mPager.setAdapter(adapter);
        mPager.addOnPageChangeListener(listener);

        mIndicator.setViewPager(mPager);
        return view;
    }
}
