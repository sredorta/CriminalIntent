
package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;


/**
 * Created by sredorta on 9/29/2016.
 */
public class PhotoFragment extends DialogFragment {
    private static final String ARG_IMG = "image";

    private ImageView mImageView;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        File  myPhotoFile = (File) getArguments().getSerializable(ARG_IMG);
        Bitmap bitmap = PictureUtils.getScaledBitmap(myPhotoFile.getPath(), getActivity());


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mImageView = (ImageView) v.findViewById(R.id.dialog_photo);
        mImageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.photo_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .create();
    }

    public static PhotoFragment newInstance(File mPhoto) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMG, mPhoto);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int ResultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(),ResultCode,intent);
    }


}
