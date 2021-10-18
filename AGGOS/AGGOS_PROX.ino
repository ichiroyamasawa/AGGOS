#define SENSOR 2 //Pin 2 Mega

int counter = 0;

// conveyor gearmotor
int IN1 = 4;
int IN4 = 5;

// bool isBLEcon = false;

//sensor connection:
//Brown: 5V DC
//Blue:  GNG
//Black: Signal, to PIN 2

void gearmotorSetup()
{
  pinMode(IN1, OUTPUT);
  pinMode(IN4, OUTPUT);
}

boolean isBinReady()
  {
  int L =digitalRead(SENSOR);// read the sensor 
  // Serial.print("isBLECON? ");
  // Serial.println(isBLEcon);
    // if( isBLEcon == true)
    // {
     if(L == 0)
     {
        counter++;
        
        if(counter >= 3){
        //Serial.println("Bin is FULL!");
        //BTserial.println(BIN_FULL);
        conveyorOFF();
        return false;
        }
        delay(500);
     }
     else
     {
        
        counter=0;
        //Serial.println("Bin is not yet full");
        //BTserial.println(BIN_READY);
        conveyorON();
        return true;
        delay(500);
     }
     delay(500);
  }
  // }

// void isBleConnected(int stat){
//   Serial.print(stat);
//   if (stat == 6)
//   {
//     isBLEcon = true;
//   }
// }

void conveyorON()
{
  digitalWrite(IN1, HIGH); // turn on gearmotor
  digitalWrite(IN4, HIGH); 
}

void conveyorOFF()
{
  digitalWrite(IN1, LOW); // turn off gearmotor
  digitalWrite(IN4, LOW); 
}