#define SENSOR 2 //Pin 2 Mega

const int BIN_READY = 0xA; //10
const int BIN_FULL = 0xB; //11

int counter = 0;

//Brown: 5V DC
//Blue:  GNG
//Black: Signal, to PIN 2




void proxSensor()
  {
  int L =digitalRead(SENSOR);// read the sensor 

     if(L == 0)
     {
        counter++;
        delay(1000);
        if(counter >= 3){
        Serial.println("Bin is FULL!");
        BTserial.println(BIN_FULL);
        }
//        else{
//        Serial.println("Sensor is detecting something...");
//        BTserial.println("Sensor is detecting something...");
//        }
     }
     else
     {
        counter=0;
        Serial.println("Bin is not yet full");
        BTserial.println(BIN_READY);
     }
     delay(500);
  }
