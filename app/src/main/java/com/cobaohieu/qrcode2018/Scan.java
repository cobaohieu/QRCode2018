package com.cobaohieu.qrcode2018;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Scan extends AppCompatActivity {
    private Button scan_btn;
    private TextView result_txt;
    private Context context;
    private FloatingActionButton save_fabtn;
    private FloatingActionButton share_fabtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        init();
        hide2Button();
        //Chỉnh màn hình về dọc
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        lockScreenRotation(Configuration.ORIENTATION_PORTRAIT);
    }

    private void lockScreenRotation(int orientation)
    {
        // Stop the screen orientation changing during an event
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    //Code mở camera
    public void init() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Di chuyển Máy ảnh đến vùng chứa mã QR để quét.");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
        //integrator.getCameraSettings().setAutoFocusEnabled(true);
    }

    public void hide2Button(){
        save_fabtn = (FloatingActionButton)findViewById(R.id.save_fabtn);
        save_fabtn.setVisibility(View.INVISIBLE);
        share_fabtn = (FloatingActionButton)findViewById(R.id.share_fabtn);
        share_fabtn.setVisibility(View.INVISIBLE);
    }

    public void showSaveButton(){
        save_fabtn = (FloatingActionButton)findViewById(R.id.save_fabtn);
        save_fabtn.setVisibility(View.VISIBLE);
    }

    public void showShareButton(){
        share_fabtn = (FloatingActionButton)findViewById(R.id.share_fabtn);
        share_fabtn.setVisibility(View.VISIBLE);
    }

    //Code trả về kết quả khi quét
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Đã xảy ra lỗi trong quá trình quét mã!", Toast.LENGTH_LONG).show();
            } else {
                String contents = result.getContents();
                result_txt = (TextView) findViewById(R.id.result_txt);
                result_txt.setText(contents);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}