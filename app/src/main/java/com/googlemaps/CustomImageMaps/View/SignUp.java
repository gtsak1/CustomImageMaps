package com.googlemaps.CustomImageMaps.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.googlemaps.CustomImageMaps.utils.BottomSheet_Dialog;
import com.googlemaps.CustomImageMaps.utils.Config;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_CAMERA;
import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_STORAGE;
import static com.googlemaps.CustomImageMaps.utils.Config.SELECT_CAMERA;
import static com.googlemaps.CustomImageMaps.utils.Config.SELECT_PHOTO;
import static com.googlemaps.CustomImageMaps.utils.Funct.NoAcception;
import static com.googlemaps.CustomImageMaps.utils.Funct.getFileProviderUri;
import static com.googlemaps.CustomImageMaps.utils.Funct.redWrapper;
import static com.googlemaps.CustomImageMaps.utils.Funct.setImage;
import static com.googlemaps.CustomImageMaps.utils.Funct.setProgressDialog;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.CameraPermissions;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.CameraPermissionsCheck;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.PhotoPermissions;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.PhotoPermissionsCheck;

public class SignUp extends AppCompatActivity implements UserContract.View, BottomSheet_Dialog.BottomSheetListener {

    private static final String TAG = SignUp.class.getSimpleName();

    public ProgressBar mBar;
    public ProgressDialog progressDialog;

    public UserPresenter mPresenter;
    public DatabaseReference mReference;
    public FirebaseAuth mAuth;
    public StorageReference storageReference;

    public RelativeLayout image_relative;
    public BottomSheet_Dialog bottomSheet;
    public CircleImageView profilePic;
    public RelativeLayout profilePicAdd;

    String imageFileName_N_extens;
    Uri profileImageUri;
    TextView remove_photo;

    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mBar = findViewById(R.id.progressBar);
        setProgressDialog(this, progressDialog = new ProgressDialog(this), getString(R.string.image_uploading));

        mAuth = FirebaseAuth.getInstance();
        mPresenter = new UserPresenter(this);
        mReference = FirebaseDatabase.getInstance().getReference().child(Config.USER_NODE);
        storageReference = FirebaseStorage.getInstance().getReference().child(Config.USER_NODE);

        image_relative = findViewById(R.id.image_relative);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            image_relative.setVisibility(View.GONE);
        }

        profilePic = findViewById(R.id.profilePic);
        profilePicAdd = findViewById(R.id.profilePicAdd);

        profilePic.setOnClickListener(view -> {
            bottomSheet = new BottomSheet_Dialog();
            bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
        });

        profilePicAdd.setOnClickListener(view -> {
            bottomSheet = new BottomSheet_Dialog();
            bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
        });

        EditText mEmail = findViewById(R.id.email);
        EditText mPassword = findViewById(R.id.password);
        EditText PasswordConfirm = findViewById(R.id.confirm_password);
        EditText fName = findViewById(R.id.fName);
        EditText LName = findViewById(R.id.LName);
        EditText phone_number = findViewById(R.id.phonnum);

        remove_photo = findViewById(R.id.remove_photo);
        remove_photo.setOnClickListener(view -> {
            setImage(this, profilePic, R.mipmap.avatardefault_92824);
            remove_photo.setVisibility(View.GONE);
            profileImageUri = null;
        });

        Button btnSignUp = findViewById(R.id.email_sign_in_button);

        TextView sign_in_link = findViewById(R.id.sign_in_link);
        sign_in_link.setOnClickListener(view -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
            }
            else {
                super.finish();
            }
            startActivity(new Intent(this, LoginActivity.class));
        });



        btnSignUp.setOnClickListener(view -> {
            String email, pass, pass_confirm, first_Name, last_Name, phone;
            email = mEmail.getText().toString();
            phone = phone_number.getText().toString();
            pass = mPassword.getText().toString(); pass_confirm = PasswordConfirm.getText().toString();
            first_Name = fName.getText().toString(); last_Name = LName.getText().toString();

            redWrapper(this, mEmail); redWrapper(this, mPassword); redWrapper(this, PasswordConfirm);
            redWrapper(this, fName); redWrapper(this, LName);



            if (email.isEmpty() || pass.isEmpty() || pass_confirm.isEmpty() || first_Name.isEmpty()
                    || last_Name.isEmpty()) {
                if (email.isEmpty())
                    mEmail.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                if (pass.isEmpty())
                    mPassword.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                if (pass_confirm.isEmpty())
                    PasswordConfirm.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                if (first_Name.isEmpty())
                    fName.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                if (last_Name.isEmpty())
                    LName.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                Toasty.error(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmail.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                Toasty.error(this, getString(R.string.mail_not_valid), Toast.LENGTH_SHORT).show();
            }
            else if(pass.length() < 6) {
                mPassword.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                Toasty.error(this, getString(R.string.password_6_chars), Toast.LENGTH_SHORT).show();
            }
            else if(!pass.equals(pass_confirm)) {
                mPassword.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                PasswordConfirm.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                Toasty.error(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show();
            }
            else {
                Random random = new Random();
                int markerColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

                User user = new User(email, pass, first_Name, last_Name, phone, profileImageUri, markerColor);
                mPresenter.createNewUser(this, mReference, user, mAuth, storageReference);

            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            image_relative.setVisibility(View.GONE);
        else
            image_relative.setVisibility(View.VISIBLE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequest");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                PhotoPermissions(this);
            }
            else
                NoAcception(this, false);
        }

        else if (requestCode == REQUEST_CAMERA){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                imageFileName_N_extens = CameraPermissions(this); //fileName and extension of the captured image
            }
            else
                NoAcception(this, false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){

            try {
                profileImageUri = data.getData();
                setImage(this, profilePic, profileImageUri);
                remove_photo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else if (requestCode == SELECT_CAMERA && resultCode == RESULT_OK) {

            try {
                Log.e(TAG, "imageFileName_N_extens: " + imageFileName_N_extens);
                Uri uri = getFileProviderUri(this, imageFileName_N_extens);

                Log.e(TAG, "uri: " + uri);
                Log.e(TAG, "url: " + uri.toString());

                profileImageUri = uri;
                setImage(this, profilePic, profileImageUri);
                remove_photo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    String successMessage; Class<?> intentClass;
    @Override
    public void onUserSuccess(String message, Class<?> classh) {
        successMessage = message;
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
    public void onProcessUploadStart() {
        progressDialog.show();
    }


    @Override
    public void onProcessEnd() {
        mBar.setVisibility(View.GONE);
    }

    @Override
    public void onProcessUploadEnd() {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
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
        Toasty.success(this, successMessage,
                Toast.LENGTH_SHORT).show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
        Intent myIntent;
        myIntent = new Intent(this, intentClass);
        myIntent.putExtra("user", user);
        startActivity(myIntent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void ongetUserById(User user) {

    }

    @Override
    public void onBottomSheetListenerButtonClicked(String text) {
        if (text.equals("take_photo"))
            imageFileName_N_extens = CameraPermissionsCheck(this);
        else if (text.equals("choose_photo"))
            PhotoPermissionsCheck(this);
    }
}


