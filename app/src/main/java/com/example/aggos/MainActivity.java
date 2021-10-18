package com.example.aggos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BLEControllerListener{

    private Button btnFaq;
    private Button btnInstruction;
    private Button btnScan;
    private Button btnHide;
    private Button btnConnect;
    private Button btnOpnCtrl;

    private ImageView btnLogo;

    private TextView logView;

    private BLEController bleController;
    private ConstraintLayout scanner;

    private LinearLayout mask;

    private String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnFaq = findViewById(R.id.btnFAQ);
        btnInstruction = findViewById(R.id.btnInstruction);
        btnScan = findViewById(R.id.btnSCAN);
        btnLogo = findViewById(R.id.mainLogo);
        btnHide = findViewById(R.id.btnHide);
        btnConnect = findViewById(R.id.btnCONNECT);
        btnOpnCtrl = findViewById(R.id.btnOPENCTRL);

        scanner = findViewById(R.id.scanner);
        logView = findViewById(R.id.logView);

        mask = findViewById(R.id.mask);

        this.bleController = BLEController.getInstance(this);



        logView.setMovementMethod(new ScrollingMovementMethod());

        //final ScanningDialog scanningDialog = new ScanningDialog(MainActivity.this);

        checkBLESupport();
        checkPermissions();
        promptLocation();

        initConnectButton();

//      FAQ BUTTON
        btnFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "FAQ button clicked", Toast.LENGTH_SHORT).show();
                openFAQ();
            }
        });

//      INSTRUCTIONS BUTTON
        btnInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Instruction button clicked", Toast.LENGTH_SHORT).show();
                openInstructions();
            }
        });

//      SCAN BUTTON
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "START button clicked", Toast.LENGTH_SHORT).show();
                //openController();
                //scanningDialog.startScanningDialog();

                scanner.setVisibility(View.VISIBLE);
                mask.setVisibility(View.VISIBLE);

                //checkPermissions();

                if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTIntent, 1);
                }
            }
        });

//      LOGO (ALTERNATIVE SCAN BUTTON)
        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "LOGO clicked", Toast.LENGTH_SHORT).show();
                openController();
            }
        });

        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hide Button", Toast.LENGTH_SHORT).show();
                scanner.setVisibility(View.INVISIBLE);
                mask.setVisibility(View.INVISIBLE);
            }
        });

        btnOpnCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openController();
            }
        });
    }

    private void initConnectButton() {
            btnConnect = findViewById(R.id.btnCONNECT);
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //connectButton.setEnabled(false);
                    log("Connecting...");
                    bleController.connectToDevice(deviceAddress);
                }
            });
        }

        private void initDisconnectButton() {
            //this.disconnectButton = findViewById(R.id.disconnectButton);
            //this.disconnectButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                   // disconnectButton.setEnabled(false);
//                    log("Disconnecting...");
//                    bleController.disconnect();
//                }
//            });
        }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
           finish();
        System.exit(0);
    }

    private void promptLocation(){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    finishAffinity();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        }
    }

//  Check if the bluetooth is enabled; if not, request
    @Override
    protected void onStart() {
        super.onStart();

        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.deviceAddress = null;
        this.bleController = BLEController.getInstance(this);
        this.bleController.addBLEControllerListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            log("[BLE]\tSearching for AGGOS...");
            this.bleController.init();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.bleController.removeBLEControllerListener(this);
        //   stopHeartBeat();
    }




    public void openFAQ(){
        Intent intent = new Intent(MainActivity.this, FAQActivity.class);
        startActivity(intent);
        //finish();
    }

    public void openInstructions(){
        Intent intent = new Intent(MainActivity.this, InstructionsActivity.class);
        startActivity(intent);
        //finish();
    }

    public void openController() {
        Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
        startActivity(intent);
        //finish();
    }

//  Check if the device has bluetooth; if not, exitw
    private void checkBLESupport() {
        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

//  Check if Fine Location is permitted; This is to check nearby devices
    private void checkPermissions() {
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(MainActivity.this, "\"Access Fine Location\" permission not granted yet! \n Without this permission Bluetooth devices cannot be searched!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services permission not granted yet!");
            builder.setMessage("Without this permission Bluetooth devices cannot be searched!");
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
    }

//  SCAN LOG
    private void log(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.setText(
                logView.getText() + "\n" +
                text);
            }
        });
    }

    @Override
    public void BLEControllerConnected() {
        log("[BLE]\tConnected");
        //input delay
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                disconnectButton.setEnabled(true);
//                switchLEDButton.setEnabled(true);
                btnScan.setVisibility(View.INVISIBLE);
                btnOpnCtrl.setVisibility(View.VISIBLE);
                //bleController.discover();
                //openController();
            }
        });
        //startHeartBeat();
        openController();
    }

    @Override
    public void BLEControllerDisconnected() {
        log("[BLE]\tDisconnected");
        //disableButtons();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnScan.setVisibility(View.INVISIBLE);
                btnConnect.setVisibility(View.VISIBLE);
            }
        });
//        this.isLEDOn = false;
//        stopHeartBeat();
    }

    @Override
    public void BLEDeviceFound(String name, String address) {
        this.deviceAddress = address;
        //this.btnScan.setVisibility(View.INVISIBLE);
        this.btnConnect.setVisibility(View.VISIBLE);
        log("Device " + name + " found with address " + address);
    }



//    public void openScanPopup(){
//        Intent intent = new Intent(MainActivity.this,ScanPopupActivity.class);
//        startActivity(intent);
//    }
}