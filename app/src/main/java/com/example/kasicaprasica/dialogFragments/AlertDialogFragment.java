package com.example.kasicaprasica.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kasicaprasica.interfaces.AlertDialogInterface;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;

public class AlertDialogFragment extends DialogFragment {

    public AlertDialogInterface alertDialogInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        try {
            alertDialogInterface = (AlertDialogInterface) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment() + " must implement Alert dialog interface");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Bundle bundle = getArguments();
        String title = bundle.getString("title");
        String message = bundle.getString("message");

        builder.setMessage(message).setTitle(title);

        builder.setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialogInterface.onOkClick(AlertDialogFragment.this);
            }
        });

        return builder.create();
    }
}
