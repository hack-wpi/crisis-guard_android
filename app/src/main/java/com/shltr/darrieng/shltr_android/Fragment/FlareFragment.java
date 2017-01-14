package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.shltr.darrieng.shltr_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlareFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    @BindView(R.id.flare_button)
    Button flareButton;

    interface OnLocationRetrievedListener {
        void setOnLocationRetrievedListener();
    }

    public FlareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flare, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        flareButton.setEnabled(false);

        flareButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Firing a flare", Toast.LENGTH_SHORT).show();
        });
    }

    public static FlareFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FlareFragment fragment = new FlareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
