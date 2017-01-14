package com.shltr.darrieng.shltr_android.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shltr.darrieng.shltr_android.Model.FlareModel;
import com.shltr.darrieng.shltr_android.Pojo.BaseResponse;
import com.shltr.darrieng.shltr_android.Pojo.FlarePojo;
import com.shltr.darrieng.shltr_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlareFragment extends Fragment
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Callback<BaseResponse> {

    public static final String ARG_PAGE = "ARG_PAGE";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient googleApiClient;

    private SharedPreferences preferences;

    private static final int REQUEST_FINE_LOCATION = 1;

    Location location;

    @BindView(R.id.flare_button)
    Button flareButton;

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
        preferences = getActivity().getSharedPreferences(getString(R.string.base), Context.MODE_PRIVATE);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }

        googleApiClient.connect();

        flareButton.setEnabled(false);

        flareButton.setOnClickListener(v -> {
            Gson gson = new GsonBuilder().create();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FlareModel.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

            FlareModel fm = retrofit.create(FlareModel.class);

            Call<BaseResponse> call;

            Toast.makeText(getActivity(), preferences.getInt(
                getString(R.string.id), -1) + "", Toast.LENGTH_SHORT).show();

            call = fm.createUser("Bearer " + preferences.getString(getString(R.string.token), null),
                new FlarePojo(location.getLongitude(),
                    location.getLatitude(),
                    preferences.getInt(
                        getString(R.string.id), -1)));

            call.enqueue(this);
        });
    }

    @Override
    public void onDestroyView() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
        super.onDestroyView();
    }

    /**
     * Helper method to request fine location permission.
     */
    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // partay permission granted
                } else {
                    Toast.makeText(
                        getActivity(), "We need location to fire a flare!", Toast.LENGTH_LONG).show();

                    // Try again for permissions
                    ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION);
                }

                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(
            getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            requestFineLocationPermission();
        } else {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            flareButton.setEnabled(true);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
        if (response.isSuccessful()) {
            Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_LONG).show();
        } else {
            try {
                Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "lol", Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(getActivity(), response.code(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {

    }
}
