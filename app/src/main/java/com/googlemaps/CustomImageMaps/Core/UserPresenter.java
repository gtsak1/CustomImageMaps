package com.googlemaps.CustomImageMaps.Core;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.googlemaps.CustomImageMaps.Model.User;

import java.util.ArrayList;

public class UserPresenter implements UserContract.Presenter, UserContract.onOperationListener {
    private UserContract.View mView;
    private UserInteractor mInteractor;

    public UserPresenter(UserContract.View mView) {
        this.mView = mView;
        mInteractor = new UserInteractor(this);
    }

    @Override
    public void SignInUser(Context context, User user, FirebaseAuth mAuth) {
        mInteractor.performSignInUser(context, user, mAuth);
    }

    @Override
    public void createNewUser(Context context, DatabaseReference reference, User user, FirebaseAuth mAuth, StorageReference storageReference) {
        mInteractor.performCreateUser(context, reference , user, mAuth, storageReference);
    }

    @Override
    public void readUsers(DatabaseReference reference) {
        mInteractor.performReadUsers(reference);
    }

    @Override
    public void UpdateUser(Context context, DatabaseReference reference, User user, StorageReference storageReference) {
        mInteractor.performUpdateUser(context, reference, user, storageReference);
    }

    @Override
    public void UpdateUserProfilePhoto(Context context, DatabaseReference reference, User user, StorageReference storageReference) {
        mInteractor.performUpdateUserProfilePhoto(context, reference, user, storageReference);
    }

    @Override
    public void UpdateUserLoginStatus(Context context, DatabaseReference reference, String user_id, boolean loggedIn, FirebaseAuth mAuth) {
        mInteractor.performUpdateUserLoginStatus(context, reference, user_id, loggedIn, mAuth);
    }

    @Override
    public void UpdateUserVisibilityStatus(Context context, DatabaseReference reference, String user_id, boolean visible) {
        mInteractor.performUpdateUserVisibilityStatus(context, reference, user_id, visible);
    }

    @Override
    public void UpdateUserLocation(Context context, DatabaseReference reference, String user_id, LatLng location, String address) {
        mInteractor.performUpdateUserLocation(context, reference, user_id, location, address);
    }

    @Override
    public void DeleteUser(Context context, DatabaseReference reference, User user, StorageReference storageReference, FirebaseAuth mAuth) {
        mInteractor.performDeleteUser(context, reference, user, storageReference, mAuth);
    }

    @Override
    public void getUser(DatabaseReference reference, FirebaseAuth auth) {
        mInteractor.performgetUser(reference, auth);
    }

    @Override
    public void getUserById(DatabaseReference reference, String user_id) {
        mInteractor.performgetUserById(reference, user_id);
    }

    @Override
    public void onSuccess(String message, Class<?> classh) {
        mView.onUserSuccess(message, classh);
    }

    @Override
    public void onDeleteSuccess(String message, Class<?> classh) {
        mView.onUserDeleteSuccess(message, classh);
    }

    @Override
    public void onFailure(String message) {
        mView.onUserFailure(message);
    }

    @Override
    public void onStart() {
        mView.onProcessStart();
    }

    @Override
    public void onUploadStart() {
        mView.onProcessUploadStart();
    }

    @Override
    public void onEnd() {
        mView.onProcessEnd();
    }

    @Override
    public void onUploadEnd() {
        mView.onProcessUploadEnd();
    }

    @Override
    public void onRead(ArrayList<User> users) {
        mView.onUserRead(users);
    }

    @Override
    public void onUpdate(User user) {
        mView.onUserUpdate(user);
    }

    @Override
    public void onProfilePhotoUpdate(String image_Url) {
        mView.onUserProfilePhotoUpdate(image_Url);
    }

    @Override
    public void onVisibilityStatusUpdate(boolean visible) {
        mView.onUserVisibilityStatusUpdate(visible);
    }

    @Override
    public void onLoginStatusUpdate(boolean loggedIn, Class<?> classh) {
        mView.onUserLoginStatusUpdate(loggedIn, classh);
    }

    @Override
    public void onDelete(User user) {
        mView.onUserDelete(user);
    }

    @Override
    public void onUser(User user) {
        mView.ongetUser(user);
    }

    @Override
    public void onUserById(User user) {
        mView.ongetUserById(user);
    }

}
