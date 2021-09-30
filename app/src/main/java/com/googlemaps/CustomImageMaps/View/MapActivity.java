package com.googlemaps.CustomImageMaps.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.googlemaps.CustomImageMaps.Adapters.UsersAdapter;
import com.googlemaps.CustomImageMaps.Core.UserContract;
import com.googlemaps.CustomImageMaps.Core.UserPresenter;
import com.googlemaps.CustomImageMaps.Model.User;
import com.googlemaps.CustomImageMaps.R;
import com.googlemaps.CustomImageMaps.Receiver.LocationBroadcastReceiver;
import com.googlemaps.CustomImageMaps.Services.LocationService;
import com.googlemaps.CustomImageMaps.utils.BottomSheet_Dialog;
import com.googlemaps.CustomImageMaps.utils.Config;
import com.googlemaps.CustomImageMaps.utils.Funct;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.googlemaps.CustomImageMaps.utils.Config.ERROR_DIALOG_REQUEST;
import static com.googlemaps.CustomImageMaps.utils.Config.MAPVIEW_BUNDLE_KEY;
import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_CAMERA;
import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_CODE_MAP;
import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_LOCATION;
import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_STORAGE;
import static com.googlemaps.CustomImageMaps.utils.Config.SELECT_CAMERA;
import static com.googlemaps.CustomImageMaps.utils.Config.SELECT_PHOTO;
import static com.googlemaps.CustomImageMaps.utils.Funct.NoAcception;
import static com.googlemaps.CustomImageMaps.utils.Funct.SpanTextView;
import static com.googlemaps.CustomImageMaps.utils.Funct.ZoomBounds;
import static com.googlemaps.CustomImageMaps.utils.Funct.call;
import static com.googlemaps.CustomImageMaps.utils.Funct.changeCompassPosition;
import static com.googlemaps.CustomImageMaps.utils.Funct.createMarkerUserName;
import static com.googlemaps.CustomImageMaps.utils.Funct.getAddress;
import static com.googlemaps.CustomImageMaps.utils.Funct.getFileProviderUri;
import static com.googlemaps.CustomImageMaps.utils.Funct.isNetworkConnected;
import static com.googlemaps.CustomImageMaps.utils.Funct.isServicesOK;
import static com.googlemaps.CustomImageMaps.utils.Funct.redWrapper;
import static com.googlemaps.CustomImageMaps.utils.Funct.sendMail;
import static com.googlemaps.CustomImageMaps.utils.Funct.setImage;
import static com.googlemaps.CustomImageMaps.utils.Funct.setProgressDialog;
import static com.googlemaps.CustomImageMaps.utils.Funct.transformMarker;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.CameraPermissions;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.CameraPermissionsCheck;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.PhotoPermissions;
import static com.googlemaps.CustomImageMaps.utils.PermissionsFunct.PhotoPermissionsCheck;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationBroadcastReceiver.LocationBroadcastReceiverListener, BottomSheet_Dialog.BottomSheetListener,
        UserContract.View, UsersAdapter.onUserListener {

    private static final String TAG = "Map_screen";

    GoogleApiClient mGoogleApiClient;
    GoogleMap mGoogleMap;
    ArrayList<Marker> marker_arraylist;
    ArrayList<Marker> marker_arraylist_names;
    ArrayList<User> mUserList;

    private MapView mMapView;

    RelativeLayout sat;
    ConstraintLayout con2;
    RelativeLayout listes;
    ImageView fullscreenIm, fullscreenIm0; boolean isFullScreen;

    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    UsersAdapter mAdapter;

    public ProgressBar mBar;
    public ProgressDialog progressDialog;
    public FirebaseAuth mAuth;
    public StorageReference storageReference;
    public UserPresenter mPresenter;
    public DatabaseReference mReference;

    LocationBroadcastReceiver receiver;

    BottomSheet_Dialog bottomSheet;
    String imageFileName_N_extens;

    User user;
    MenuItem visibility_icon_pressed;
    SharedPreferences.Editor editor_SignOut;

    Uri profileImageUri;

    boolean justSignedUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mAuth = FirebaseAuth.getInstance();
        mPresenter = new UserPresenter(this);
        mReference = FirebaseDatabase.getInstance().getReference().child(Config.USER_NODE);
        storageReference = FirebaseStorage.getInstance().getReference().child(Config.USER_NODE);

        user = (User) getIntent().getSerializableExtra("user");

        if (getIntent().getExtras() != null)
            justSignedUp = getIntent().getExtras().getBoolean("sign_up");

        if (user == null)
            mPresenter.getUser(mReference, mAuth);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPrefSignOut = getSharedPreferences("signed_out", MODE_PRIVATE);
        editor_SignOut = sharedPrefSignOut.edit();
        editor_SignOut.putBoolean("signed_out", false);
        editor_SignOut.apply();

        receiver = new LocationBroadcastReceiver(this);
        recyclerView = findViewById(R.id.users_recy);

        mBar = findViewById(R.id.progressBar);
        setProgressDialog(this, progressDialog = new ProgressDialog(this), getString(R.string.image_uploading));

        mPresenter.readUsers(mReference);

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        sat = findViewById(R.id.sat);

        fullscreenIm0 = findViewById(R.id.map_fullscreen0);
        fullscreenIm = findViewById(R.id.map_fullscreen);
        listes = findViewById(R.id.rcv);
        con2 = findViewById(R.id.map_screen_constraint2o);

        ConfigChange();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        fullscreenIm0.setOnClickListener(view -> changeScreenProportion(listes, con2));
        fullscreenIm.setOnClickListener(view -> changeScreenProportion(con2, listes));

        Funct.SwipeRefresh(this, null);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        changeCompassPosition(mMapView, this);

        try {
            if (justSignedUp)
                new Handler().postDelayed(() -> mGoogleMap.moveCamera(Objects.requireNonNull(ZoomBounds(this, mUserList, 80))), 4500);
            else
                mGoogleMap.moveCamera(Objects.requireNonNull(ZoomBounds(this, mUserList, 80)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        marker_arraylist = new ArrayList<>(); marker_arraylist_names = new ArrayList<>();
        if (mUserList != null) {
            for (int i = 0; i < mUserList.size(); i++) {
                createMarkers(mUserList.get(i));
            }
        }

        sat.setOnClickListener(view -> {
            //Creating the instance of PopupMenu for google map layers
            PopupMenu popup = new PopupMenu(this, sat);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.earth_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.item1:
                        if (mGoogleMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL) {
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            changeMarkerTextColor();
                        }

                        return true;
                    case R.id.item2:
                        if (mGoogleMap.getMapType() != GoogleMap.MAP_TYPE_HYBRID) {
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            changeMarkerTextColor();
                            if (!isNetworkConnected(this))
                                Toast.makeText(this, R.string.epipedo_doryforos, Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    case R.id.item3:
                        if (mGoogleMap.getMapType() != GoogleMap.MAP_TYPE_TERRAIN) {
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            changeMarkerTextColor();
                            if (!isNetworkConnected(this))
                                Toast.makeText(this, R.string.epipedo_edafos, Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    default:
                        return true;
                }

            });
            popup.show();
        });

        mGoogleMap.setOnInfoWindowClickListener(marker -> {
            int position = -1;
            for (int i=0; i<mUserList.size(); i++)
                if (mUserList.get(i).getsignUpDateMillis() == (long) marker.getTag()) {
                    position = i;
                    break;
                }
            DetailDialog(position);
        });

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTitle() != null) {
                    View v;

                    v = getLayoutInflater().inflate(R.layout.infowindow_layout, null);

                    TextView tv0 = v.findViewById(R.id.tv0_map_infoW);
                    TextView tv1 = v.findViewById(R.id.tv1_map_infoW);

                    tv0.setText(marker.getTitle());

                    if (marker.getSnippet() != null)
                        tv1.setText(marker.getSnippet());
                    else
                        tv1.setVisibility(View.GONE);

                    return v;
                }
                return null;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        //visibility icon
        menu.getItem(0).setIcon(user.isVisible()? R.drawable.ic_baseline_visibility_24 : R.drawable.ic_baseline_visibility_off_24);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_visible:
                boolean isVisible;
                if (!pressed)
                    isVisible = user.isVisible();
                else
                    isVisible = visible_meta;

                if (isVisible)
                    ConfirmDialog(0, getString(R.string.user_visibility), getString(R.string.user_visibility_alert));
                else
                    mPresenter.UpdateUserVisibilityStatus(this, mReference, user.getId(), true);
                visibility_icon_pressed = item;
                return true;

            case R.id.menu_edit_profile:
                UpdateUser(-1);
                return true;

            case R.id.menu_delete_account:
                ConfirmDialog(1, getString(R.string.delete_account), getString(R.string.delete_account_alert));
                return true;

            case R.id.menu_log_out:
                mPresenter.UpdateUserLoginStatus(this, mReference, user.getId(), false, mAuth);
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    public void changeScreenProportion(View view, View view2) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();

        params.height = isFullScreen ? 0 : ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = isFullScreen ? 0 : ViewGroup.LayoutParams.MATCH_PARENT;
        view2.setVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        isFullScreen = !isFullScreen;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        if (mMapView != null)
            mMapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null)
            mMapView.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("ACT_LOC"));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null)
            mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null)
            mMapView.onStop();
    }

    @Override
    public void onPause() {
        try {
            if (mMapView != null)
                mMapView.onPause();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            super.onPause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (mMapView != null)
                mMapView.onDestroy();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            super.onDestroy();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null)
            mMapView.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ConfigChange();

    }
    private void ConfigChange() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) con2.getLayoutParams();
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) listes.getLayoutParams();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.startToEnd = R.id.guide_vert;
            params.bottomToBottom =R.id.map_screen_constraint;
            params.topToTop = R.id.map_screen_constraint;
            params.endToEnd = R.id.map_screen_constraint;
            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            params.topToBottom = ConstraintLayout.LayoutParams.UNSET;
            con2.setLayoutParams(params);

            params2.startToStart = R.id.map_screen_constraint;
            params2.bottomToBottom =R.id.map_screen_constraint;
            params2.topToTop = R.id.map_screen_constraint;
            params2.endToEnd = R.id.guide_vert;
            params2.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            listes.setLayoutParams(params2);
        }
        else {
            params.startToStart = R.id.map_screen_constraint;
            params.endToEnd =R.id.map_screen_constraint;
            params.topToBottom = R.id.guide;
            params.bottomToBottom = R.id.map_screen_constraint;
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET;
            params.topToTop = ConstraintLayout.LayoutParams.UNSET;
            con2.setLayoutParams(params);

            params2.startToStart = R.id.map_screen_constraint;
            params2.endToEnd =R.id.map_screen_constraint;
            params2.topToTop = R.id.map_screen_constraint;
            params2.bottomToTop = R.id.guide;
            params2.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
            listes.setLayoutParams(params2);
        }
    }

    void startLocService() {
        IntentFilter filter = new IntentFilter("ACT_LOC");
        registerReceiver(receiver, filter);
        Intent intentSrv = new Intent(this, LocationService.class);
        startService(intentSrv);
    }

    private void ConfirmDialog(int action, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AlertDialog dial = builder.setMessage(message)
                .setCancelable(true)
                .setTitle(title)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    if (action == 0)
                        mPresenter.UpdateUserVisibilityStatus(this, mReference, user.getId(), false);
                    else if (action == 1) {
                        try {
                            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        mPresenter.DeleteUser(this, mReference, user, storageReference, mAuth);
                    }
                })
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.dismiss()).create();

        dial.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequest");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_MAP) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else
                NoAcception(this);
        }

        else if (requestCode == REQUEST_STORAGE){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                PhotoPermissions(this);
            }
            else
                NoAcception(this);
        }

        else if (requestCode == REQUEST_CAMERA){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                imageFileName_N_extens = CameraPermissions(this); //fileName and extension of the captured image
            }
            else
                NoAcception(this);
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_MAP);
                Log.e(TAG, "Request not granted");
            } else {
                Log.d(TAG, "Request granted, enable loc button");
                startLocService();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult()", Integer.toString(resultCode));

        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    if (isServicesOK(TAG, this, ERROR_DIALOG_REQUEST)) {
                        Log.d(TAG, "Request onActivityResult");
                        requestLocationPermission();
                    }
                    // All required changes were successfully made
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    NoAcception(this);
                    // The user was asked to change settings, but chose not to
                    break;
                }
                default: {
                    break;
                }
            }
        }

        else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest;
        PendingResult<LocationSettingsResult> result;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    //...

                    try {
                        if (isServicesOK(TAG, this, ERROR_DIALOG_REQUEST)) {
                            Log.d(TAG, "Request onConnected");
                            requestLocationPermission();
                        }
                    }catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        Log.d(TAG, "Request onConnected dialog");
                        status.startResolutionForResult(
                                this,
                                REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    //...
                    break;
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onReceiveCoordinates(double latitude, double longitude) {
        Log.d("coord", latitude + ", " + longitude);
        String address = getAddress(this, latitude, longitude);
        mPresenter.UpdateUserLocation(this, mReference, user.getId(), new LatLng(latitude, longitude), address);
    }

    @Override
    public void onBottomSheetListenerButtonClicked(String text) {
        if (text.equals("take_photo"))
            imageFileName_N_extens = CameraPermissionsCheck(this);
        else if (text.equals("choose_photo"))
            PhotoPermissionsCheck(this);
    }

    CircleImageView profilePic; TextView remove_photo; boolean remove_photo_clicked;
    EditText firstname, lastname, phone; View alertDialogView;
    private void UpdateUser(int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        alertDialogView = layoutInflaterAndroid.inflate(R.layout.edit_profile_dialog, null);

        profilePic = alertDialogView.findViewById(R.id.profilePic);
        RelativeLayout profilePicAdd = alertDialogView.findViewById(R.id.profilePicAdd);

        firstname = alertDialogView.findViewById(R.id.first_name); lastname = alertDialogView.findViewById(R.id.last_name); phone = alertDialogView.findViewById(R.id.phon_n);
        remove_photo = alertDialogView.findViewById(R.id.remove_photo);

        remove_photo.setOnClickListener(view2 -> {
            setImage(this, profilePic, R.mipmap.avatardefault_92824);
            remove_photo.setVisibility(View.GONE);
            profileImageUri = null;
            remove_photo_clicked = true;
        });

        profilePic.setOnClickListener(view2 -> {
            bottomSheet = new BottomSheet_Dialog();
            bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
        });

        profilePicAdd.setOnClickListener(view2 -> {
            bottomSheet = new BottomSheet_Dialog();
            bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
        });

        if (position == -1)
            mPresenter.getUserById(mReference, user.getId());
        else
            ShowUpdateDialog(mUserList.get(position));
    }

    private void ShowUpdateDialog(User user) {
        String image_url, fname, lname, phonenum;

        image_url = user.getImage_Url();
        fname = user.getfName();
        lname = user.getlName();
        phonenum = user.getPhone();

        if (image_url != null)
            remove_photo.setVisibility(View.VISIBLE);

        setImage(this, profilePic, image_url);
        firstname.setText(fname);
        lastname.setText(lname);
        phone.setText(phonenum);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertDialogView);

        //Setting message manually and performing action on button click
        AlertDialog dial = builder
                .setPositiveButton(R.string.edit, null)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setTitle(R.string.edit_profile)
                .setCancelable(false).create();

        dial.setOnShowListener(dialogInterface -> {
            dial.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View.OnClickListener) view3 -> {
                String name = firstname.getText().toString(), surname = lastname.getText().toString(), phon = phone.getText().toString();

                redWrapper(this, firstname);
                redWrapper(this, lastname);

                if (name.isEmpty() || surname.isEmpty()) {
                    if (name.isEmpty())
                        firstname.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    if (surname.isEmpty())
                        lastname.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    Toasty.error(MapActivity.this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
                }

                else {
                    if (!name.equals(fname) || !surname.equals(lname) || !phon.equals(phonenum) || profileImageUri != null || remove_photo_clicked) {
                        User user2 = new User(user.getId(), name, surname, phon, profileImageUri);
                        mPresenter.UpdateUser(this, mReference, user2, storageReference);
                    }
                    dial.dismiss();
                }

            });
        });

        dial.show();
    }

    private void DetailDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater2 = getLayoutInflater();
        View alertLayout = inflater2.inflate(R.layout.detail_alert, null);

        TextView fullname = alertLayout.findViewById(R.id.fullname);
        TextView phone = alertLayout.findViewById(R.id.phonenum);
        TextView mail = alertLayout.findViewById(R.id.email);
        TextView latitude = alertLayout.findViewById(R.id.lati);
        TextView longitude = alertLayout.findViewById(R.id.longi);
        TextView address = alertLayout.findViewById(R.id.address);

        fullname.setText(mUserList.get(position).getfName() + " " + mUserList.get(position).getlName());

        if (mUserList.get(position).getPhone() == null || mUserList.get(position).getPhone().equals(""))
            phone.setVisibility(View.GONE);
        else
            phone.setText(SpanTextView(getString(R.string.phone_number) + ": ", mUserList.get(position).getPhone(),
                    ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK));

        mail.setText(SpanTextView(getString(R.string.prompt_email) + ": ", mUserList.get(position).getEmail(),
                ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK));

        latitude.setText(SpanTextView(getString(R.string.latitude) + ": ", String.valueOf(mUserList.get(position).getLatitude()),
                ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK));

        longitude.setText(SpanTextView(getString(R.string.longitude) + ": ", String.valueOf(mUserList.get(position).getLongitude()),
                ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK));

        if (mUserList.get(position).getAddress().equals(""))
            address.setVisibility(View.GONE);
        else
            address.setText(SpanTextView(getString(R.string.address) + ": ", mUserList.get(position).getAddress(),
                    ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK));

        ImageView im1 = alertLayout.findViewById(R.id.gmaps_icon_direct); ImageView im2 = alertLayout.findViewById(R.id.gmaps_icon);

        im1.setOnClickListener(v -> {
            String url = "http://maps.google.com/maps?daddr=" +
                    mUserList.get(position).getLatitude() + "," + mUserList.get(position).getLongitude() + "";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        });

        im2.setOnClickListener(v -> {
            String uri = "http://maps.google.com/maps?q=loc:" + mUserList.get(position).getLatitude() + "," + mUserList.get(position).getLongitude()
                    + " (" + mUserList.get(position).getfName() + " " + mUserList.get(position).getlName() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        });


        //Setting message manually and performing action on button click
        final AlertDialog dial = builder
                .setView(alertLayout)
                .setPositiveButton("Ok", (dialog, id) -> dialog.dismiss()).create();


        dial.show();
    }

    private void GlideForMarker(User user, boolean shouldUpdate, int updatePosition) {
        try {
            Object image_url;
            String noImageText;
            if (user.getImage_Url() != null) {
                image_url = user.getImage_Url();
                noImageText = null;
            } else {
                image_url = BitmapFactory.decodeResource(getResources(), R.mipmap.light_blue2);
                noImageText = user.getlName().substring(0, 1);
            }
            Glide.with(this)
                    .asBitmap()
                    .load(image_url)
                    .apply(new RequestOptions().override(187, 187))
                    .circleCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap bmp = transformMarker(resource, noImageText, user.getMarkerRandomColor());
                            if (!shouldUpdate) {//create and add marker
                                Marker m = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(user.getLatitude(), user.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                        .title(user.getlName() + " " + user.getfName())
                                        .visible(user.isVisible() && user.isLoggedIn())
                                        .anchor(0.47f, 1f)
                                        .infoWindowAnchor(0.47f, 0.092f));
                                m.setTag(user.getsignUpDateMillis()); //setting a unique tag for each marker, so that we are sure we don't choose wrong marker in any calculations made
                                setMarkerSnippet(m, user);
                                marker_arraylist.add(m);
                            }

                            else {//update marker
                                marker_arraylist.get(updatePosition).setPosition(new LatLng(user.getLatitude(), user.getLongitude()));
                                marker_arraylist.get(updatePosition).setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
                                marker_arraylist.get(updatePosition).setTitle(user.getlName() + " " + user.getfName());
                                setMarkerSnippet(marker_arraylist.get(updatePosition), user);

                                //update visibility
                                marker_arraylist.get(updatePosition).setVisible(user.isVisible() && user.isLoggedIn());
                            }


                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMarkers(User user) {
        try {
            GlideForMarker(user, false, -1);//create icon etc


            marker_arraylist_names.add(mGoogleMap.addMarker(new MarkerOptions()//create names
                    .position(new LatLng(user.getLatitude(), user.getLongitude()))
                    .anchor(0.5f, 0.15f)
                    .visible(user.isVisible() && user.isLoggedIn())
                    .icon(createMarkerUserName(this, user.getlName() + " " + user.getfName().substring(0, 1) + ".",
                            mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeMarkerTextColor() {
        if (mUserList != null && marker_arraylist_names != null) {
            for (int i = 0; i < mUserList.size(); i++) {
                marker_arraylist_names.get(i).setIcon(createMarkerUserName(this, mUserList.get(i).getlName() + " " + mUserList.get(i).getfName().substring(0, 1) + ".",
                        mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID));
            }
        }
    }

    private void setMarkerSnippet(Marker m, User user) {
        if ((user.getPhone() == null || user.getPhone().equals("")) && !user.getAddress().equals(""))
            m.setSnippet(getString(R.string.address) + ": " + user.getAddress());
        else if (!(user.getPhone() == null || user.getPhone().equals("")) && user.getAddress().equals(""))
            m.setSnippet(getString(R.string.phone) + ": " + user.getPhone());
        else if(!(user.getPhone() == null || user.getPhone().equals("")) && !user.getAddress().equals(""))
            m.setSnippet(getString(R.string.phone) + ": " + user.getPhone() + "\n" + getString(R.string.address) + ": " + user.getAddress());
    }

    private void UpdateOrRemoveMarker(User user, boolean shouldUpdate) {
        try {
            for (int i=0; i<marker_arraylist.size(); i++)
                if (user.getsignUpDateMillis() == (long) marker_arraylist.get(i).getTag()) {
                    if (shouldUpdate) {//update marker
                        GlideForMarker(user, true, i);//update icon etc

                        //update names
                        marker_arraylist_names.get(i).setPosition(new LatLng(user.getLatitude(), user.getLongitude()));
                        marker_arraylist_names.get(i).setIcon(createMarkerUserName(this, user.getlName() + " " + user.getfName().substring(0, 1) + ".",
                                        mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID));

                        //update visibility
                        marker_arraylist_names.get(i).setVisible(user.isVisible() && user.isLoggedIn());
                    }
                    else {//remove marker
                        marker_arraylist.get(i).remove();
                        marker_arraylist_names.get(i).remove();
                    }
                    break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ZoomInUser(int position) {
        long millis = mUserList.get(position).getsignUpDateMillis();
        double latitude = mUserList.get(position).getLatitude();
        double longitude = mUserList.get(position).getLongitude();

        for (int j=0; j<marker_arraylist.size(); j++){
            if ((long) marker_arraylist.get(j).getTag() == millis) {
                if (marker_arraylist.get(j).isVisible()) {
                    marker_arraylist.get(j).showInfoWindow();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude)
                            , 16.5f), 1500, null);

                    //show the map in full screen
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) con2.getLayoutParams();
                    ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) listes.getLayoutParams();
                    params2.height = 0;
                    params2.width = 0;
                    listes.setVisibility(View.GONE);
                    con2.setVisibility(View.VISIBLE);
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    isFullScreen = true;
                }
                else {
                    Toast.makeText(this, R.string.user_not_loggedIn, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public int getIndex(User user){
        int index = 0;

        for (User count_user: mUserList){
            if (count_user.getId().trim().equals(user.getId())){
                break;
            }
            index++;
        }

        return index;
    }

    @Override
    public void onUserProfileUpdateClick(int position) {
        UpdateUser(position);
    }

    @Override
    public void onUserDirectionsClick(int position) {
        String url = "http://maps.google.com/maps?daddr=" +
                mUserList.get(position).getLatitude() + "," + mUserList.get(position).getLongitude() + "";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    @Override
    public void onUserSendClick(int position) {
        /*This will be the actual content we will share.*/
        String phone = mUserList.get(position).getPhone();
        if (mUserList.get(position).getPhone() == null)
            phone = "";
        String shareBody = mUserList.get(position).getfName() + " " + mUserList.get(position).getlName()
                + "\n\n" + getString(R.string.phone_number) + ": " + phone
                + "\n" + getString(R.string.prompt_email) + ": " + mUserList.get(position).getEmail()
                + "\n\n" + getString(R.string.latitude) + ": " + mUserList.get(position).getLatitude()
                + "\n" + getString(R.string.longitude) + ": " + mUserList.get(position).getLongitude()
                + "\n" + getString(R.string.address) + ": " + mUserList.get(position).getAddress();

        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);

        /*The type of the content is text*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.user_details_from_app));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, getString(R.string.send_user_details)));
    }

    @Override
    public void onUserContactClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater2 = getLayoutInflater();
        View alertLayout = inflater2.inflate(R.layout.contact_user, null);

        LinearLayout mlin = alertLayout.findViewById(R.id.mail_lin); LinearLayout phlin = alertLayout.findViewById(R.id.phone_lin1);
        TextView pht1 = alertLayout.findViewById(R.id.phone_text1);

        mlin.setOnClickListener(view -> sendMail(MapActivity.this, mUserList.get(position).getEmail()));

        if (mUserList.get(position).getPhone() == null || mUserList.get(position).getPhone().equals(""))
            phlin.setVisibility(View.GONE);
        else {
            pht1.setText(mUserList.get(position).getPhone());
            phlin.setOnClickListener(view -> call(MapActivity.this, mUserList.get(position).getPhone()));
        }


        final AlertDialog dial = builder
                .setView(alertLayout)
                .setTitle(getString(R.string.contact_with_user))
                .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.dismiss()).create();

        dial.show();
    }

    @Override
    public void onUserDetailsClick(int position) {
        DetailDialog(position);
    }

    @Override
    public void onUserZoomInClick(int position) {
        ZoomInUser(position);
    }

    @Override
    public void onUserZoomOutClick(int position) {
        try {
            mGoogleMap.animateCamera(Objects.requireNonNull(ZoomBounds(this, mUserList, 82)), 1500, null);
            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) listes.getLayoutParams();
            if (params2.height == ViewGroup.LayoutParams.MATCH_PARENT && params2.width == ViewGroup.LayoutParams.MATCH_PARENT) //if the map is not shown, show it in half screen
                changeScreenProportion(listes, con2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserSuccess(String message, Class<?> classh) {

    }

    @Override
    public void onUserDeleteSuccess(String message, Class<?> classh) {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Toasty.success(this, message,
                Toast.LENGTH_SHORT).show();
        editor_SignOut.putBoolean("signed_out", true);
        editor_SignOut.apply();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
    }

    @Override
    public void onUserFailure(String message) {
        Toast.makeText(this, message,
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
        progressDialog.show();
    }

    @Override
    public void onProcessUploadEnd() {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onUserRead(ArrayList<User> users) {
        mUserList = users;

        if (mGoogleMap != null)
            createMarkers(mUserList.get(mUserList.size() - 1));

        mAdapter = new UsersAdapter(this, users, this, user);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);//prevent recyclerview items from blinking on constant location updates
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onUserUpdate(User user) {
        try {
            int index = getIndex(user);
            mUserList.set(index, user);
            mAdapter.notifyItemChanged(index);
            UpdateOrRemoveMarker(user, true);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            Log.e(TAG, "onUserUpdate: NullPointerException");
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.e(TAG, "onUserUpdate: IndexOutOfBoundsException");
        }
    }

    @Override
    public void onUserDelete(User user) {
        try {
            int index = getIndex(user);
            mUserList.remove(index);
            mAdapter.notifyItemRemoved(index);
            UpdateOrRemoveMarker(user, false);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            Log.e(TAG, "onUserDelete: NullPointerException");
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.e(TAG, "onUserDelete: IndexOutOfBoundsException");
        }
    }

    @Override
    public void onUserProfilePhotoUpdate(String image_Url) {

    }

    boolean pressed, visible_meta;
    @Override
    public void onUserVisibilityStatusUpdate(boolean visible) {
        pressed = true;
        visible_meta = visible;
        visibility_icon_pressed.setIcon(visible ? R.drawable.ic_baseline_visibility_24 : R.drawable.ic_baseline_visibility_off_24);
    }

    @Override
    public void onUserLoginStatusUpdate(boolean loggedIn, Class<?> classh) {
        if (!loggedIn) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            editor_SignOut.putBoolean("signed_out", true);
            editor_SignOut.apply();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
            }
            else {
                super.finish();
            }
        }
    }

    @Override
    public void ongetUser(User user) {
        this.user = user;
    }

    @Override
    public void ongetUserById(User user) {
        ShowUpdateDialog(user);
    }
}