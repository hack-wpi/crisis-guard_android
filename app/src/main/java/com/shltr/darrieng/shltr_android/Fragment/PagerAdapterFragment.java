package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagerAdapterFragment extends FragmentStatePagerAdapter {

    Context context;

    public PagerAdapterFragment(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IdentiferFragment.newInstance(position + 1);
            case 1:
                return FlareFragment.newInstance(position + 1);
            case 2:
                return NearbyFragment.newInstance(position + 1);
            default:
                return FlareFragment.newInstance(position + 1);
        }
    }

    
}
