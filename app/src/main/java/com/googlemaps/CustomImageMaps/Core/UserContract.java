package com.googlemaps.CustomImageMaps.Core;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.googlemaps.CustomImageMaps.Model.User;

import java.util.ArrayList;

public interface UserContract {
    interface View{
        void onUserSuccess(String message, Class<?> classh);
        void onUserDeleteSuccess(String message, Class<?> classh);
        void onUserFailure(String message);
        void onProcessStart();
        void onProcessEnd();
        void onProcessUploadStart();
        void onProcessUploadEnd();
        void onUserRead(ArrayList<User> users);
        void onUserUpdate(User user);
        void onUserProfilePhotoUpdate(String image_Url);
        void onUserVisibilityStatusUpdate(boolean visible);
        void onUserLoginStatusUpdate(boolean loggedIn, Class<?> classh);
        void onUserDelete(User user);

        void ongetUser(User user);
        void ongetUserById(User user);
    }

    interface Presenter{
        void SignInUser(Context context, User user, FirebaseAuth mAuth);
        void createNewUser(Context context, DatabaseReference reference, User user, FirebaseAuth mAuth, StorageReference storageReference);
        void readUsers(DatabaseReference reference);
        void UpdateUser(Context context, DatabaseReference reference, User user, StorageReference storageReference);
        void UpdateUserProfilePhoto(Context context, DatabaseReference reference, User user, StorageReference storageReference);
        void UpdateUserLoginStatus(Context context, DatabaseReference reference, String user_id, boolean loggedIn, FirebaseAuth mAuth);
        void UpdateUserVisibilityStatus(Context context, DatabaseReference reference, String user_id, boolean visible);
        void UpdateUserLocation(Context context, DatabaseReference reference, String user_id, LatLng location, String address);
        void DeleteUser(Context context, DatabaseReference reference, User user, StorageReference storageReference, FirebaseAuth mAuth);
        void getUser(DatabaseReference reference, FirebaseAuth auth);
        void getUserById(DatabaseReference reference, String user_id);
    }

    interface Interactor{
        void performSignInUser(Context context, User user, FirebaseAuth mAuth);
        void performCreateUser(Context context, DatabaseReference reference, User user, FirebaseAuth mAuth, StorageReference storageReference);
        void performReadUsers(DatabaseReference reference);
        void performUpdateUser(Context context, DatabaseReference reference, User user, StorageReference storageReference);
        void performUpdateUserProfilePhoto(Context context, DatabaseReference reference, User user, StorageReference storageReference);
        void performUpdateUserLoginStatus(Context context, DatabaseReference reference, String user_id, boolean loggedIn, FirebaseAuth mAuth);
        void performUpdateUserVisibilityStatus(Context context, DatabaseReference reference, String user_id, boolean visible);
        void performUpdateUserLocation(Context context, DatabaseReference reference, String user_id, LatLng location, String address);
        void performDeleteUser(Context context, DatabaseReference reference, User user, StorageReference storageReference, FirebaseAuth mAuth);
        void performgetUser(DatabaseReference reference, FirebaseAuth auth);
        void performgetUserById(DatabaseReference reference, String user_id);
    }

    interface onOperationListener{
        void onSuccess(String message, Class<?> classh);
        void onDeleteSuccess(String message, Class<?> classh);
        void onFailure(String message);
        void onStart();
        void onUploadStart();
        void onEnd();
        void onUploadEnd();
        void onRead(ArrayList<User> users);
        void onUpdate(User user);
        void onProfilePhotoUpdate(String image_Url);
        void onVisibilityStatusUpdate(boolean visible);
        void onLoginStatusUpdate(boolean loggedIn, Class<?> classh);
        void onDelete(User user);
        void onUser(User user);
        void onUserById(User user);
    }
}
