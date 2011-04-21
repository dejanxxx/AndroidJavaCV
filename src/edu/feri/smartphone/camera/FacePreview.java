package edu.feri.smartphone.camera;

/*
 * Copyright (C) 2010,2011 Samuel Audet
 *
 * This file was based on CameraPreview.java that came with the Samples for
 * Android SDK API 8, revision 1 and contained the following copyright notice:
 *
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * IMPORTANT - Make sure your AndroidManifiest.xml file includes the following:
 *    <uses-sdk android:minSdkVersion="4" android:targetSdkVersion="4" />
 *    <uses-permission android:name="android.permission.CAMERA" />
 *    <uses-feature android:name="android.hardware.camera" />
 *    <application android:label="@string/app_name">
 *        <activity android:name="FacePreview"
 *                  android:label="@string/app_name"
 *                  android:screenOrientation="landscape">
 */

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

// ----------------------------------------------------------------------

public class FacePreview extends Activity implements OnClickListener {
    private FrameLayout layout;
    private MyFaceView faceView;
    private MyCameraView mPreview;
    private LinearLayout mainll;
    private Button btnSet;
    private EditText edt;
    private int mySetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Create our Preview view and set it as the content of our activity.
        try {
            layout = new FrameLayout(this);
            faceView = new MyFaceView(this);
            mPreview = new MyCameraView(this, faceView);
            layout.addView(mPreview);
            layout.addView(faceView);
            mainll = new LinearLayout(this);
            mainll.setOrientation(LinearLayout.VERTICAL);
            btnSet = new Button(this);
            btnSet.setText("Set param");
            btnSet.setOnClickListener(this);
            edt = new EditText(this);
            mySetting = 0;
            
            mainll.addView(edt);
            mainll.addView(btnSet);
            mainll.addView(layout);
            setContentView(mainll);
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setMessage(e.getMessage()).create().show();
        }
    }

	@Override
	public void onClick(View v) {
		try {
			mySetting = Integer.parseInt(edt.getText().toString());
			faceView.setMySettings(mySetting);
		} catch (Exception e) {
		}
	}
}
