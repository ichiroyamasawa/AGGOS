#include <Servo.h>

#define MAX_SIGNAL 2000

#define MIN_SIGNAL 1000

#define MOTOR_PIN_R 8
#define MOTOR_PIN_L 9

int POWER = 1100; //1000-2000

Servo motorL, motorR;

  
const int STEER = 0x3;
const int SWITCH_LED = 0x4;
const int CONVEYOR = 0x5;

const int lightsPin =  13;

const initController() {
  pinMode(lightsPin, OUTPUT);
}

void executeCommand(int cmd[]) {
  switch(cmd[1]) {
    case SWITCH_LED: switchLight(cmd[3]); break;
    case STEER: escController(cmd[3]); break;
    // case CONVEYOR: isBleConnected(cmd[3]); break;
  }
}

void switchLight(int v) {
  if(v == 0) {
    Serial.println("Switch the light off");
  }else {
    Serial.println("Switch the light on");
  }
  
  switchLED(v);
}

void switchLED(int value) {
    analogWrite(lightsPin, value);
}

void escSetup()
{
    motorL.attach(MOTOR_PIN_L);
    motorR.attach(MOTOR_PIN_R);

    motorL.writeMicroseconds(MIN_SIGNAL);
    motorR.writeMicroseconds(MIN_SIGNAL);
}

void escController(int DELAY)
{

    if(DELAY == 100)
    {
      escControlF();
    
    }  

    else if(DELAY == 101)
    {
      escControlL();
    }  

    else if(DELAY == 102)
    {
      escControlR();
    }  

    else
    {
      escControlStopper();
    }

    delay(1000);

    DELAY==0;
}

void escControlF()
{
      motorL.writeMicroseconds(POWER);
      motorR.writeMicroseconds(POWER);

      Serial.print("\n");

      Serial.println("Motor F");
}

void escControlL()
{
      motorL.writeMicroseconds(1000);
      motorR.writeMicroseconds(POWER);

      Serial.print("\n");

      Serial.println("Motor L"); 

}

void escControlR()
{
      motorL.writeMicroseconds(POWER);
      motorR.writeMicroseconds(1000);

      Serial.print("\n");

      Serial.println("Motor R"); 

}

void escControlStopper()
{
      motorL.writeMicroseconds(1000);
      motorR.writeMicroseconds(1000);

      Serial.println("Motor STOP"); 
}
