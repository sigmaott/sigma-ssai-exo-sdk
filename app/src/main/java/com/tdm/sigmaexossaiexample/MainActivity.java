package com.tdm.sigmaexossaiexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout layoutAction;
    private RelativeLayout layoutInput;
    private Button btSSAI;
    private Button btUrlTracking;
    public static int EX_SSAI_LINK = 1;
    public static int EX_SOURCE_LINK = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        action();
    }


    private void initView() {
        layoutAction = findViewById(R.id.layout_action);
        layoutInput = findViewById(R.id.layout_edt);
        btSSAI = findViewById(R.id.bt_ssai);
        btUrlTracking = findViewById(R.id.bt_url_tracking);
        btSSAI.setNextFocusDownId(R.id.bt_url_tracking);
        btUrlTracking.setNextFocusUpId(R.id.bt_ssai);
        btSSAI.requestFocus();
    }

    private void action() {
        btSSAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("type", EX_SSAI_LINK);
                intent.putExtra("url_ssai","https://ssai-stream-dev.sigmaott.com/manifest/manipulation/session/79799fec-88b3-4a11-b323-1c0b8113370e/origin04/scte35-av/master.m3u8");
                startActivity(intent);
            }
        });
        btUrlTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("type", EX_SOURCE_LINK);
                intent.putExtra("url_source","");
                intent.putExtra("url_tracking","");
//                startActivity(intent);
                Toast.makeText(MainActivity.this,"urlSource, urlTracking is empty!",Toast.LENGTH_LONG).show();
            }
        });
    }
}