package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagerAdapterFragment extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Person Identifier" , "Flare", "Nearby"};

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

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    
}
