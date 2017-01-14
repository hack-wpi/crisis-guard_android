package com.shltr.darrieng.shltr_android.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shltr.darrieng.shltr_android.R;

import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {


    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    public static NearbyFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NearbyFragment fragment = new NearbyFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
