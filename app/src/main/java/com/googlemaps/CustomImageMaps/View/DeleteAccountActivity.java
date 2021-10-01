package com.googlemaps.CustomImageMaps.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.googlemaps.CustomImageMaps.Core.UserContract;
import com.googlemaps.CustomImageMaps.Core.UserPresenter;
import com.googlemaps.CustomImageMaps.Model.User;
import com.googlemaps.CustomImageMaps.R;
import com.googlemaps.CustomImageMaps.utils.Config;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class DeleteAccountActivity extends AppCompatActivity implements UserContract.View {

    public FirebaseAuth mAuth;
    public StorageReference storageReference;
    public UserPresenter mPresenter;
    public DatabaseReference mReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);

        SharedPreferences sharedPrefSignOut = getSharedPreferences("signed_out", MODE_PRIVATE);
        SharedPreferences.Editor editor_SignOut;
        editor_SignOut = sharedPrefSignOut.edit();
        editor_SignOut.putBoolean("signed_out", true);
        editor_SignOut.apply();

        mAuth = FirebaseAuth.getInstance();
        mPresenter = new UserPresenter(this);
        mReference = FirebaseDatabase.getInstance().getReference().child(Config.USER_NODE);
        storageReference = FirebaseStorage.getInstance().getReference().child(Config.USER_NODE);

        User user = (User) getIntent().getSerializableExtra("user");

        mPresenter.DeleteUser(this, mReference, user, storageReference, mAuth);

    }


    @Override
    public void onUserSuccess(String message, Class<?> classh) {

    }

    @Override
    public void onUserDeleteSuccess(String message, Class<?> classh) {
        Toasty.success(this, message,
                Toast.LENGTH_SHORT).show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
        startActivity(new Intent(this, classh));
    }

    @Override
    public void onUserFailure(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_SHORT).show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onProcessStart() {

    }

    @Override
    public void onProcessEnd() {

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

    }

    @Override
    public void ongetUserById(User user) {

    }
}
