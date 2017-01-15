package com.shltr.darrieng.shltr_android.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shltr.darrieng.shltr_android.Model.PwModel;
import com.shltr.darrieng.shltr_android.Model.RegisterModel;
import com.shltr.darrieng.shltr_android.Pojo.PasswordPojo;
import com.shltr.darrieng.shltr_android.Pojo.RegisterPojo;
import com.shltr.darrieng.shltr_android.Pojo.UserToken;
import com.shltr.darrieng.shltr_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.shltr.darrieng.shltr_android.R.string.email;

public class SignupActivity extends AppCompatActivity implements Callback<UserToken> {

    @BindView(R.id.signup_button)
    Button signupButton;

    @BindView(R.id.login_button)
    Button loginButton;

    @BindView(R.id.button_screen)
    LinearLayout buttonView;

    @BindView(R.id.text_screen)
    LinearLayout textScreenView;

    @BindView(R.id.enterInputView)
    TextInputEditText enterInputView;

    @BindView(R.id.enter_pw_view)
    TextInputEditText enterPwView;

    @BindView(R.id.go_button)
    FloatingActionButton goButton;

    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;

    @BindView(R.id.name_view)
    TextInputEditText nameView;

    Boolean isSigningUp;

    SharedPreferences preferences;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        preferences = getSharedPreferences(getString(R.string.base), MODE_PRIVATE);
        getSupportActionBar().hide();
        deterministicSkip();
        signupButton.setOnClickListener(v -> setUpForInput(true));
        loginButton.setOnClickListener(v -> setUpForInput(false));
        goButton.setOnClickListener(v -> {
            if (validateInput()) {
                // send to server, partay
                startNetworking();
            } else {
                // no-op
            }
        });
    }

    private boolean validateInput() {
        String nameInputView = enterInputView.getText().toString();
        String passwordInput = enterPwView.getText().toString();
        boolean isValid = false;
        try {
            Integer.parseInt(nameInputView);
            isValid = true;
        } catch (Exception e) {
            // no-op
        }

        if (nameInputView.contains("@")) {
            if (nameInputView.substring(nameInputView.indexOf("@")).contains(".")) {
                isValid = true;
            }
        }

        if (passwordInput.isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    private void setUpForInput(boolean isSigningUp) {
        buttonView.setVisibility(GONE);
        this.isSigningUp = isSigningUp;
        textScreenView.setVisibility(View.VISIBLE);
        if (isSigningUp) {
            nameLayout.setVisibility(View.VISIBLE);
        } else {
            nameLayout.setVisibility(GONE);
        }
    }

    private void setUpForChoice() {
        textScreenView.setVisibility(View.GONE);
        buttonView.setVisibility(View.VISIBLE);
    }

    /**
     * Skip this activity if user is already logged in.
     */
    private void deterministicSkip() {
        if (preferences.contains(getString(R.string.token))) {
            Intent intent = new Intent(this, RescueeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (isSigningUp != null) {
            setUpForChoice();
            isSigningUp = null;
        }
    }

    private void passData(String token) {
        editor = preferences.edit();
        editor.putString(getString(R.string.token), token);
        editor.apply();
    }

    @Override
    public void onResponse(Call<UserToken> call, Response<UserToken> response) {
        if (response.isSuccessful()) {
            passData(response.body().getAccess_token());
            Intent intent = new Intent(this, RescueeActivity.class);
            intent.putExtra(getString(email), enterInputView.getText().toString());
            editor.putString(getString(R.string.email), enterInputView.getText().toString());
            editor.apply();
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to login " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<UserToken> call, Throwable t) {

    }

    public void startNetworking() {
        Gson gson = new GsonBuilder().create();
        if (!isSigningUp) {
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PwModel.LOGIN_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

            PwModel passwordModel = retrofit.create(PwModel.class);

            Call<UserToken> call;
            PasswordPojo pwpj =
                new PasswordPojo(enterInputView.getText().toString(), enterPwView.getText().toString());
            call = passwordModel.loginUser(pwpj);
            call.enqueue(this);
        } else {
            Call<Void> call;
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RegisterModel.LOGIN_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

            RegisterModel rm = retrofit.create(RegisterModel.class);
            call = rm.createUser(
                new RegisterPojo(
                    nameView.getText().toString(),
                    enterInputView.getText().toString(),
                    enterPwView.getText().toString()));

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Successful register, please log in", Toast.LENGTH_SHORT).show();
                        setUpForInput(false);
                    } else {
                        Toast.makeText(SignupActivity.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });

        }
    }
}

