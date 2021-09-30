package com.googlemaps.CustomImageMaps.Model;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    public String id;
    public String email;
    public String password;
    public String fName;
    public String lName;
    public String phone;
    public long signUpDateMillis;
    public String image_Url;
    public Uri image_Uri;
    public boolean visible;
    public boolean isLoggedIn;

    public long getsignUpDateMillis() {
        return signUpDateMillis;
    }

    public void setsignUpDateMillis(long signUpDateMillis) {
        this.signUpDateMillis = signUpDateMillis;
    }

    public int markerRandomColor;
    public double latitude;
    public double longitude;
    public String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public int getMarkerRandomColor() {
        return markerRandomColor;
    }

    public void setMarkerRandomColor(int markerRandomColor) {
        this.markerRandomColor = markerRandomColor;
    }

    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String id, String fName, String lName, String phone, Uri image_Uri) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.image_Uri = image_Uri;
    }

    public User(String id, String email, String fName, String lName, String phone, long signUpDateMillis, String image_Url, double latitude, double longitude, String address,
                boolean visible, boolean isLoggedIn, int markerRandomColor) {
        this.id = id;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.signUpDateMillis = signUpDateMillis;
        this.image_Url = image_Url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.visible = visible;
        this.isLoggedIn = isLoggedIn;
        this.markerRandomColor = markerRandomColor;
    }

    public User(String email, String password, String fName, String lName, String phone, Uri image_Uri, int markerRandomColor) {
        this.email = email;
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.image_Uri = image_Uri;
        this.markerRandomColor = markerRandomColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage_Url() {
        return image_Url;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    public Uri getImage_Uri() {
        return image_Uri;
    }

    public void setImage_Uri(Uri image_Uri) {
        this.image_Uri = image_Uri;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
