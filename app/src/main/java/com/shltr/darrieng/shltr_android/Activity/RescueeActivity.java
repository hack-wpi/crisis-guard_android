package com.shltr.darrieng.shltr_android.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shltr.darrieng.shltr_android.Fragment.PagerAdapterFragment;
import com.shltr.darrieng.shltr_android.Model.IdModel;
import com.shltr.darrieng.shltr_android.Pojo.UserId;
import com.shltr.darrieng.shltr_android.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RescueeActivity extends AppCompatActivity implements Callback<UserId> {

    String userEmail;

    SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuee);

        preferences = getSharedPreferences(getString(R.string.base), MODE_PRIVATE);

//        Toast.makeText(this, userEmail, Toast.LENGTH_SHORT).show();

        if (getIntent().getExtras() != null) {
            userEmail = getIntent().getExtras().getString(getString(R.string.email));
            Gson gson = new GsonBuilder().create();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IdModel.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

            Call<UserId> call;

            IdModel idModel = retrofit.create(IdModel.class);

            call = idModel.retrieveId("Bearer " + preferences.getString(getString(R.string.token), null), userEmail);
            call.enqueue(this);
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapterFragment(getSupportFragmentManager(), this));
        viewPager.setCurrentItem(1);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    public void onResponse(Call<UserId> call, Response<UserId> response) {
        if (response.isSuccessful()) {
            Toast.makeText(this, response.body().getId() + "", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.id), response.body().getId());
            editor.apply();
        } else {
            Toast.makeText(this, "Network failure :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<UserId> call, Throwable t) {

    }
}
