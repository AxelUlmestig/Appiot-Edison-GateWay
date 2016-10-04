package com.appiot.examples.gateway.samplegateway;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.sigma.sensation.gateway.sdk.client.SensationClient;
import se.sigma.sensation.gateway.sdk.client.SensorMeasurementAcknowledge;
import se.sigma.sensation.gateway.sdk.client.data.ISensorMeasurement;
import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

public class TemperatureSensor implements Runnable {
    private final Logger logger = Logger.getLogger(this.getClass().getName()); 
    private static final String TEMP_SERVICE_ID = "0000181a-0000-1000-8000-00805f9b34fb";
    private static final String TEMP_CHARACTERISTIC_ID = "00002a6e-0000-1000-8000-00805f9b34fb";
    
    private final String serialNumber;
    private final int sensorHardwareTypeId;

    private SensationClient client;
    private BluetoothDevice sensor;
    private boolean connected;

    private boolean reading = true;

    public TemperatureSensor(SensationClient client, String serialNumber, int sensorHardwareTypeId) {
        this.client = client;
        this.serialNumber = serialNumber;
        this.sensorHardwareTypeId = sensorHardwareTypeId;
        connected = false;
    }

    public void run() {
        try {
            startReading();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SensorException e) {
            e.printStackTrace();
        }
    }

    public boolean connect(String macAddress) {
        BluetoothManager manager = BluetoothManager.getBluetoothManager();

        boolean discoveryStarted = manager.startDiscovery();

        logger.log(Level.INFO, "The discovery started: " + (discoveryStarted ? "true" : "false"));
        BluetoothDevice sensor;
        try {
            sensor = getDevice(macAddress);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            return false;
        }

        try {
            manager.stopDiscovery();
        } catch (BluetoothException e) {
            logger.log(Level.WARNING, "Discovery could not be stopped.");
        }

        if (sensor == null) {
            logger.log(Level.SEVERE, "No sensor found with the provided address.");
            return false;
        }

        if (sensor.connect())
            logger.log(Level.INFO, "Sensor with the provided address connected");
        else {
            logger.log(Level.SEVERE, "Could not connect device.");
            return false;
        }
        this.sensor = sensor;
        connected = true;
        return connected;
    }

    public void startReading() throws InterruptedException, SensorException {
        if (!connected)
            throw new SensorException("Not connected, call function connect(macAdress) first.");

        BluetoothGattService tempService = getService(sensor, TEMP_SERVICE_ID);

        if (tempService == null) {
            logger.log(Level.SEVERE, "This device does not have the temperature service we are looking for.");
            sensor.disconnect();
            return;
        }
        logger.log(Level.INFO, "Found service " + tempService.getUUID());

        BluetoothGattCharacteristic tempValue = getCharacteristic(tempService, TEMP_CHARACTERISTIC_ID);

        if (tempValue == null) {
            logger.log(Level.SEVERE, "Could not find the correct characteristics.");
            sensor.disconnect();
            return;
        }

        logger.log(Level.INFO, "Found the temperature characteristics");
        while (reading) {
            byte[] tempRaw = tempValue.readValue();
            double temp = ByteBuffer.wrap(tempRaw).getDouble();
            logger.log(Level.INFO, "Measured " + temp + " degrees Celsius.");

            Measurement measurement = new Measurement(sensorHardwareTypeId, serialNumber, temp);
            client.sendSensorMeasurement(measurement);

            Thread.sleep(1000);
        }
        sensor.disconnect();
        logger.log(Level.INFO, "Sensor disconnected.");
    }

    public void stopReading() {
        this.reading = false;
        logger.log(Level.INFO, "stopReading called.");
    }

    private BluetoothDevice getDevice(String address) throws InterruptedException {
        BluetoothManager manager = BluetoothManager.getBluetoothManager();
        BluetoothDevice sensor = null;
        int attemptsLimit = 5;
        for (int i = 1; i <= attemptsLimit; ++i) {
            List<BluetoothDevice> list = manager.getDevices();
            if (list == null)
                return null;

            for (BluetoothDevice device : list) {
                if (device.getAddress().equals(address))
                    sensor = device;
            }

            if (sensor != null) {
                return sensor;
            }
            Thread.sleep(4000);
            logger.log(Level.INFO, "Device not found, " + (attemptsLimit - i) + " attempts remaining.");
        }
        return null;
    }

    private BluetoothGattService getService(BluetoothDevice device, String UUID) throws InterruptedException {
        logger.log(Level.INFO, "Services exposed by device:");
        BluetoothGattService tempService = null;
        List<BluetoothGattService> bluetoothServices = null;
        do {
            bluetoothServices = device.getServices();
            if (bluetoothServices == null)
                return null;

            for (BluetoothGattService service : bluetoothServices) {
                logger.log(Level.INFO, "UUID: " + service.getUUID());
                if (service.getUUID().equals(UUID))
                    tempService = service;
            }
            Thread.sleep(4000);
        } while (bluetoothServices.isEmpty());
        return tempService;
    }

    static BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String UUID) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        if (characteristics == null)
            return null;

        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUUID().equals(UUID))
                return characteristic;
        }
        return null;
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
