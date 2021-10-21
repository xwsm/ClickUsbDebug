package com.debug.click;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author M
 */
public class MainActivity extends AppCompatActivity {
     TextView textView;
     String TAG=this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

        if (!DebugClickService.isStart()) {
            textView.setText("无障碍服务未开启");
        } else {
            textView.setText("无障碍服务已开启");
        }
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.do_command).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExeCommand exeCommand = new ExeCommand();
                String result=exeCommand.run("su", 2000)
                        .run("setprop service.adb.tcp.port 5555", 2000)
                        .run("stop adbd", 2000)
                        .run("start adbd", 2000).getResult();
                Log.i(TAG,result);
            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!DebugClickService.isStart()) {
                textView.setText("无障碍服务未开启");
            } else {
                textView.setText("无障碍服务已开启");
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}