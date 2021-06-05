package com.example.kasicaprasica.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.kasicaprasica.R;
import com.example.kasicaprasica.database.DatabaseHelper;
import com.example.kasicaprasica.dialogFragments.AlertDialogFragment;
import com.example.kasicaprasica.dialogFragments.SuccessEntryDialogFragment;
import com.example.kasicaprasica.interfaces.AlertDialogInterface;
import com.example.kasicaprasica.interfaces.SuccessDialogInterface;
import com.example.kasicaprasica.models.Bank;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class EditBankFragment extends Fragment implements SuccessDialogInterface, AlertDialogInterface {

    private DatabaseHelper helper;

    private MediaPlayer player;

    private Bank b;

    private EditText bankName;
    private EditText valueText;

    private ImageView prostorZaSliku;
    private ImageButton newPhotoButton;
    private String previousPhotoPath = "";
    private String currentPhotoPath;

    private int bankId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null) {
            bankId = bundle.getInt("Bank_id");
        }
        return inflater.inflate(R.layout.fragment_edit_bank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helper = new DatabaseHelper(getContext());

        player = MediaPlayer.create(getContext(), R.raw.button_sound);

        b = new Bank();
        Cursor c = helper.getOneBank(bankId);
        if(c.getCount() != 0) {
            c.moveToFirst();
            b.setId(c.getInt(0));
            b.setBankName(c.getString(1));
            b.setDateOfCreation(c.getString(2));
            b.setImagePath(c.getString(3));
        }
        c.close();


        bankName = view.findViewById(R.id.bank_name_edit_text);
        prostorZaSliku = view.findViewById(R.id.prostor_za_sliku_edit_bank);
        newPhotoButton = view.findViewById(R.id.take_picture_button_edit_bank);

        if(b.getImagePath() != null) {
            if(!b.getImagePath().equals("")) {
                prostorZaSliku.setVisibility(View.VISIBLE);
                prostorZaSliku.setImageBitmap(generatePhoto(b.getImagePath()));
            } else {
                prostorZaSliku.setVisibility(View.GONE);
            }
        } else {
            prostorZaSliku.setVisibility(View.GONE);
        }

        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                player.start();
                dispatchTakePictureIntent();
            }
        });

        bankName.setText(b.getBankName());
        if(b.getImagePath() != null) {
            previousPhotoPath = b.getImagePath();
        }
        currentPhotoPath = "";
        //valueText = view.findViewById(R.id.bank_value_edit_text);

        view.findViewById(R.id.save_button_edit_bank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                player.start();
                prikaziDialog();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeKeyboard();
                return false;
            }
        });
    }


    private void prikaziDialog() {
        if(b.getBankName().contentEquals(bankName.getText()) && previousPhotoPath.equals(currentPhotoPath)) {
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", "Warning!");
            arguments.putString("message", "You didn't make any changes.");
            alertDialogFragment.setArguments(arguments);
            alertDialogFragment.setTargetFragment(this, 0);
            alertDialogFragment.show(getParentFragmentManager(), "AlertDialogFragment");
        } else {
            SuccessEntryDialogFragment dialogFragment = new SuccessEntryDialogFragment();
            dialogFragment.setTargetFragment(this, 0);
            Bundle args = new Bundle();
            args.putString("title", "Editing!");
            args.putString("message", "You will change your bank information.");
            args.putString("positiveButton", "Proceed");
            args.putString("negativeButton", "Cancel");
            dialogFragment.setArguments(args);
            dialogFragment.show(getParentFragmentManager(), "SuccessEntryDialogFragment");
        }
    }

    private void spremiPromjene() {
        String naziv = bankName.getText().toString();

        b.setBankName(naziv);
        b.setImagePath(currentPhotoPath);

        boolean isInserted = helper.updateBankData(b);
        if(isInserted) {
            Toast.makeText(getContext(), "Bank name is updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "BANK NAME IS NOT updated", Toast.LENGTH_LONG).show();
        }
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onOkClick(DialogFragment dialogFragment) {
        player.start();
        dialogFragment.dismiss();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        player.start();
        spremiPromjene();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        player.start();
        dialog.dismiss();
    }

    private Bitmap generatePhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = 4;
        /*options.inJustDecodeBounds = true;
        int srcWidth  = options.outWidth;
        int srcHeight = options.outHeight;
        options.inDensity = srcWidth;
        options.inTargetDensity = srcWidth * options.inSampleSize;*/

        return BitmapFactory.decodeFile(path, options);

    }



    //CAMERA HANDLING

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e("IOException Account Fragment:", e.toString());
            }
            if(photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e("Activity Not found Account Fragment:", e.toString());
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HH:mm dd.MM.yyyy.").format(new Date(System.currentTimeMillis()));
        String imageFileName = "JPEG_" + timeStamp + "_";
        //MediaStore.Images

        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(this.currentPhotoPath);
            prostorZaSliku.setImageBitmap(imageBitmap);
            prostorZaSliku.setVisibility(View.VISIBLE);
        }
    }
}
