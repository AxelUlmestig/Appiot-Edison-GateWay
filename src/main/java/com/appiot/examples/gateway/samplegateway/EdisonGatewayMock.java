package main.java.com.appiot.examples.gateway.samplegateway;
//package com.appiot.examples.gateway.samplegateway;

import java.util.logging.Level;
import java.util.logging.Logger;

import se.sigma.sensation.gateway.sdk.client.SensationClient;
import se.sigma.sensation.gateway.sdk.client.SensorMeasurementAcknowledge;
import se.sigma.sensation.gateway.sdk.client.data.ISensorMeasurement;

public class EdisonGatewayMock {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private SamplePlatform platform;
    private SensationClient sensationClient;

    public static void main(String[] args) {
        EdisonGatewayMock gateway = new EdisonGatewayMock();
        gateway.start();
    }

    /*
     * This will send the temperature measurement value 1337 to
     * Appiot once.
     */
    private void start() {
        logger.log(Level.INFO, "Sample Gateway starting up.");
        platform = new SamplePlatform();
        sensationClient = new SensationClient(platform);
        sensationClient.start();

        final String deviceId = "";
        final int sensorHardwareTypeId = 1; // 1 is for temperature

        try {
            double temperature = 1337;
            logger.log(Level.INFO, "Measured " + temperature + " degrees Celsius.");
            Measurement measurement = new Measurement(sensorHardwareTypeId, deviceId, temperature);
            sensationClient.sendSensorMeasurement(measurement);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
