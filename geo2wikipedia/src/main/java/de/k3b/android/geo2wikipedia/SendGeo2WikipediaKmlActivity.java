/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of geo2wikipedia https://github.com/k3b/VirtualCamera/
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.geo2wikipedia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.k3b.util.TempFileUtil;

/**
 * Translates from ACTION_SEND(TO)/VIEW with geo-uri to ACTION_SEND(TO)/VIEW with kml/kmz uri
 */
public class SendGeo2WikipediaKmlActivity extends Activity {
    private static final String TAG = "k3b.geo2wikipedia";

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 100;
    static private final int ACTION_REQUEST_IMAGE_CAPTURE = 4;
    private static final String STATE_RESULT_PHOTO_URI = "resultPhotoUri";

    private Uri resultPhotoUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.resultPhotoUri = savedInstanceState.getParcelable(STATE_RESULT_PHOTO_URI);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
            return;
        }

        onSendKml();

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onSendKml();
            } else {
                String message = "" + getText(R.string.permission_error);
                Toast.makeText(this,message , Toast.LENGTH_LONG).show();
                Log.e(TAG, message);
                setResult(Activity.RESULT_CANCELED, null);
                finish();
            }

        }
    }

    private void onSendKml() {
        this.resultPhotoUri = createSharedUri();
        String action = getIntent().getAction();

        Intent intent = new Intent(action)
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Intent.ACTION_SEND.compareTo(action) == 0) {
            intent.putExtra(Intent.EXTRA_STREAM, this.resultPhotoUri);
        } else {
            // ACTION_SENDTO or ACTION_VIEW
            intent.setData(this.resultPhotoUri);
        }

        // start the image capture Intent
        startActivityForResult(intent, ACTION_REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_REQUEST_IMAGE_CAPTURE && this.resultPhotoUri != null) {
            if (resultCode == RESULT_OK) {
                onResponsePhoto(this.resultPhotoUri);
            } else {
                getContentResolver().delete(this.resultPhotoUri, null, null);
                this.resultPhotoUri = null;
                setResult(Activity.RESULT_CANCELED, null);
                finish();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onResponsePhoto(Uri photo) {
        Intent result = new Intent();
        result.setData(photo);
        result.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    protected File getSharedDir() {
        File sharedDir = new File(this.getFilesDir(), "shared");
        sharedDir.mkdirs();

        // #11: remove unused temporary crops from send/get_content after some time.
        TempFileUtil.removeOldTempFiles(sharedDir, System.currentTimeMillis());
        return sharedDir;
    }

    protected String createCropFileName() {
        return new SimpleDateFormat("'img_'yyyyMMsd-HHmmss'.jpg'").format(new Date());
    }

    protected Uri createSharedUri() {
        File outFile = new File(getSharedDir(), createCropFileName());
        return FileProvider.getUriForFile(this, "de.k3b.geo2wikipedia", outFile);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_RESULT_PHOTO_URI, this.resultPhotoUri);
    }


}
