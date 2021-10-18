int offset = 0; //set the correction offset value

// int initl = 0; //initializer

int lowBattCounter = 0;

bool isLowBatt = false;

// Powerbank
double chargeThreshold = 10.00; //set the charge threshhold //3.00powerbank
//double maxPeak = 20.00; //set max voltage peak //5.5powerbank
//double minPeak, temp;

//motor battery
// double chargeThreshholdM = 11.50; //set the charge threshhold Motor
// double maxPeakM = 12.75; //set max voltage peak
// double minPeakM, tempM;

const int MAINBATT_HIGH_BIN_READY = 0xA; //10
const int MAINBATT_HIGH_BIN_FULL = 0xB; //11
const int MAINBATT_LOW_BIN_READY = 0xC; //12
const int MAINBATT_LOW_BIN_FULL = 0xD; //13

// const int MAINBATT_HIGH_MOTORBATT_HIGH_BIN_READY = 0xA; //10
// const int MAINBATT_HIGH_MOTORBATT_HIGH_BIN_FULL = 0xB; //11
// const int MAINBATT_HIGH_MOTORBATT_LOW_BIN_READY = 0xC; //12
// const int MAINBATT_HIGH_MOTORBATT_LOW_BIN_FULL = 0xD; //13
// const int MAINBATT_LOW_MOTORBATT_HIGH_BIN_READY = 0xE; //14
// const int MAINBATT_LOW_MOTORBATT_HIGH_BIN_FULL = 0xF; //15
// const int MAINBATT_LOW_MOTORBATT_LOW_BIN_READY = 0x10; //16
// const int MAINBATT_LOW_MOTORBATT_LOW_BIN_FULL = 0x11; //17

void checkMainBatt() {
  // Robojax.com voltage sensor
  int volt = analogRead(A0);// read the input
  // int voltM = analogRead(A1);// read the input Motor
  double voltage = map(volt,0,1023, 0, 2500) + offset;// map 0-1023 to 0-2500 and add correction offset
  // double voltageM = map(voltM,0,1023, 0, 2500) + offset;// map 0-1023 to 0-2500 and add correction offset
  voltage /=100;// divide by 100 to get the decimal values
  // voltageM /=100;// divide by 100 to get the decimal values
  Serial.print("Voltage: ");
  Serial.print(voltage);//print the voltge
  Serial.println("V");

  // Serial.print("MOTOR Voltage: ");
  // Serial.print(voltageM);//print the voltge
  // Serial.println("V");

  // if(initl == 0)
  // {
  //   if(voltage <= maxPeak ) 
  //   // && voltageM <= maxPeakM)
  //   {
  //     temp = voltage;
  //     // tempM = voltageM;
  //   }
    
  //   minPeak = temp;
  //   // minPeakM = tempM;

  //   initl = 1;
  //   Serial.println("INIT");
  //   Serial.print("INITIAL MIN PEAK: ");
  //   Serial.println(minPeak);

  //   // Serial.print("INITIAL MOTOR MIN PEAK: ");
  //   // Serial.println(minPeakM);
  // }

  // if(voltage < minPeak)
  // {
  //   lowBattCounter++;
  //   Serial.print("LOW BATT COUNTER: ");
  //   Serial.print(lowBattCounter);
  //   if(lowBattCounter > 10)
  //   {
  //     minPeak = voltage;
  //   }
  //   // Serial.print("CURRENT MIN PEAK: ");
  //   // Serial.println(minPeak);
  // }

  if(voltage < chargeThreshold)
  {
    lowBattCounter++;
    Serial.print("LOW BATT COUNTER: ");
    Serial.println(lowBattCounter);
    if(lowBattCounter > 5)
    {
      isLowBatt = true;
    }
  }

  // if(voltageM < minPeakM)
  // {
  //   minPeakM = voltageM;
  //   Serial.print("CURRENT MOTOR MIN PEAK: ");
  //   Serial.println(minPeakM);
  // }
  
  STATE_MAIN_PROX();

delay(500);
  
}

void STATE_MAIN_PROX(){
  //11
  if(!isLowBatt && isBinReady())
  {
    Serial.println("MAIN_BATT: HIGH");
    Serial.println("BIN: READY");
    Serial1.println(MAINBATT_HIGH_BIN_READY); 
  }

  //10
  else if(!isLowBatt && !isBinReady())
  {
    Serial.println("MAIN_BATT: HIGH");
    Serial.println("BIN: FULL");
    Serial1.println(MAINBATT_HIGH_BIN_FULL);
    
  }

  //01
  else if(isLowBatt &&  isBinReady())
  {
    Serial.println("MAIN_BATT: LOW");
    Serial.println("BIN: READY");
    Serial1.println(MAINBATT_LOW_BIN_READY);
  }

  //00
  else if(isLowBatt && !isBinReady())
  {
    Serial.println("MAIN_BATT: LOW");
    Serial.println("BIN: FULL");
    Serial1.println(MAINBATT_LOW_BIN_FULL);
  }
}

// void STATE_MAIN_MOTOR_PROX(){
//   //111
//   if(minPeak >= chargeThreshhold && minPeakM >= chargeThreshholdM && isBinReady())
//   {
//     Serial.println("MAIN_BATT: HIGH");
//     Serial.println("MOTOR_BATT: HIGH");
//     Serial.println("BIN: READY");
//     Serial1.println(MAINBATT_HIGH_MOTORBATT_HIGH_BIN_READY); 
//   }

//   //110
//   else if(minPeak >= chargeThreshhold && minPeakM >= chargeThreshholdM &&  !isBinReady())
//   {
//     Serial.println("MAIN_BATT: HIGH");
//     Serial.println("MOTOR_BATT: HIGH");
//     Serial.println("BIN: FULL");
//     Serial1.println(MAINBATT_HIGH_MOTORBATT_HIGH_BIN_FULL);
    
//   }

//   //101
//   else if(minPeak >= chargeThreshhold && minPeakM < chargeThreshholdM &&  isBinReady())
//   {
//     Serial.println("MAIN_BATT: HIGH");
//     Serial.println("MOTOR_BATT: LOW");
//     Serial.println("BIN: READY");
//     Serial1.println(MAINBATT_HIGH_MOTORBATT_LOW_BIN_READY);
//   }

//   //100
//   else if(minPeak >= chargeThreshhold && minPeakM < chargeThreshholdM &&  !isBinReady())
//   {
//     Serial.println("MAIN_BATT: HIGH");
//     Serial.println("MOTOR_BATT: LOW");
//     Serial.println("BIN: FULL");
//     Serial1.println(MAINBATT_HIGH_MOTORBATT_LOW_BIN_FULL);
//   }
  
//   //011
//   else if(minPeak < chargeThreshhold && minPeakM >= chargeThreshholdM &&  isBinReady())
//   {
//     Serial.println("MAIN_BATT: LOW");
//     Serial.println("MOTOR_BATT: HIGH");
//     Serial.println("BIN: READY");
//     Serial1.println(MAINBATT_LOW_MOTORBATT_HIGH_BIN_READY);
//   }

//   //010
//   else if(minPeak < chargeThreshhold && minPeakM >= chargeThreshholdM &&  !isBinReady())
//   {
//     Serial.println("MAIN_BATT: LOW");
//     Serial.println("MOTOR_BATT: HIGH");
//     Serial.println("BIN: FULL");
//     Serial1.println(MAINBATT_LOW_MOTORBATT_HIGH_BIN_FULL);
//   }

//   //001
//   else if(minPeak < chargeThreshhold && minPeakM < chargeThreshholdM &&  isBinReady())
//   {
//     Serial.println("MAIN_BATT: LOW");
//     Serial.println("MOTOR_BATT: LOW");
//     Serial.println("BIN: READY");
//     Serial1.println(MAINBATT_LOW_MOTORBATT_LOW_BIN_READY);
//   }

//   //000
//   else if(minPeak < chargeThreshhold && minPeakM < chargeThreshholdM &&  !isBinReady())
//   {
//     Serial.println("MAIN_BATT: LOW");
//     Serial.println("MOTOR_BATT: LOW");
//     Serial.println("BIN: FULL");
//     Serial1.println(MAINBATT_LOW_MOTORBATT_LOW_BIN_FULL);
//   }
// }
