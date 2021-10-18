void setup()
{
  initBluetooth();
  gearmotorSetup();
  escSetup();
}

void loop()
{
  //FOR TESTING
  //updateSerial();
  
  //MAIN
  readCommand();
  checkMainBatt();  
  // sendCommand("AT+STATE"); //STATE AGGOS

  //uncomment for testing
  // escControlF();
  // conveyorON();
}
