#include <MsTimer2.h>
#include <SoftwareSerial.h> 
#include <string.h>
#include <Servo.h> 

#define RxD 6
#define TxD 7

#define DEBUG_ENABLED  1

SoftwareSerial blueToothSerial(RxD,TxD); 

Servo servo_speed, servo_direction;

void setup() 
{ 
  pinMode(13, OUTPUT);
  Serial.begin(9600);
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBlueToothConnection();
  servo_direction.attach(9);
  servo_speed.attach(8);
  MsTimer2::set(1000, timeout);
  MsTimer2::start();
} 

boolean is_valid_value(char value) {
  if (((int)value >= 0) && ((int)value <= 255))
    return true;
  return false;
}

void loop() 
{ 
  char trash;
  char cspeed;
  char cdirection;

  while(1){
    //blueToothSerial.flush();
    //if (blueToothSerial.available() != 2)
    // return;
    if(!blueToothSerial.available())
      return; //check if there's any data sent from the remote bluetooth shield
    
    cspeed = blueToothSerial.read();
    cdirection = blueToothSerial.read();
    
    if ((is_valid_value(cspeed)) && (is_valid_value(cdirection))) {
      Serial.print("Speed: ");
      Serial.println(cspeed, DEC);
      Serial.print("Direction: ");
      Serial.println(cdirection, DEC);
    
      servo_direction.write(cdirection);
      servo_speed.write(cspeed);
      MsTimer2::count = 0;
    }
    
    delay(1);
  }
} 

void setupBlueToothConnection()
{
  blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
  Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  blueToothSerial.flush();
}

void timeout() {
  static boolean output = HIGH;

  digitalWrite(13, output);
  output = !output;
  
  servo_direction.write(128);
  servo_speed.write(90);  
}
