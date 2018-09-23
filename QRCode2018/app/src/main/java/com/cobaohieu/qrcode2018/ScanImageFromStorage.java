package com.cobaohieu.qrcode2018;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

public class ScanImageFromStorage extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_PHOTO = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageButton scan_imgBtn;
    private Button scan_btn;
    private TextView result_txt;
    private String LOG_TAG = "GenerateQRCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image_from_storage);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                //Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
                Log.v(LOG_TAG, "Ứng dụng đã được cấp quyền.");
            } else {
                requestPermission();
                // Code for permission
            }
        } else {
            Log.v(LOG_TAG, "Ứng dụng đã được cấp quyền.");
            // Code for Below 23 API Oriented Device
            // Do next code
        }
        Button scan_btn = (Button) findViewById(R.id.scan_btn);
        //cast neccesary variables to their views
        scan_imgBtn = (ImageButton) findViewById(R.id.scan_imgBtn);
        //set a new custom listener
        scan_imgBtn.setOnClickListener(this);
        scan_imgBtn.setVisibility(View.INVISIBLE);
        //mở thư viện ảnh thông qua intent
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
            Toast.makeText(ScanImageFromStorage.this, "Xin vui lòng cấp quyền truy cập Bộ nhớ cho ứng dụng.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ScanImageFromStorage.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn2:
                //mở thư viện ảnh thông qua intent
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
                    //So sánh đường dẫn chưa tập ảnh
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        //Lấy hình ảnh từ thư
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi không tìm thấy tệp ảnh.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    //Giải mã bitmap
                    Bitmap bMap = BitmapFactory.decodeStream(imageStream);
                    scan_imgBtn.setImageURI(selectedImage);// To display selected image in image view
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    // copy pixel data from the Bitmap into the 'intArray' array
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();// use this otherwise
                    //Kiểm tra ngoại lệ
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
                        //thông báo lỗi ngoại lệ
                    } catch (NotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi ứng dụng không quét được mã QR từ ảnh của bạn.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (ChecksumException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi ứng dụng không quét được mã QR từ ảnh của bạn.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (FormatException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi định dạng mã QR sai", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "Lỗi ứng dụng không quét được mã QR từ ảnh của bạn.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
