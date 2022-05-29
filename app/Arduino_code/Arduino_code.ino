
/**
 * Created by Keith Chad
 *
 * Email: keithchad10@outlook.com
 *
 * Github: https://github.com/keithchad
 *
 * Copyright (c) 2022 Chad
 *
 */


//Pulse Heart Rate BPM and Oxygen SpO2 Monitor
#include <Wire.h>
#include <MAX30100_PulseOximeter.h>
#include <ArduinoJson.h>
#include <SFE_BMP180.h>
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include "Adafruit_GFX.h"
#include "OakOLED.h"

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

//WiFi
#define WIFI_SSID "Jotawi" //WiFi SSID
#define WIFI_PASSWORD "sonditi@20000009" //WiFi Password

//Define the API Key
#define API_KEY "AIzaSyCG-79KpLAvW4MDKvDTh9FVk0h5aSR7mW8"

//Define the RealtimeDatabase URL
#define DATABASE_URL "patient-monitoring-syste-c3a8f-default-rtdb.firebaseio.com/" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app

//Define the user Email and password
#define USER_EMAIL "firebaseiot@gmail.com"
#define USER_PASSWORD "369070"

// Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;

unsigned long count = 0;

#define REPORTING_PERIOD_MS 1000

// Connections : SCL PIN - D1 , SDA PIN - D2 , INT PIN - D0
PulseOximeter pulseOximeter;
SFE_BMP180 bmp180;
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
                Serial.print(" SpO2:");
                Serial.print(SpO2);
                Serial.println(" %");


               pulseOximeter.shutdown();
                if (Firebase.ready() && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)){
                  
                       sendDataPrevMillis = millis();
                  
                       Serial.println("Sending Data to Firebase");
                       Firebase.setInt(fbdo, F("/Vitals/yLQykTjwzWYVzPvPIaAM4riDdDi1/heartBeat"), BPM+40);
                       Firebase.setInt(fbdo, F("/Vitals/yLQykTjwzWYVzPvPIaAM4riDdDi1/bloodPressure"), 82.0);
                       Firebase.setInt(fbdo, F("/Vitals/yLQykTjwzWYVzPvPIaAM4riDdDi1/bodyTemperature"), 36.5);
                       Firebase.setInt(fbdo, F("/Vitals/yLQykTjwzWYVzPvPIaAM4riDdDi1/bloodOxygen"), SpO2);
                       
               }
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

  //Display Firebase Client Version
  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  //Assign the api key
  config.api_key = API_KEY;

  //Assign the user sign in credentials
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  //Assign the RTDB URL
  config.database_url = DATABASE_URL;

  //Assign the callback function for the long running token generation task
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h
  
  //Begin INtialization
  Firebase.begin(&config, &auth);

  // Comment or pass false value when WiFi reconnection will control by your code or third party library
  Firebase.reconnectWiFi(true);

  Firebase.setDoubleDigits(5);

  //WiFi reconnect timeout (interval) in ms (10 sec - 5 min) when WiFi disconnected.
  config.timeout.wifiReconnect = 10 * 1000;

}

void initializeWifi() {

  //Intialize WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
 
  Serial.print("Connecting to: ");
  Serial.print(WIFI_SSID);

  while(WiFi.status() != WL_CONNECTED) {
      delay(500);
  }

  Serial.println();
  Serial.print("Connected: ");
  Serial.print("IP Address- ");
  Serial.println(WiFi.localIP());
  Serial.print("Connected to: ");
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

}
    
