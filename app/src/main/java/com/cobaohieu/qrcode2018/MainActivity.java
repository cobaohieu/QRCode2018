package com.cobaohieu.qrcode2018;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private Button about_btn;
    private Button create_btn;
    private Button scan_btn;
    private Button scan_btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create_btn = (Button) findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Create.class);
                startActivity(intent);
            }
        });

        scan_btn = (Button) findViewById(R.id.scan_btn);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Scan.class);
                startActivity(intent);
            }
        });

        scan_btn2 = (Button) findViewById(R.id.scan_btn2);
        scan_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanImageFromStorage.class);
                startActivity(intent);
            }
        });

        about_btn = (Button) findViewById(R.id.about_btn);
        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });
    }
}
