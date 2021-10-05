package com.example.aggos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class ControllerActivity extends AppCompatActivity {

    private BLEController bleController;
    private RemoteControl remoteControl;
    private Button dpad_fwd;
    private Button dpad_left;
    private Button dpad_right;
    private Button refresh;

    public ConstraintLayout holderBinReady;
    public ConstraintLayout holderBinFull;

    public ConstraintLayout holderBattHigh;
    public ConstraintLayout holderBattLow;

    private WebView cam;

    private boolean isLEDOn = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        dpad_fwd = findViewById(R.id.dpad_fwd);
        dpad_left = findViewById(R.id.dpad_left);
        dpad_right = findViewById(R.id.dpad_right);

        refresh = findViewById(R.id.refreshBtn);

        //CAMERA
        cam = findViewById(R.id.cam);
        WebSettings webSettings = cam.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        cam.setWebViewClient(new Callback());
        cam.loadUrl("http://192.168.43.94");

        holderBinReady = findViewById(R.id.holder_binStatReady);
        holderBinFull = findViewById(R.id.holder_binStatFull);

        holderBattHigh = findViewById(R.id.highBatteryHolder);
        holderBattLow = findViewById(R.id.lowBatteryHolder);

        this.bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(this.bleController);

        bleController.setCharacteristicNotification(true);



        dpad_fwd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    remoteControl.conveyorStart();
                    remoteControl.steerFWD();
                    Toast.makeText(ControllerActivity.this, "FORWARD BUTTON", Toast.LENGTH_SHORT).show();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    remoteControl.steerSTOP();
                    Toast.makeText(ControllerActivity.this, "MOTOR STOP", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

        dpad_left.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    remoteControl.steerLEFT();
                    Toast.makeText(ControllerActivity.this, "LEFT BUTTON", Toast.LENGTH_SHORT).show();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    remoteControl.steerSTOP();
                    Toast.makeText(ControllerActivity.this, "MOTOR STOP", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

        dpad_right.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    remoteControl.steerRIGHT();
                    Toast.makeText(ControllerActivity.this, "RIGHT BUTTON", Toast.LENGTH_SHORT).show();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    remoteControl.steerSTOP();
                    Toast.makeText(ControllerActivity.this, "MOTOR STOP", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ControllerActivity.this, "Refreshing Camera View...", Toast.LENGTH_SHORT).show();
                cam.loadUrl("http://192.168.43.94");
            }
        });

        //livedata
        renderUpdates();
    }

    private void renderUpdates(){
        MyViewModel myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        Observer<Integer> liveDataObserver=new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
               if(integer == 0)
               {
                   holderBinReady.setVisibility(View.VISIBLE);
                   holderBinFull.setVisibility(View.INVISIBLE);

                   holderBattHigh.setVisibility(View.VISIBLE);
                   holderBattLow.setVisibility(View.INVISIBLE);
               }
               else if (integer == 1)
               {
                   holderBinReady.setVisibility(View.INVISIBLE);
                   holderBinFull.setVisibility(View.VISIBLE);

                   holderBattHigh.setVisibility(View.VISIBLE);
                   holderBattLow.setVisibility(View.INVISIBLE);
               }
               else if (integer == 2)
               {
                   holderBinReady.setVisibility(View.VISIBLE);
                   holderBinFull.setVisibility(View.INVISIBLE);

                   holderBattHigh.setVisibility(View.INVISIBLE);
                   holderBattLow.setVisibility(View.VISIBLE);
               }
               else if (integer == 3)
               {
                   holderBinReady.setVisibility(View.INVISIBLE);
                   holderBinFull.setVisibility(View.VISIBLE);

                   holderBattHigh.setVisibility(View.INVISIBLE);
                   holderBattLow.setVisibility(View.VISIBLE);
               }
            }
        };
        myViewModel.getLiveData().observe(this,liveDataObserver);
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}
