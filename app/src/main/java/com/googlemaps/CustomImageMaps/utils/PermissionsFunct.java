package com.googlemaps.CustomImageMaps.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.googlemaps.CustomImageMaps.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_CAMERA;
import static com.googlemaps.CustomImageMaps.utils.Config.REQUEST_STORAGE;
import static com.googlemaps.CustomImageMaps.utils.Config.SELECT_CAMERA;
import static com.googlemaps.CustomImageMaps.utils.Config.SELECT_PHOTO;

public class PermissionsFunct {

    public static String CameraPermissions(Activity act) {

        Intent cameraImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();

        String imageFileName_N_extens = imageFileName + ".jpg";
        File file = new File(storageDir, imageFileName_N_extens);

        Uri uri = FileProvider.getUriForFile(act, act.getPackageName() + ".provider", file);
        cameraImageIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        act.startActivityForResult(cameraImageIntent, SELECT_CAMERA);

        return imageFileName_N_extens;

    }

    public static void PhotoPermissions(Activity act) {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, act.getString(R.string.choose_image_from));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        act.startActivityForResult(chooserIntent, SELECT_PHOTO);

    }

    public static String CameraPermissionsCheck(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                act.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
            else
                return CameraPermissions(act);
        }
        return null;
    }

    public static void PhotoPermissionsCheck(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                act.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            else
                PhotoPermissions(act);
        }
    }

}
