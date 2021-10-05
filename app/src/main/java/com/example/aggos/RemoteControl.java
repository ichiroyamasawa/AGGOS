package com.example.aggos;

import android.bluetooth.BluetoothGattService;

public class RemoteControl {
    private final static byte START = 0x1;
    private final static byte HEARTBEAT = 0x2;
    private final static byte STEER = 0x3;
    private final static byte LED_COMMAND = 0x4;

    private final static byte VALUE_OFF = 0x0;
    private final static byte VALUE_ON = (byte)0xFF;

    private final static byte VALUE_FWD = (byte)0x64; //100
    private final static byte VALUE_LEFT = (byte)0x65; //101
    private final static byte VALUE_RIGHT = (byte)0x66; //102
    private final static byte VALUE_STOP = (byte)0x67; //103

    private final static byte CONVEYOR = 0x5;
    private final static byte CONVEYOR_START = 0x6;


    private BLEController bleController;

    public RemoteControl(BLEController bleController) {
        this.bleController = bleController;
    }

    private byte [] createControlWord(byte type, byte ... args) {
        byte [] command = new byte[args.length + 3];
        command[0] = START;
        command[1] = type;
        command[2] = (byte)args.length;
        for(int i=0; i<args.length; i++)
            command[i+3] = args[i];



        return command;
    }

    public void switchLED(boolean on) {
        this.bleController.sendData(createControlWord(LED_COMMAND, on?VALUE_ON:VALUE_OFF));
    }

    public void conveyorStart() {this.bleController.sendData(createControlWord(CONVEYOR, CONVEYOR_START));}

    public void steerFWD() {
        this.bleController.sendData(createControlWord(STEER, VALUE_FWD));
    }

    public void steerLEFT() {
        this.bleController.sendData(createControlWord(STEER, VALUE_LEFT));
    }

    public void steerRIGHT() {
        this.bleController.sendData(createControlWord(STEER, VALUE_RIGHT));
    }

    public void steerSTOP() {
        this.bleController.sendData(createControlWord(STEER, VALUE_STOP));
    }


    public void heartbeat() {
        this.bleController.sendData(createControlWord(HEARTBEAT));
    }
}