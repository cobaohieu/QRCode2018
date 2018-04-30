package com.cobaohieu.qrcode2018;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


//public class Create extends Activity implements OnClickListener {
    public class Create extends AppCompatActivity implements View.OnClickListener{
    private EditText text;
    private Button generator_btn;
    private ImageView imageView;
    private FloatingActionButton save_fabtn;
    private FloatingActionButton share_fabtn;
    private AlertDialog dialog;
    private String text2Qr;
    //private String LOG_TAG = "GenerateQRCode";
    private String LOG = "";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                //Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();

                Log.v(LOG,"Permission is granted");
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            Log.v(LOG,"Permission is granted");
            // Code for Below 23 API Oriented Device
            // Do next code
        }
        init();
        setupView();
    }

    public void setupView(){
        save_fabtn.setOnClickListener(this);
        share_fabtn.setOnClickListener(this);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void init(){
        imageView = (ImageView) findViewById(R.id.image_view);
        save_fabtn = (FloatingActionButton) findViewById(R.id.save_fabtn);
        share_fabtn = (FloatingActionButton) findViewById(R.id.share_fabtn);
        text = (EditText) findViewById(R.id.text);
        generator_btn = (Button) findViewById(R.id.generator_btn);
        imageView = (ImageView) findViewById(R.id.image_view);
        generator_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text2Qr = text.getText().toString().trim();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,450,450);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);

                }
                catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static Bitmap viewToBitmap(View view, int width, int height){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //image.setImageBitmap(bitmap);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.save_fabtn: {
                dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Save Image");
                dialog.setMessage("You sure to save image?");
                dialog.setButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSave();
                    }
                });
                dialog.setButton2("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            }
            case R.id.share_fabtn: {
                startShare();
                break;
            }
        }
    }

    public void startShare(){
        Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "QRCode2018.jpg");
        try{
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/QRCode2018.jpg"));
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    public void startSave(){
        FileOutputStream fileOutputStream = null;
        File file = getDisc();
        if (!file.exists() && !file.mkdirs()){
            Toast.makeText(this,"Can't create directory to save image", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img" + date + ".jpg";
        String file_name = file.getAbsolutePath() + "/" + name;
        File new_file = new File(file_name);
        try{
            fileOutputStream = new FileOutputStream(new_file);
            Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);
            Toast.makeText(this, "Save image success!", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        refreshGallery(new_file);
    }
    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }
    private File getDisc(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "QRCode2018");
    }
}
