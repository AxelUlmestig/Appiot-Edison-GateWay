package main.java.com.appiot.examples.gateway.samplegateway;
//package com.appiot.examples.gateway.samplegateway;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.sigma.sensation.gateway.sdk.client.SensationClient;
import se.sigma.sensation.gateway.sdk.client.SensorMeasurementAcknowledge;
import se.sigma.sensation.gateway.sdk.client.data.ISensorMeasurement;

public class EdisonGateway {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private SamplePlatform platform;
    private SensationClient sensationClient;

    public static void main(String[] args) {
        EdisonGateway gateway = new EdisonGateway();
        gateway.start();
    }

    private void start() {
        logger.log(Level.INFO, "Sample Gateway starting up.");
        platform = new SamplePlatform();
        sensationClient = new SensationClient(platform);
        sensationClient.start();

        final String macAddress = "";
        final String deviceId = "";

        final int sensorHardwareTypeId = 1; // 1 is for temperature
        final String serviceId = "0000181a-0000-1000-8000-00805f9b34fb";
        final String characteristicId = "00002a6e-0000-1000-8000-00805f9b34fb";

        final BLEClient bleClient = new BLEClient(macAddress);
        boolean connected = false;
        try {
            connected = bleClient.setCharacteristic(serviceId, characteristicId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (GatewayException e) {
            e.printStackTrace();
        }
        if (connected) {
            new Thread(new Runnable() {
                public void run() {
                    while (bleClient.isConnected()) {
                        try {
                            double temperature;
                            temperature = bleClient.readDouble();
                            logger.log(Level.INFO, "Measured " + temperature + " degrees Celsius.");
                            Measurement measurement = new Measurement(sensorHardwareTypeId, deviceId, temperature);
                            sensationClient.sendSensorMeasurement(measurement);
			    Thread.sleep(1000);
                        } catch (GatewayException e) {
                            bleClient.disconnect();
                            e.printStackTrace();
                        } catch (InterruptedException e) {
			    bleClient.disconnect();
			    e.printStackTrace();
			}
                    }
                }
            }).start();

            Scanner keyboard = new Scanner(System.in);
            System.out.println("*** Press ENTER to stop reading. ***");
            @SuppressWarnings("unused")
            String line = keyboard.nextLine();
            keyboard.close();

            bleClient.disconnect();
        } else {
            logger.log(Level.SEVERE, "Could not connect to device.");
        }
        sensationClient.stop();
        logger.log(Level.INFO, "Shutting down.");
    }

    private class Measurement implements ISensorMeasurement {
        private SensorMeasurementAcknowledge ack;
        private int sensorHardwareTypeId;
        private String serialNumber;
        private double value;

        public Measurement(int sensorHardwareTypeId, String serialNumber, double value) {
            this.sensorHardwareTypeId = sensorHardwareTypeId;
            this.serialNumber = serialNumber;
            this.value = value;
        }

        public SensorMeasurementAcknowledge getAcknowledge() {
            return this.ack;
        }

        public int getSensorHardwareTypeId() {
            return sensorHardwareTypeId;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public long getUnixTimestampUTC() {
            return System.currentTimeMillis();
        }

        public double[] getValue() {
            double[] valueArr = { value };
            return valueArr;
        }

        public void setAcknowledge(SensorMeasurementAcknowledge arg0) {
            this.ack = arg0;
        }

    }
}
