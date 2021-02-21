package com.example.android.morsecodehelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter);

        TabLayout tabLayout = findViewById(R.id.tabBar);
        TabItem tab1 = findViewById(R.id.tab_1);
        TabItem tab2 = findViewById(R.id.tab_2);
        ViewPager viewPager = findViewById(R.id.view_pager);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener (new
                   TabLayout.OnTabSelectedListener() {
                       @Override
                       public void onTabSelected(TabLayout.Tab tab) {
                           viewPager.setCurrentItem(tab.getPosition());
                       }

                       @Override
                       public void onTabUnselected(TabLayout.Tab tab) {

                       }

                       @Override
                       public void onTabReselected(TabLayout.Tab tab) {

                       }

                   });
    }
}