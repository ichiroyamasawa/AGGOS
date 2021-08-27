package com.example.aggos;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BLEController extends MyViewModel{
    private static BLEController instance;

    private BluetoothLeScanner scanner;
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager bluetoothManager;

    private BluetoothGattCharacteristic btGattChar = null;


    public static String UUID_BLE = "0000FFE0-0000-1000-8000-00805f9b34fb";
    public static String CHAR_BLE = "0000FFE1-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"; //descriptor

    private ArrayList<BLEControllerListener> listeners = new ArrayList<>();
    private HashMap<String, BluetoothDevice> devices = new HashMap<>();

    private String[] services;

    private BluetoothGattService btGattService;


    public String ready = "[49, 48, 13, 10]";
    public String full = "[49, 49, 13, 10]";




    private BLEController(Context ctx) {
        this.bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public static BLEController getInstance(Context ctx) {
        if(null == instance)
            instance = new BLEController((ctx));

        return instance;
    }

    public void addBLEControllerListener(BLEControllerListener l) {
        if(!this.listeners.contains(l))
            this.listeners.add(l);
    }

    public void removeBLEControllerListener(BLEControllerListener l) {
        this.listeners.remove(l);
    }

    public void init() {
        this.devices.clear();
        this.scanner = this.bluetoothManager.getAdapter().getBluetoothLeScanner();
        scanner.startScan(bleCallback);
    }

    private ScanCallback bleCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if(!devices.containsKey(device.getAddress()) && isThisTheDevice(device)) {
                deviceFound(device);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for(ScanResult sr : results) {
                BluetoothDevice device = sr.getDevice();
                if(!devices.containsKey(device.getAddress()) && isThisTheDevice(device)) {
                    deviceFound(device);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i("[BLE]", "scan failed with errorcode: " + errorCode);
        }
    };

    private boolean isThisTheDevice(BluetoothDevice device) {
        return null != device.getName() && device.getName().startsWith("AGGOS");
    }

    private void deviceFound(BluetoothDevice device) {
        this.devices.put(device.getAddress(), device);
        fireDeviceFound(device);
    }
    public void connectToDevice(String address) {
        this.device = this.devices.get(address);
        this.scanner.stopScan(this.bleCallback);
        Log.i("[BLE]", "connect to device " + device.getAddress());
        this.bluetoothGatt = device.connectGatt(null, false, this.bleConnectCallback);
    }

    private final BluetoothGattCallback bleConnectCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("[BLE]", "start service discovery " + bluetoothGatt.discoverServices());
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                btGattChar = null;
                Log.w("[BLE]", "DISCONNECTED with status " + status);
                fireDisconnected();
            }else {
                Log.i("[BLE]", "unknown state " + newState + " and status " + status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(null == btGattChar) {
                for (BluetoothGattService service : gatt.getServices()) {
                    if (service.getUuid().toString().toUpperCase().startsWith("0000FFE0")) {
                        List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();

                        for (BluetoothGattCharacteristic bgc : gattCharacteristics) {
                            if (bgc.getUuid().toString().toUpperCase().startsWith("0000FFE1")) {
                                int chprop = bgc.getProperties();
                                if (((chprop & BluetoothGattCharacteristic.PROPERTY_WRITE) | (chprop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
                                    btGattChar = bgc;
                                    Log.i("[BLE]", "CONNECTED and ready to send");
                                    fireConnected();
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            Log.i("onCharacteristicRead", characteristic.toString());
            byte[] value=characteristic.getValue();
            String v = new String(value);
            Log.i("onCharacteristicRead", "Value: " + v.isEmpty());
            Log.i("onCharacteristicRead", "Value: " + Arrays.toString(value));


            //getBinStatus();

            if(Arrays.toString(value).equals(ready))
            {
                Log.i("onCharacteristicRead", "ValueREADY: " + Arrays.toString(value));
                updateBin(0); // 0 = READY

            }

            else if(Arrays.toString(value).equals(full))
            {
                Log.i("onCharacteristicRead", "ValueFULL: " + Arrays.toString(value));
                updateBin(1); // 1 = FULL
            }
            else{
                Log.i("onCharacteristicRead", "ValueELSEARR: " + Arrays.toString(value));
                Log.i("onCharacteristicRead", "ValueELSEV: " + v     );
            }
        }
    };



    public void setCharacteristicNotification(boolean enabled) {

        bluetoothGatt.setCharacteristicNotification(btGattChar, enabled);

            BluetoothGattDescriptor descriptor = btGattChar.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);

    }

    private void fireDisconnected() {
        for(BLEControllerListener l : this.listeners)
            l.BLEControllerDisconnected();

        this.device = null;
    }

    private void fireConnected() {
        for(BLEControllerListener l : this.listeners)
            l.BLEControllerConnected();
    }

    private void fireDeviceFound(BluetoothDevice device) {
        for(BLEControllerListener l : this.listeners)
            l.BLEDeviceFound(device.getName().trim(), device.getAddress());
    }

    public void sendData(byte [] data) {
        this.btGattChar.setValue(data);
        Log.d("test",this.btGattChar.toString());
        bluetoothGatt.writeCharacteristic(this.btGattChar);
    }

//    public boolean checkConnectedState() {
//        return this.bluetoothManager.getConnectionState(this.device, GATT) == 2;
//    }

    public void disconnect() {
        this.bluetoothGatt.disconnect();
    }
}