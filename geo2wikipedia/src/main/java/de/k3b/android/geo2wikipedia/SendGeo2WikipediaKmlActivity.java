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
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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

    private static final int PERMISSION_REQUEST_ID_FILE_WRITE = 23;
    private static final String PERMISSION_FILE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static final int PERMISSION_REQUEST_ID_INTERNET = 24;
    private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;

    private static final int RESULT_NO_PERMISSIONS = -22;

    private Bundle lastSavedInstanceState = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions(savedInstanceState);
    }

    private void checkPermissions(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(savedInstanceState, PERMISSION_INTERNET, PERMISSION_REQUEST_ID_INTERNET);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_FILE_WRITE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(savedInstanceState, PERMISSION_FILE_WRITE, PERMISSION_REQUEST_ID_FILE_WRITE);
            return;
        }

        onSendKml(savedInstanceState);
    }

    private void requestPermission(Bundle savedInstanceState, final String permission, final int requestCode) {
        lastSavedInstanceState = savedInstanceState;
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    private boolean isGrantSuccess(int[] grantResults) {
        return (grantResults != null)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_ID_INTERNET) {
            onRequestPermissionsResult(grantResults);
            return;
        }
        if (requestCode == PERMISSION_REQUEST_ID_FILE_WRITE) {
            onRequestPermissionsResult(grantResults);
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void onRequestPermissionsResult(int[] grantResults) {
        if (isGrantSuccess(grantResults)) {
            checkPermissions(lastSavedInstanceState);
        } else {
            Toast.makeText(this, R.string.permission_error, Toast.LENGTH_LONG).show();
            setResult(RESULT_NO_PERMISSIONS, null);
            finish();
        }
    }

    private void onSendKml(Bundle savedInstanceState) {
        this.lastSavedInstanceState = null;
        /* !!! TODO
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

         */
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

        // outState.putParcelable(STATE_RESULT_PHOTO_URI, this.resultPhotoUri);
    }


}
