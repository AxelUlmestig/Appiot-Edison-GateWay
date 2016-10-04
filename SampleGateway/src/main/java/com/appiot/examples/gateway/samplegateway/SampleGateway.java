package com.appiot.examples.gateway.samplegateway;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.sigma.sensation.gateway.sdk.client.SensationClient;

public class SampleGateway {
    private final Logger logger = Logger.getLogger(this.getClass().getName()); 

    private SamplePlatform platform;
    private SensationClient sensationClient;

    public static void main(String[] args) {
        SampleGateway gateway = new SampleGateway();
        gateway.start();
    }

    private void start() {      
        logger.log(Level.INFO, "Sample Gateway starting up.");
        platform = new SamplePlatform();        
        sensationClient = new SensationClient(platform); 
        sensationClient.start();
        
        String macAddress = "XX:XX:XX:XX";
        String serialNumber = macAddress;
        int sensorHardwareTypeId = -1;

        TemperatureSensor tempSensor = new TemperatureSensor(sensationClient, serialNumber, sensorHardwareTypeId);
        boolean connected = tempSensor.connect(macAddress);
        
        if(connected) {
            new Thread(tempSensor).start();
            
            Scanner keyboard = new Scanner(System.in);
            System.out.println("*** Press ENTER to stop reading. ***");
            @SuppressWarnings("unused")
            String line = keyboard.nextLine();
            keyboard.close();
            
            tempSensor.stopReading();
        } else {
            logger.log(Level.SEVERE, "Could not connect to device.");
        }
        sensationClient.stop();
        logger.log(Level.INFO, "Shutting down.");
    }
    
}
