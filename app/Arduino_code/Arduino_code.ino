//Pulse Heart Rate BPM and Oxygen SpO2 Monitor
#include <Wire.h>
#include "MAX30100_PulseOximeter.h"
#include <SFE_BMP180.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "Wire.h"

#define REPORTING_PERIOD_MS 1000
#define BLYNK_PRINT Serial

//WIFI CONFIG
char auth[] = "--------------------";             // Authentication Token Sent by Blynk
char ssid[] = "--------";        //WiFi SSID
char pass[] = "--------";        //WiFi Password

// Connections : SCL PIN - D1 , SDA PIN - D2 , INT PIN - D0
PulseOximeter pox;
SFE_BMP180 bmp180;

float BPM, SpO2;
uint32_t tsLastReport = 0;

void onBeatDetected()
{

}

void setup()
{
    Serial.begin(115200);

    pinMode(16, OUTPUT);
    Serial.print("Initializing Pulse Oximeter..");

    if (!pox.begin()) {

         Serial.println("FAILED");
         for(;;);

    } else {

         Serial.println("SUCCESS");
         pox.setOnBeatDetectedCallback(onBeatDetected);

    }

    Serial.begin(115200);

    bool success = bmp180.begin();

    if (success) {

        Serial.println("BMP180 init success");

    }

    // The default current for the IR LED is 50mA and it could be changed by uncommenting the following line.
     //pox.setIRLedCurrent(MAX30100_LED_CURR_7_6MA);

}

void loop() {
    pox.update();

    BPM = pox.getHeartRate();
    SpO2 = pox.getSpO2();

    if (millis() - tsLastReport > REPORTING_PERIOD_MS) {

        Serial.print("Heart rate:");
        Serial.print(BPM);
        Serial.print(" SpO2:");
        Serial.print(SpO2);
        Serial.println(" %");

        tsLastReport = millis();

    }
}

void getTemperatureAndPressure() {
        char status;
        double T, P;
        bool success = false;

        status = bmp180.startTemperature();
  if (status != 0) {
      delay(1000);
      status = bmp180.getTemperature(T);
  if (status != 0) {
      status = bmp180.startPressure(3);
  if (status != 0) {
      delay(status);
      status = bmp180.getPressure(P, T);
  if (status != 0) {
      Serial.print("Pressure: ");
      Serial.print(P);
      Serial.println(" hPa");
      Serial.print("Temperature: ");
      Serial.print(T);
      Serial.println(" C");
      
        }
      }
    }
  }
}
