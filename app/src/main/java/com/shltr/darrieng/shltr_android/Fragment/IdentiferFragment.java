package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.shltr.darrieng.shltr_android.Model.UploaderServiceModel;
import com.shltr.darrieng.shltr_android.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
 * // * A simple {@link Fragment} subclass.
 * //
 */
public class IdentiferFragment extends Fragment implements Callback<ResponseBody> {

    @BindView(R.id.fire_image_button)
    ImageButton fireImageButton;

    @BindView(R.id.ivPreview)
    ImageView ivPreview;

    SharedPreferences preferences;

    boolean returningWithResult;

    String filePath;

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

        preferences = getActivity().getSharedPreferences(getString(R.string.base), MODE_PRIVATE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RQ && resultCode == RESULT_OK) {
            returningWithResult = true;
            filePath = data.getDataString().substring(5);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (returningWithResult) {
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(getActivity(), uploadId, UploaderServiceModel.ENDPOINT + "api/uploadProfilePicture")
                    .addFileToUpload(filePath, "image") //Adding file
                    .addParameter("user_id", preferences.getInt(getString(R.string.id), -1) + " ")
                    .addHeader("Authorization", "Bearer " + preferences.getString(getString(R.string.token), null))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        returningWithResult = false;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            Toast.makeText(getActivity(), response.code() + " " + response.errorBody().string(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "cry", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Toast.makeText(getActivity(), "You failed", Toast.LENGTH_SHORT).show();
        String stackTrace = Log.getStackTraceString(t);
        Log.wtf("DGL", stackTrace);
    }
}
