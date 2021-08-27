#define BTserial Serial1

const int START_COMMAND = 0x1;
int buff[10];
int currPos = 0;


void initBluetooth() {
  BTserial.begin(9600);
  Serial.begin(9600);

  sendCommand("AT"); //enable AT Commands
  sendCommand("AT+TYPE0"); // WO pin
  sendCommand("AT+MODE2");
  sendCommand("AT+ROLE0"); //slave
  sendCommand("AT+UUIDFFE0"); // Default UID
  sendCommand("AT+CHARFFE1"); // Default Characteristic ID
  sendCommand("AT+NAMEAGGOS"); //Name AGGOS
}

void sendCommand(const char * command) {
  Serial.print("Command send :");
  Serial.println(command);
  BTserial.println(command);
  //wait some time
  delay(100);

  char reply[100];
  int i = 0;
  while (Serial1.available()) {
    reply[i] = BTserial.read();
    i += 1;
  }
  //end the string
  reply[i] = '\0';
  Serial.print(reply);
  Serial.println("\nReply end");
  delay(100);
}

// Used for manual setup
void updateSerial() {
  if (Serial1.available())       
   Serial.write(BTserial.read()); 

  if (Serial.available())     
    BTserial.write(Serial.read());
}

// Used for proximity sensor
void checkBin(){
  
  proxSensor();
  
  }

void readCommand() {
  while (BTserial.available()) {
    int nc = BTserial.read();
    if (currPos > 0 || nc == START_COMMAND)
      parseNext(nc);
  }
}

void parseNext(int next) {
  buff[currPos++] = next;
  if (isCommandFullyRead()) {
    executeCommand(buff);
    currPos = 0;
  }
}

bool isCommandFullyRead() {
  return currPos > 2             && // command header is 3 bytes (START, Command type, num of parameters)
         buff[2] == currPos - 3;    // are all parameters already read
}
