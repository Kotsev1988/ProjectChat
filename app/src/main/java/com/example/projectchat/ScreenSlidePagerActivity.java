package com.example.projectchat;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;


public class ScreenSlidePagerActivity extends AppCompatActivity {
    private static final int NUM_PAGES=5;
    private ViewPager2 mPager;
    //private PagerAdapter pagerAdapter;
    FragmentStateAdapter pagerAdapter;
Toolbar toolbar;
String pathVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        mPager=(ViewPager2) findViewById(R.id.pager1);
        pagerAdapter=new ScreenSlidePagerAdapter(this);
        mPager.setAdapter(pagerAdapter);
        toolbar=(Toolbar)findViewById(R.id.toolbarvideo);

        setSupportActionBar(toolbar);
        pathVideo=getIntent().getStringExtra("pathVideo");


    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("landscapeAct", "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("PORTRAITAct", "PORTRAIT");
        }
    }

    @Override
    public void onBackPressed(){
        if (mPager.getCurrentItem()==0){
            super.onBackPressed();
        } else{
            mPager.setCurrentItem(mPager.getCurrentItem()-1);
        }
    }



    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fm){

            super(fm);
        }



        @NonNull
        @Override
        public Fragment createFragment(int position) {
           /* ScreenSlidePageFragment screenSlidePageFragment=new ScreenSlidePageFragment();
            Bundle bundle=new Bundle();
            bundle.putString("pathVideo", pathVideo);
            screenSlidePageFragment.setArguments(bundle);*/
Log.d("pathFragmentActivity", ""+pathVideo);
            return ScreenSlidePageFragment.newInstance(pathVideo);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}