package com.example.android.morsecodehelper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private int count;

    public PagerAdapter(FragmentManager fm, int count){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new LetterActivity();
            case 1:
                return new PhraseActivity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}
