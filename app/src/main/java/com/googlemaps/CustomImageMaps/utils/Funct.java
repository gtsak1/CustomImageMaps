package com.googlemaps.CustomImageMaps.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.googlemaps.CustomImageMaps.Model.User;
import com.googlemaps.CustomImageMaps.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Funct {
    private static final int photoMargin = 30;
    private static final int margin = 20;
    private static final int triangleMargin = 10;

    public static void changeCompassPosition(MapView mapView, Activity act){
        try {
            final ViewGroup parent = (ViewGroup) mapView.findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(() -> {
                try {
                    Resources r = act.getResources();
                    //convert our dp margin into pixels
                    int marginPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
                    int marginPixels2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, r.getDisplayMetrics());
                    // Get the map compass view
                    View mapCompass = parent.getChildAt(4);

                    // create layoutParams, giving it our wanted width and height(important, by default the width is "match parent")
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mapCompass.getWidth(), mapCompass.getHeight());

                    //give compass margin
                    rlp.topMargin = marginPixels;
                    rlp.leftMargin = marginPixels2;
                    mapCompass.setLayoutParams(rlp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static CameraUpdate ZoomBounds(Context cx, ArrayList<User> users, int margin){//zoom google map according to the location of all of its visible markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int width = cx.getResources().getDisplayMetrics().widthPixels;
        int height = cx.getResources().getDisplayMetrics().heightPixels;

        try {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).isLoggedIn() && users.get(i).isVisible())
                    builder.include(new LatLng(users.get(i).getLatitude(), users.get(i).getLongitude()));
            }

            LatLngBounds bounds = builder.build();
            return CameraUpdateFactory.newLatLngBounds(bounds, width, height, margin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getAddress(Context cx, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(cx);
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            String  addr = obj.getAddressLine(0);

            Log.e("Location Address", "Address" + addr);
            address = addr;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Location Address", "IOException");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static BitmapDescriptor createMarkerUserName(Context cx, String text, boolean textColorWhite) {
        LayoutInflater inflater = LayoutInflater.from(cx);
        View markerLayout = inflater.inflate(R.layout.custom_marker_layout, null);

        TextView markerText = markerLayout.findViewById(R.id.marker_text);
        markerText.setText(text);
        if (textColorWhite)
            markerText.setTextColor(Color.WHITE);
        else
            markerText.setTextColor(Color.BLACK);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap transformMarker(Bitmap source, String noImageText, int randomColor) {
        int size = Math.min(source.getWidth(), source.getHeight());
        float r = size / 2f;

        Bitmap output = Bitmap.createBitmap(size + triangleMargin, size + triangleMargin, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(randomColor);
        paintBorder.setStrokeWidth(margin);
        canvas.drawCircle(r, r, r - margin + 2, paintBorder);

        //create & draw the lower triangle shape of the marker
        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setStrokeWidth(2);
        trianglePaint.setColor(randomColor);
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);
        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(size - margin, (float) size / 2);
        triangle.lineTo((float) size / 2, size + triangleMargin);
        triangle.lineTo(margin, (float) size / 2);
        triangle.close();
        canvas.drawPath(triangle, trianglePaint);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(r, r, r - photoMargin, paint);

        if (noImageText != null) {
            // paint defines the text color, stroke width and size
            Paint color = new Paint();
            color.setTextSize(70);
            color.setColor(randomColor);

            // modify canvas
            canvas.drawText(noImageText, r - margin - 2, r + margin + triangleMargin - 2, color);
        }

        return output;
    }

    public static boolean isNetworkConnected(Activity a) {
        ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String getFileExtension(Context cx, Uri mUri){

        ContentResolver cr = cx.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    public static Uri getFileProviderUri(Context cx, String imageFileName_N_extens) {
        //File object of camera image
        File file = new File(Environment.getExternalStorageDirectory(), imageFileName_N_extens);

        //return Uri of camera image
        return FileProvider.getUriForFile(cx, cx.getApplicationContext().getPackageName() + ".provider", file);
    }

    public static void SwipeRefresh(Activity a, View root) {
        SwipeRefreshLayout mSwipeRefreshLayout;
        if (root == null)
            mSwipeRefreshLayout = a.findViewById(R.id.swipeToRefresh);
        else
            mSwipeRefreshLayout = root.findViewById(R.id.swipeToRefresh);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.edittext);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            RefreshActivity(a);
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    public static void RefreshActivity(Activity a) {
        a.overridePendingTransition(0, 0);
        a.finish();
        a.overridePendingTransition(0, 0);
        a.startActivity(a.getIntent());
        a.overridePendingTransition(0, 0);
    }

    public static void NoAcception(Activity act, boolean finishActivity){
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        //Setting message manually and performing action on button click
        final AlertDialog dial = builder.setMessage(R.string.not_completed_message)
                .setCancelable(!finishActivity)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if (finishActivity)
                        act.finish();
                })
                .setTitle(R.string.rights_error)
                .create();

        dial.setOnShowListener(dialog -> dial.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false));

        dial.show();
    }

    public static boolean isServicesOK(String TAG, Activity cx, int ERROR_DIALOG_REQUEST) {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(cx);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(cx, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(cx, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //creates and returns String that combines different color between first and second String argument
    public static SpannableStringBuilder SpanTextView(String tvStr1, String tvStr2, int color1, int color2) {
        SpannableStringBuilder spanbuilder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString(tvStr1);
        str1.setSpan(new ForegroundColorSpan(color1), 0, str1.length(), 0);
        spanbuilder.append(str1);

        SpannableString str2= new SpannableString(tvStr2);
        str2.setSpan(new ForegroundColorSpan(color2), 0, str2.length(), 0);
        spanbuilder.append(str2);

        return spanbuilder;
    }

    public static void redWrapper(Context cx, EditText edT){
        edT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edT.getText().toString().equals(""))
                    edT.setBackgroundTintList(cx.getResources().getColorStateList(R.color.red));
                else
                    edT.setBackgroundTintList(cx.getResources().getColorStateList(R.color.colorPrimary));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void sendMail(Context cx, String mail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mail });

        Intent chooser = Intent.createChooser(emailIntent, cx.getString(R.string.mail_via));
        if (emailIntent.resolveActivity(cx.getPackageManager()) != null) {
            cx.startActivity(chooser);
        }
    }

    public static void call(Context cx, String number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        if (intent.resolveActivity(cx.getPackageManager()) != null) {
            cx.startActivity(intent);
        }
    }

    public static void setImage(Context cx, CircleImageView imageView, Object imageFile) {
        RequestOptions defaultOptions = new RequestOptions()
                .error(R.mipmap.avatardefault_92824);
        Glide.with(cx)
                .setDefaultRequestOptions(defaultOptions)
                .load(imageFile)
                .dontAnimate()  //CircleImageView needs this
                .into(imageView);
    }

    public static void setProgressDialog(Context cx, ProgressDialog progressDialog, String title) {
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(cx.getString(R.string.loading));
    }

}
