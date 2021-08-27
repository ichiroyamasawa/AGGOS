package com.example.aggos;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MyViewModel extends ViewModel {
    //MutableLiveData is a LiveData which publicly exposes setValue(T) and
    // postValue(T) method.
//    public MutableLiveData<String> mutableLiveData=new MutableLiveData<>();
//
//
//
//    public void updateBin(String stat){
//                mutableLiveData.postValue(stat);
//        Log.i("onCharacteristicReadMODEL", "ValueACTMODEL: " + stat);
//            }
//
//            public void sendUpdate(){
//                mutableLiveData.postValue(mutableLiveData.getValue());
//            }
//    //Constructor
//    public MyViewModel(){
//        sendUpdate();
//    }
//
//    public MutableLiveData<String> getLiveData() {
//
//        return mutableLiveData;
//    }

    private MutableLiveData<Integer> mutableLiveData=new MutableLiveData<>();

    private static int BEGIN_AFTER = 1000, INTERVAL = 1000;
    private static int status;

    private void startTimer(){
        Timer timer=new Timer();
//        //Schedule the specified task for repeated fixed-rate execution,
//        // beginning after the specified delay. Subsequent executions
//        // take place at approximately regular intervals, separated by
//        // the specified period.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mutableLiveData.postValue(status);
            }
        },BEGIN_AFTER,INTERVAL);


    }

    public void updateBin(int stat){
        status = stat;

        Log.i("onCharacteristicReadMODEL", "ValueACTMODEL: " + status);
            }

    //Constructor
    public MyViewModel(){
       startTimer();
//       updateBin();
    }
    public MutableLiveData<Integer> getLiveData() {
        return mutableLiveData;
    }
}

//end