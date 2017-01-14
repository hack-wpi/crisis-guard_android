package com.shltr.darrieng.shltr_android.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shltr.darrieng.shltr_android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlareFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public FlareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flare, container, false);
    }

    public static FlareFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FlareFragment fragment = new FlareFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
