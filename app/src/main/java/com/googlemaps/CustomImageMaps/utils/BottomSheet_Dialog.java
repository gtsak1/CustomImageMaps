package com.googlemaps.CustomImageMaps.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.googlemaps.CustomImageMaps.R;

public class BottomSheet_Dialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_layout, container, false);
        LinearLayout button1 = v.findViewById(R.id.take_photo);
        LinearLayout button2 = v.findViewById(R.id.choose_photo);

        button1.setOnClickListener(v -> {
            mListener.onBottomSheetListenerButtonClicked("take_photo");
            dismiss();
        });
        button2.setOnClickListener(v -> {
            mListener.onBottomSheetListenerButtonClicked("choose_photo");
            dismiss();
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //this expands the bottom sheet even after a config change
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) v.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.setPeekHeight(0);
    }

    public interface BottomSheetListener {
        void onBottomSheetListenerButtonClicked(String text);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
