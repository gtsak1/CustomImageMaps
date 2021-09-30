package com.googlemaps.CustomImageMaps.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.googlemaps.CustomImageMaps.Core.UserContract;
import com.googlemaps.CustomImageMaps.Core.UserPresenter;
import com.googlemaps.CustomImageMaps.Model.User;
import com.googlemaps.CustomImageMaps.R;
import com.googlemaps.CustomImageMaps.utils.Config;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.googlemaps.CustomImageMaps.utils.Funct.redWrapper;

public class LoginActivity extends AppCompatActivity  implements UserContract.View {
    public UserPresenter mPresenter;
    public ProgressBar mBar;
    public DatabaseReference mReference;
    public FirebaseAuth mAuth;

    public RelativeLayout image_relative;
    public EditText mEmail, mPassword;

    SharedPreferences sharedPrefSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefSignOut = getSharedPreferences("signed_out", MODE_PRIVATE);
        if (!sharedPrefSignOut.getBoolean("signed_out", true)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
            }
            else {
                super.finish();
            }
            startActivity(new Intent(this, MapActivity.class));
        }

        else {
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            mPresenter = new UserPresenter(this);
            mBar = findViewById(R.id.progressBar);
            mReference = FirebaseDatabase.getInstance().getReference().child(Config.USER_NODE);

            image_relative = findViewById(R.id.image_relative);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                image_relative.setVisibility(View.GONE);
            }
            mEmail = findViewById(R.id.email);
            mPassword = findViewById(R.id.password);
            Button btnSignIn = findViewById(R.id.email_sign_in_button);

            TextView sign_up_link = findViewById(R.id.sign_up_link);
            sign_up_link.setOnClickListener(view -> {
                finish();
                startActivity(new Intent(this, SignUp.class));
            });


            btnSignIn.setOnClickListener(view -> {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                redWrapper(this, mEmail);
                redWrapper(this, mPassword);

                if (email.isEmpty() || pass.isEmpty()) {
                    if (email.isEmpty())
                        mEmail.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    if (pass.isEmpty())
                        mPassword.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    Toasty.error(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(email, pass);
                    mPresenter.SignInUser(this, user, mAuth);
                }
            });

        }

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            image_relative.setVisibility(View.GONE);
        else
            image_relative.setVisibility(View.VISIBLE);
    }

    Class<?> intentClass;
    @Override
    public void onUserSuccess(String message, Class<?> classh) {
        intentClass = classh;
        mPresenter.getUser(mReference, mAuth);
    }

    @Override
    public void onUserDeleteSuccess(String message, Class<?> classh) {

    }

    @Override
    public void onUserFailure(String message) {
        Toasty.error(this, message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProcessStart() {
        mBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProcessEnd() {
        mBar.setVisibility(View.GONE);
    }

    @Override
    public void onProcessUploadStart() {

    }

    @Override
    public void onProcessUploadEnd() {

    }

    @Override
    public void onUserRead(ArrayList<User> users) {

    }

    @Override
    public void onUserUpdate(User user) {

    }

    @Override
    public void onUserProfilePhotoUpdate(String image_Url) {

    }

    @Override
    public void onUserVisibilityStatusUpdate(boolean visible) {

    }

    @Override
    public void onUserLoginStatusUpdate(boolean loggedIn, Class<?> classh) {

    }

    @Override
    public void onUserDelete(User user) {

    }

    @Override
    public void ongetUser(User user) {
        mPresenter.UpdateUserLoginStatus(this, mReference, user.getId(), true, mAuth);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
        SharedPreferences.Editor editor_SignOut = sharedPrefSignOut.edit();
        editor_SignOut.putBoolean("signed_out", false);
        editor_SignOut.apply();
        Intent myIntent;
        myIntent = new Intent(this, intentClass);
        myIntent.putExtra("user", user);
        startActivity(myIntent);
    }

    @Override
    public void ongetUserById(User user) {

    }
}
