package com.example.kasicaprasica.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;

public class SuccessEntryDialogFragment extends DialogFragment {

    public SuccessDialogInterface successDialogInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        try {
            successDialogInterface = (SuccessDialogInterface) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment() + " must implement Success dialog interface");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Bundle args = getArguments();
        String title = args.getString("title");
        String message = args.getString("message");
        String positiveButton = args.getString("positiveButton");
        String negativeButton = args.getString("negativeButton");
        int id = args.getInt("id");

        builder.setMessage(message).setTitle(title);

        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                successDialogInterface.onDialogPositiveClick(SuccessEntryDialogFragment.this);
            }
        });

        builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                successDialogInterface.onDialogNegativeClick(SuccessEntryDialogFragment.this);
            }
        });

        return builder.create();
    }
}
