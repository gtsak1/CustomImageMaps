package com.googlemaps.CustomImageMaps.Core;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.googlemaps.CustomImageMaps.Model.User;
import com.googlemaps.CustomImageMaps.R;
import com.googlemaps.CustomImageMaps.View.LoginActivity;
import com.googlemaps.CustomImageMaps.View.MapActivity;
import com.googlemaps.CustomImageMaps.utils.Funct;

import java.util.ArrayList;
import java.util.Objects;

public class UserInteractor implements UserContract.Interactor {

    private UserContract.onOperationListener mListener;
    private ArrayList<User> users = new ArrayList<>();

    public UserInteractor(UserContract.onOperationListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void performSignInUser(Context context, User user, FirebaseAuth mAuth) {
        mListener.onStart();

        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        mListener.onSuccess("", MapActivity.class);
                        mListener.onEnd();

                    } else {
                        // If sign in fails, display a message to the user.
                        mListener.onFailure(context.getString(R.string.wrong_combination));
                        mListener.onEnd();
                    }

                });
    }

    @Override
    public void performCreateUser(Context context, DatabaseReference reference, User user, FirebaseAuth mAuth, StorageReference storageReference) {
        mListener.onStart();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information

                        FirebaseUser user_auth = mAuth.getCurrentUser();
                        assert user_auth != null;

                        String id = reference.push().getKey();

                        if (user.getImage_Uri() != null) {
                            final StorageReference fileRef = storageReference.child(user.getfName() + "_" + user.getlName() + "_" + System.currentTimeMillis() + "." + Funct.getFileExtension(context, user.getImage_Uri()));

                            fileRef.putFile(user.getImage_Uri()).addOnSuccessListener(taskSnapshot -> {
                                mListener.onUploadEnd();
                                fileRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        User user2 = new User(id, user_auth.getEmail(), user.getfName(), user.getlName(), user.getPhone(), System.currentTimeMillis(), uri.toString(),
                                                0, 0,"",true, true, user.getMarkerRandomColor());

                                        reference.child(Objects.requireNonNull(id)).setValue(user2).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                mListener.onSuccess(context.getString(R.string.sign_up_success), MapActivity.class);
                                                mListener.onEnd();
                                            } else {
                                                Objects.requireNonNull(user_auth).delete();
                                                mListener.onFailure(context.getString(R.string.sign_up_failure));
                                                mListener.onEnd();
                                            }
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        mListener.onFailure(context.getString(R.string.something_went_wrong));
                                        mListener.onEnd();
                                    });
                            })

                                    .addOnProgressListener(snapshot -> mListener.onUploadStart())
                                    .addOnFailureListener(e -> {
                                        mListener.onUploadEnd();
                                        mListener.onFailure(context.getString(R.string.upload_failed));
                                    });
                        }

                        else {
                            User user2 = new User(id, user_auth.getEmail(), user.getfName(), user.getlName(), user.getPhone(), System.currentTimeMillis(), null, 0, 0,
                                    "", true, true, user.getMarkerRandomColor());

                            reference.child(Objects.requireNonNull(id)).setValue(user2).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    mListener.onSuccess(context.getString(R.string.sign_up_success), MapActivity.class);
                                    mListener.onEnd();
                                } else {
                                    Objects.requireNonNull(user_auth).delete();
                                    mListener.onFailure(context.getString(R.string.sign_up_failure));
                                    mListener.onEnd();
                                }
                            });
                        }



                    } else {
                        // If sign up fails, display a message to the user.
                        mListener.onFailure(context.getString(R.string.user_alredy_exists));
                        mListener.onEnd();
                    }


                });


    }

    @Override
    public void performReadUsers(DatabaseReference reference) {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                users.add(user);
                mListener.onRead(users);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                mListener.onUpdate(user);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mListener.onDelete(user);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void performUpdateUser(Context context, DatabaseReference reference, User user, StorageReference storageReference) {
        mListener.onStart();

        if (user.getImage_Uri() == null) {
            reference.child(user.getId()).child("image_Url").setValue(null).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    mListener.onFailure(context.getString(R.string.something_went_wrong));
                }
            });
        }

        else {

            final StorageReference fileRef = storageReference.child(user.getfName() + "_" + user.getlName() + "_" + System.currentTimeMillis() + "." + Funct.getFileExtension(context, user.getImage_Uri()));

            fileRef.putFile(user.getImage_Uri()).addOnSuccessListener(taskSnapshot -> {
                mListener.onUploadEnd();
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    reference.child(user.getId()).child("image_Url").setValue(uri.toString()).addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            mListener.onFailure(context.getString(R.string.something_went_wrong));
                        }
                    });

                });
            })
            .addOnProgressListener(snapshot -> mListener.onUploadStart())
            .addOnFailureListener(e -> {
                mListener.onUploadEnd();
                mListener.onFailure(context.getString(R.string.upload_failed));
            });

        }


        reference.child(user.getId()).child("phone").setValue(user.getPhone()).addOnCompleteListener(task -> {
            if(!task.isSuccessful())
            {
                mListener.onFailure(context.getString(R.string.something_went_wrong));
            }
        });
        reference.child(user.getId()).child("fName").setValue(user.getfName()).addOnCompleteListener(task -> {
            if(!task.isSuccessful())
            {
                mListener.onFailure(context.getString(R.string.something_went_wrong));
            }
        });
        reference.child(user.getId()).child("lName").setValue(user.getlName()).addOnCompleteListener(task -> {
            if(!task.isSuccessful())
            {
                mListener.onEnd();
                mListener.onFailure(context.getString(R.string.something_went_wrong));
            }
            else
                mListener.onEnd();
        });
    }

    @Override
    public void performUpdateUserProfilePhoto(Context context, DatabaseReference reference, User user, StorageReference storageReference) {

        final StorageReference fileRef = storageReference.child(user.getfName() + "_" + user.getlName() + "_" + System.currentTimeMillis() + "." + Funct.getFileExtension(context, user.getImage_Uri()));

        fileRef.putFile(user.getImage_Uri()).addOnSuccessListener(taskSnapshot -> {
            mListener.onUploadEnd();
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                mListener.onStart();
                reference.child(user.getId()).child("image_Url").setValue(uri.toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mListener.onEnd();
                        mListener.onProfilePhotoUpdate(uri.toString());
                    } else {
                        mListener.onFailure(context.getString(R.string.something_went_wrong));
                        mListener.onEnd();
                    }
                });
            });
        })
        .addOnProgressListener(snapshot -> mListener.onUploadStart())
        .addOnFailureListener(e -> {
            mListener.onUploadEnd();
            mListener.onFailure(context.getString(R.string.upload_failed));
        });
    }

    @Override
    public void performUpdateUserLoginStatus(Context context, DatabaseReference reference, String user_id, boolean loggedIn, FirebaseAuth mAuth) {
        if (!loggedIn)
            mListener.onStart();

        reference.child(user_id).child("isLoggedIn").setValue(loggedIn).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                mListener.onLoginStatusUpdate(loggedIn, LoginActivity.class);
                if (!loggedIn)
                    mAuth.signOut();
            }

            else
                mListener.onFailure(context.getString(R.string.update_login_status_failed));

            if (!loggedIn)
                mListener.onEnd();
        });
    }

    @Override
    public void performUpdateUserVisibilityStatus(Context context, DatabaseReference reference, String user_id, boolean visible) {
        mListener.onStart();
        reference.child(user_id).child("visible").setValue(visible).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                mListener.onVisibilityStatusUpdate(visible);

            else
                mListener.onFailure(context.getString(R.string.something_went_wrong));

            mListener.onEnd();
        });
    }

    @Override
    public void performUpdateUserLocation(Context context, DatabaseReference reference, String user_id, LatLng location, String address) {
        reference.child(user_id).child("latitude").setValue(location.latitude).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                Log.d("SetLatitude", "onSuccess: latitude updated successfully");
            else
                Log.e("SetLatitude", "onFailure: latitude did not get updated");
        });

        reference.child(user_id).child("longitude").setValue(location.longitude).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                Log.d("SetLongitude", "onSuccess: longitude updated successfully");
            else
                Log.e("SetLongitude", "onFailure: longitude did not get updated");
        });
        reference.child(user_id).child("address").setValue(address).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                Log.d("SetAddress", "onSuccess: address updated successfully");
            else
                Log.e("SetAddress", "onFailure: address did not get updated");
        });
    }

    @Override
    public void performDeleteUser(Context context, DatabaseReference reference, User user, StorageReference storageReference, FirebaseAuth mAuth) {
        mListener.onStart();
        if (user.getImage_Url() != null) {
            storageReference.getStorage().getReferenceFromUrl(user.getImage_Url()).delete()
                .addOnSuccessListener(aVoid -> Log.d("DeleteUserImage", "onSuccess: deleted user image from storage"))
                .addOnFailureListener(exception -> Log.e("DeleteUserImage", "onFailure: did not delete user image from storage"));
        }

        if (reference != null) {
            reference.child(user.getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    mListener.onDeleteSuccess(context.getString(R.string.delete_account_success), LoginActivity.class);
                else
                    mListener.onFailure(context.getString(R.string.something_went_wrong));

            });
        }

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
                if (!task.isSuccessful())
                    mListener.onFailure(context.getString(R.string.something_went_wrong));
                else
                    mListener.onDeleteSuccess(context.getString(R.string.delete_account_success), LoginActivity.class);
            });
        }

    }

    @Override
    public void performgetUser(DatabaseReference reference, FirebaseAuth auth) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        if (user.getValue(User.class).getEmail() != null) {
                            if (user.getValue(User.class).getEmail().equals(auth.getCurrentUser().getEmail())) {
                                mListener.onUser(user.getValue(User.class));
                                break;
                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void performgetUserById(DatabaseReference reference, String user_id) {
        reference.orderByChild("id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        mListener.onUserById(user.getValue(User.class));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
