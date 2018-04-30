package com.cobaohieu.qrcode2018;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.print.PrintAttributes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

public class ScanImageFromStorage extends AppCompatActivity implements View.OnClickListener {

    private ImageButton scan_imgBtn;
    private Button scan_btn;
    private static final int SELECT_PHOTO = 100;
    private TextView result_txt;
    //for easy manipulation of the result
    private String LOG_TAG = "GenerateQRCode";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image_from_storage);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                //Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
                Log.v(LOG_TAG,"Permission is granted");
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            Log.v(LOG_TAG,"Permission is granted");
            // Code for Below 23 API Oriented Device
            // Do next code
        }
        Button scan_btn = (Button)findViewById(R.id.scan_btn);
        //cast neccesary variables to their views
        scan_imgBtn = (ImageButton)findViewById(R.id.scan_imgBtn);
        //set a new custom listener
        scan_imgBtn.setOnClickListener(this);
        scan_imgBtn.setVisibility(View.INVISIBLE);
        //launch gallery via intent
        Intent photoPic = new Intent(Intent.ACTION_PICK);
        photoPic.setType("image/*");
        startActivityForResult(photoPic, SELECT_PHOTO);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ScanImageFromStorage.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ScanImageFromStorage.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ScanImageFromStorage.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ScanImageFromStorage.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan_btn2:
                //launch gallery via intent
                Intent photoPic = new Intent(Intent.ACTION_PICK);
                photoPic.setType("image/*");
                startActivityForResult(photoPic, SELECT_PHOTO);
                break;
        }
    }

    //call the onactivity result method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    //doing some uri parsing
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        //getting the image
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi không tìm thấy file ảnh.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    //decoding bitmap
                    Bitmap bMap = BitmapFactory.decodeStream(imageStream);
                    scan_imgBtn.setImageURI(selectedImage);// To display selected image in image view
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    // copy pixel data from the Bitmap into the 'intArray' array
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();// use this otherwise
                    // ChecksumException
                    try {
                        Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
                        decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                        decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
                        Result result = reader.decode(bitmap);
                        //*I have created a global string variable by the name of barcode to easily manipulate data across the application*//
                        String contents = "";
                        contents = result.getText();
                        result_txt = (TextView) findViewById(R.id.result_txt);
                        result_txt.setText(contents);
                        scan_imgBtn.setVisibility(View.INVISIBLE);
                        /*
                        //do something with the results for demo i created a popup dialog
                        if(contents != null){
                            result_txt.setText(contents.toString());
                            scan_imgBtn.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Scan Result");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setMessage("" + barcode);
                            AlertDialog alert1 = builder.create();
                            alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent (getBaseContext(),MainActivity.class);
                                    startActivity(i);
                                }
                            });
                            alert1.setCanceledOnTouchOutside(false);
                            alert1.show();
                            }
                        else
                        {
                            result_txt.setText("Nothing found try a different image or try again");
                            scan_imgBtn.setVisibility(View.INVISIBLE);
                            /*
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Scan Result");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setMessage("Nothing found try a different image or try again");
                            AlertDialog alert1 = builder.create();
                            alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent (getBaseContext(),MainActivity.class);
                                    startActivity(i);
                                }
                            });
                            alert1.setCanceledOnTouchOutside(false);
                            alert1.show();
                        }
                        */
                        //the end of do something with the button statement.
                    } catch (NotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi ảnh không hiển thị được kết quả.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (ChecksumException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi ứng dụng không quét được mã từ ảnh của bạn.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (FormatException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi định dạng mã QR sai", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi ứng dụng không quét được mã từ ảnh của bạn", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } 
                }
                break;
        }
    }
}
