package com.stegfy.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.stegfy.R;

import java.util.Objects;

public class AboutDialogFragment extends DialogFragment {

    public static AboutDialogFragment newInstance() {
        return new AboutDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_about, null);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
              .setView(v).create();
    }
}