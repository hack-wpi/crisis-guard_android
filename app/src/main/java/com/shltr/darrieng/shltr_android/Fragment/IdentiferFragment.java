package com.shltr.darrieng.shltr_android.Fragment;


import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.materialcamera.MaterialCamera;
import com.shltr.darrieng.shltr_android.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
// * A simple {@link Fragment} subclass.
// */
public class IdentiferFragment extends Fragment {

    @BindView(R.id.fire_image_button)
    ImageButton fireImageButton;

    @BindView(R.id.ivPreview)
    ImageView ivPreview;

    private final static int CAMERA_RQ = 6969;

    public IdentiferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_identifer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        fireImageButton.setOnClickListener((v) -> {
            File saveFolder = new File(Environment.getExternalStorageDirectory(), "camera");
            if (!saveFolder.mkdirs())
            new MaterialCamera(this).saveDir(saveFolder).stillShot().start(CAMERA_RQ);
        });
    }

    public static IdentiferFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        IdentiferFragment fragment = new IdentiferFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
