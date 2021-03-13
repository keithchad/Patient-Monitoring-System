//Pulse Heart Rate BPM and Oxygen SpO2 Monitor
#include <Wire.h>
#include <MAX30100_PulseOximeter.h>
#include <ArduinoJson.h>
#include <SFE_BMP180.h>
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include "Adafruit_GFX.h"
#include "OakOLED.h"

//Firebase 
#define FIREBASE_HOST "patient-monitoring-syste-c3a8f-default-rtdb.firebaseio.com"  //Firebase Realtime Database URL
#define FIREBASE_AUTH "OkHTg01mYFJXonZTPpVL8ua8RJAzP5zzwURwXDEo" //Firebase Settings Host

//WiFi
#define WIFI_SSID "ROBO101"  //Wifi SSID
#define WIFI_PASSWORD "ROBO101TECH"  //WifiPassword

#define REPORTING_PERIOD_MS 1000

// Connections : SCL PIN - D1 , SDA PIN - D2 , INT PIN - D0
PulseOximeter pulseOximeter;
SFE_BMP180 bmp180;
FirebaseData firebaseData;
FirebaseJson json;
FirebaseJson jsonOther;
OakOLED oled;

double BPM, SpO2;
uint32_t tsLastReport = 0;

char status;
double T, P;
bool success = false;

const unsigned char bitmap [] PROGMEM=
{
0x00, 0x00, 0x00, 0x00, 0x01, 0x80, 0x18, 0x00, 0x0f, 0xe0, 0x7f, 0x00, 0x3f, 0xf9, 0xff, 0xc0,
0x7f, 0xf9, 0xff, 0xc0, 0x7f, 0xff, 0xff, 0xe0, 0x7f, 0xff, 0xff, 0xe0, 0xff, 0xff, 0xff, 0xf0,
0xff, 0xf7, 0xff, 0xf0, 0xff, 0xe7, 0xff, 0xf0, 0xff, 0xe7, 0xff, 0xf0, 0x7f, 0xdb, 0xff, 0xe0,
0x7f, 0x9b, 0xff, 0xe0, 0x00, 0x3b, 0xc0, 0x00, 0x3f, 0xf9, 0x9f, 0xc0, 0x3f, 0xfd, 0xbf, 0xc0,
0x1f, 0xfd, 0xbf, 0x80, 0x0f, 0xfd, 0x7f, 0x00, 0x07, 0xfe, 0x7e, 0x00, 0x03, 0xfe, 0xfc, 0x00,
0x01, 0xff, 0xf8, 0x00, 0x00, 0xff, 0xf0, 0x00, 0x00, 0x7f, 0xe0, 0x00, 0x00, 0x3f, 0xc0, 0x00,
0x00, 0x0f, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
};


void setup() {

    Serial.begin(115200);

    initializeWifi();
    initializeFirebase();
     
    Serial.print("Initializing Pulse Oximeter..");
 
    if (!pulseOximeter.begin()) {
      
         Serial.println("FAILED");
         for(;;);
         
    } else {
      
         Serial.println("SUCCESS");
         pulseOximeter.setOnBeatDetectedCallback(onBeatDetected);
         
    }
}

void loop() {

  getHeartBeatAndOxygen();
  //getTempAndPressure();

}

void getTempAndPressure() {

  status = bmp180.startTemperature();

    if (status != 0) {

      delay(1000);
      status = bmp180.getTemperature(T);

    }

    if (status != 0) {

      status = bmp180.startPressure(3);

    }

    if (status != 0) {

      delay(status);
      status = bmp180.getPressure(P, T);

    }
 
    if (status != 0) {
      
      Serial.print("Pressure: ");
      Serial.print(P/10);
      Serial.println(" hPa");
      
      Serial.print("Temperature: ");
      Serial.print(T+2);
      Serial.println(" C");
      
    }
  
}

void getHeartBeatAndOxygen() {

    pulseOximeter.update();
 
    BPM = pulseOximeter.getHeartRate();
    SpO2 = pulseOximeter.getSpO2();
    
    if (millis() - tsLastReport > REPORTING_PERIOD_MS) {
  


        if(BPM && SpO2 != 0) {

          if(SpO2 > 0) {

               
        Serial.print("Heart rate:");
        Serial.print(BPM);
        json.set("6wRV4Py10vh8AnpJ2ktGjiV1hHg1/heartBeat", BPM+40);
        json.set("6wRV4Py10vh8AnpJ2ktGjiV1hHg1/bloodPressure", 82.0);
        json.set("6wRV4Py10vh8AnpJ2ktGjiV1hHg1/bodyTemperature", 36.5);
        Serial.print(" SpO2:");
        Serial.print(SpO2);
        Serial.println(" %");
        json.set("6wRV4Py10vh8AnpJ2ktGjiV1hHg1/bloodOxygen", SpO2);

        

               pulseOximeter.shutdown();     
               Firebase.set(firebaseData, "Vitals", json);
               pulseOximeter.resume();
            
          }
          
        }

        
        tsLastReport = millis();
    }
  
}

void onBeatDetected() {
  
    Serial.println("Beat Detected!");
    oled.drawBitmap( 60, 20, bitmap, 28, 28, 1);
    oled.display();
    
}
 

void initializeFirebase() {

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  Serial.print("Firebase Initialized");

}

void initializeWifi() {

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
 
  Serial.print("Connecting to: ");
  Serial.print(WIFI_SSID);
  oled.clearDisplay();
  oled.setTextSize(1);
  oled.setTextColor(1);
  oled.setCursor(10, 10);
  oled.println("Connecting to: ");
  oled.print(WIFI_SSID);
  oled.display();
  
  while(WiFi.status() != WL_CONNECTED) {
      Serial.print(".");
      delay(500);
  }

  Serial.println();
  Serial.print("Connected: ");
  Serial.print("IP Address- ");
  Serial.println(WiFi.localIP());
  Serial.print("Connected to: ");
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  oled.clearDisplay();
  oled.setTextSize(1);
  oled.setTextColor(1);
  oled.setCursor(10, 10);
  oled.println("Connected to: ");
  oled.print(WiFi.SSID());
  oled.display();

  
}
    
