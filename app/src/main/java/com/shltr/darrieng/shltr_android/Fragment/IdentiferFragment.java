package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shltr.darrieng.shltr_android.Model.ProductionModel;
import com.shltr.darrieng.shltr_android.Model.UploaderServiceModel;
import com.shltr.darrieng.shltr_android.Pojo.AgeModel;
import com.shltr.darrieng.shltr_android.Pojo.CompleteIdentificationModel;
import com.shltr.darrieng.shltr_android.Pojo.Match;
import com.shltr.darrieng.shltr_android.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
 * // * A simple {@link Fragment} subclass.
 * //
 */
public class IdentiferFragment extends Fragment implements Callback<CompleteIdentificationModel> {

    @BindView(R.id.fire_image_button)
    ImageButton fireImageButton;

    @BindView(R.id.ivPreview)
    ImageView ivPreview;

    @BindView(R.id.agglomerate_text_view)
    TextView agglomerateTextView;

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
                File f = new File(filePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                ivPreview.setImageBitmap(myBitmap);
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(getActivity(), uploadId, UploaderServiceModel.ENDPOINT + "api/uploadProductionPicture")
                    .addFileToUpload(filePath, "image") //Adding file
                    .addParameter("user_id", preferences.getInt(getString(R.string.id), -1) + "")
                    .addHeader("Authorization", "Bearer " + preferences.getString(getString(R.string.token), null))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(() -> {
                    Gson gson = new GsonBuilder().create();
                    Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ProductionModel.ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                    ProductionModel productionModel = retrofit.create(ProductionModel.class);
                    Call<CompleteIdentificationModel> call = productionModel.pilferData(
                        "Bearer " + preferences.getString(getString(R.string.token), null),
                        preferences.getInt(getString(R.string.id), -1));

//                    call.enqueue(this);
                    volleyRequest();
                }, 2500);

            } catch (Exception exc) {
                Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        returningWithResult = false;
    }

    private void volleyRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());  // this = context
        final String url = "http://hack.symerit.com/api/getProductionJson?user_id=" + preferences.getInt(getString(R.string.id), -1);

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String jsonsucks = response.toString();
                    String glob = "";
                    String goodGlob = "Similar user:\nName: ";
                    glob += jsonsucks.substring(jsonsucks.indexOf("name") + 10);
                    glob = glob.substring(0, glob.indexOf("\\"));
                    String bettterGlob = "Similar users\n" +"Name: " + glob;
                    agglomerateTextView.setText(bettterGlob);
                } catch (Exception e) {
                    agglomerateTextView.setText("Unable to determine similar users in our database.");
                }


            }
        }, error -> Log.wtf("DGLDG", error.getCause().toString()));

        queue.add(getRequest);


    }
    @Override
    public void onResponse(Call<CompleteIdentificationModel> call, Response<CompleteIdentificationModel> response) {
        if (response.isSuccessful()) {
            try {
                Log.wtf("DGL", new Gson().toJson(response));
                Log.wtf("DGL", response.raw().request().url().toString());
            } catch (Exception e) {
                Log.wtf("DGL", "Exception thrown");
            }

            boolean dontFailUserModel = true;
            boolean dontFailAgeModel = true;

            try {
                response.body().getAgeModel().getStatus();
            } catch (Exception e) {
                dontFailUserModel = false;
                Log.wtf("DGL", "Age status doesn't exist");
            }

            try {
                response.body().getUserModel().getStatus();
            } catch (Exception e) {
                dontFailAgeModel = false;
                Log.wtf("DGL", "User status doesn't exist");
            }

            if (dontFailUserModel && response.body().getUserModel().getStatus().equals("Good")) {
                if (response.body().getUserModel().getMatches().size() == 0 && response.body().getAgeModel().getStatus().equals("Good")) {
                    String ageText = "";
                    AgeModel am = response.body().getAgeModel();
                    ageText += "Gender: " + am.getGender() + "\n";
                    ageText += "Age: " + am.getAge() + "\n";
                    ageText += "Ethnicity: " + am.getAge() + "\n";
                    agglomerateTextView.setText(ageText);
                } else if (dontFailAgeModel) {
                    String userText = "Possible matches: \n";
                    for (Match match: response.body().getUserModel().getMatches()) {
                        userText += "\tName: " + match.getName() + " | Probability: " + match.getProb();
                    }
                    agglomerateTextView.setText(userText);
                }
            } else {
                agglomerateTextView.setText(getString(R.string.unable));
            }
        } else {
            Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<CompleteIdentificationModel> call, Throwable t) {
        Toast.makeText(getActivity(), "cry lots", Toast.LENGTH_SHORT).show();
        String stackTrace = Log.getStackTraceString(t);
        Log.wtf("DGL", stackTrace);
    }
}
