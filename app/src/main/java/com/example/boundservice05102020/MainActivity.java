package com.example.boundservice05102020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MyBoundService mService;
    TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MyBoundService.class);
        ContextCompat.startForegroundService(this,intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBoundService.MyBind myBind = (MyBoundService.MyBind) service;
            mService = myBind.getService();
            mService.getProgress(new OnListenProgress() {
                @Override
                public void onProgressChange(int progress) {
                    mTv.setText(progress + "");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}