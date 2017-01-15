package com.shltr.darrieng.shltr_android.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.gson.Gson;
import com.shltr.darrieng.shltr_android.Model.UserRetrievalModel;
import com.shltr.darrieng.shltr_android.NearbyPersonAdapter;
import com.shltr.darrieng.shltr_android.Pojo.DeviceMessage;
import com.shltr.darrieng.shltr_android.Pojo.UserPojo;
import com.shltr.darrieng.shltr_android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.shltr.darrieng.shltr_android.Fragment.FlareFragment.ARG_PAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment
    implements Callback<UserPojo>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SharedPreferences preferences;

    MessageListener messageListener;

    GoogleApiClient googleApiClient;

    List<UserPojo> userList;

    NearbyPersonAdapter personAdapter;

    @BindView(R.id.person_list)
    RecyclerView personList;

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        userList = new ArrayList<>();
        personAdapter = new NearbyPersonAdapter(getContext(), userList);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(getActivity(), R.string.api_key, NearbyFragment.this)
                .build();
        }

        googleApiClient.connect();
        preferences = getActivity().getSharedPreferences(
            getString(R.string.base), Context.MODE_PRIVATE);

        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                Toast.makeText(getActivity(), "found message", Toast.LENGTH_SHORT).show();
                startNetworking(message);
            }
        };

        personList.setLayoutManager(new LinearLayoutManager(getActivity()));
        personList.setAdapter(personAdapter);
    }

    public void startNetworking(Message message) {
        Gson gson = new Gson();
        // make call to API with email: network to pull down Name and profile
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(UserRetrievalModel.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        UserRetrievalModel uModel = retrofit.create(UserRetrievalModel.class);

        Call<UserPojo> call = uModel.retrieveId("Bearer " +
            preferences.getString(getString(R.string.token), null),
            DeviceMessage.fromNearbyMessage(message).getMessageBody());

        call.enqueue(this);
    }

    public static NearbyFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NearbyFragment fragment = new NearbyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SubscribeOptions options = new SubscribeOptions.Builder()
            .setStrategy(Strategy.DEFAULT)
            .setCallback(new SubscribeCallback() {
                @Override
                public void onExpired() {
                    super.onExpired();
                }
            }).build();

        Nearby.Messages.subscribe(googleApiClient, messageListener, options);
        Message message = DeviceMessage.newNearbyMessage(preferences.getString(getString(R.string.email), null));
        Nearby.Messages.publish(googleApiClient, message);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
        if (response.isSuccessful()) {
            personAdapter.addUser(
                new UserPojo(response.body().getPicture(), response.body().getName()));
        } else {
            Toast.makeText(getActivity(), "Less cool partay", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<UserPojo> call, Throwable t) {
        Toast.makeText(getActivity(), "You failed", Toast.LENGTH_SHORT).show();
        String stackTrace = Log.getStackTraceString(t);
        Log.wtf("DGL", stackTrace);
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }
}
