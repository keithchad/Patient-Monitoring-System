package com.robo101.patientmonitoringsystem.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.robo101.patientmonitoringsystem.R;
import com.robo101.patientmonitoringsystem.fragment.intro.FirstIntroFragment;
import com.robo101.patientmonitoringsystem.fragment.intro.SecondIntroFragment;
import com.robo101.patientmonitoringsystem.fragment.intro.ThirdIntroFragment;

public class IntroActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 3;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initialize();
    }

    private void initialize() {

        viewPager = findViewById(R.id.liquidPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(pagerAdapter);

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0 :
                    getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.firstIntroColor));
                    getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.firstIntroColor));
                    return new FirstIntroFragment();
                case 1 :
                    getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.secondIntroColor));
                    getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.secondIntroColor));
                    return new SecondIntroFragment();
                case 2 :
                    getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.firstIntroColor));
                    getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.firstIntroColor));
                    return new ThirdIntroFragment();
            }
            return null;
        }


        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}