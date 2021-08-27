const int SWITCH_LED = 0x4;

const int lightsPin =  13;

const initController() {
  pinMode(lightsPin, OUTPUT);
}

void executeCommand(int cmd[]) {
  switch(cmd[1]) {
    case SWITCH_LED: switchLight(cmd[3]); break;
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
