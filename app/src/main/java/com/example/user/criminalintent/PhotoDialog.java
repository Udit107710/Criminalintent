package com.example.user.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by user on 1/12/2018.
 */

public class PhotoDialog extends DialogFragment {

    private ImageView mImageView;
    public PhotoDialog(){


    }

    public static PhotoDialog newInstance(String path){
        PhotoDialog photoDialog = new PhotoDialog();

        Bundle bundle = new Bundle();
        bundle.putString("Path",path);
        photoDialog.setArguments(bundle);

        return photoDialog;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.daialog_photo, container);

    }

    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mImageView = (ImageView) view.findViewById(R.id.suspect_photo);

        String path = getArguments().getString("Path");

        Bitmap bitmap = PictureUtil.getScaledBitmap(path,getActivity());

        mImageView.setImageBitmap(bitmap);

    }









}
