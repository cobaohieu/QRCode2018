package com.cobaohieu.qrcode2018;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    protected void onStart() {
        super.onStart();
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText("Ứng dụng quét mã vạch QR cơ bản trên điện thoại.\nSử dụng cho mục đích học tập.\nPhiên bản thử nghiệm 1.0.0.\n© Bản quyền thuộc về \nCổ Bảo Hiếu\nNăm 2018.");
    }
}
