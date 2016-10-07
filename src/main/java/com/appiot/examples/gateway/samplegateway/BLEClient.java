package main.java.com.appiot.examples.gateway.samplegateway;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

public class BLEClient {
    private final Logger logger = Logger.getLogger(this.getClass().getName()); 

    private String macAddress;
    
    private BluetoothDevice sensor;
    private BluetoothGattCharacteristic characteristic;

    private boolean connected;

    public BLEClient(String macAddress) {
        this.macAddress = macAddress;
        connected = false;
    }

    public boolean connect(String macAddress) {
        connected = false;
        BluetoothManager manager = BluetoothManager.getBluetoothManager();

        boolean discoveryStarted = manager.startDiscovery();

        logger.log(Level.INFO, "The discovery started: " + (discoveryStarted ? "true" : "false"));
        BluetoothDevice sensor;
        try {
            sensor = getDevice(macAddress);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            return connected;
        }

        try {
            manager.stopDiscovery();
        } catch (BluetoothException e) {
            logger.log(Level.WARNING, "Discovery could not be stopped.");
        }

        if (sensor == null) {
            logger.log(Level.SEVERE, "No sensor found with the provided address.");
            return connected;
        }

        if (sensor.connect())
            logger.log(Level.INFO, "Sensor with the provided address connected");
        else {
            logger.log(Level.SEVERE, "Could not connect device.");
            return connected;
        }
        this.sensor = sensor;
        connected = true;
        return connected;
    }

    public boolean isConnected() {
        return connected;
    }
    
    public void disconnect() {
        connected = false;
        if(sensor != null) {
            sensor.disconnect();
        }
    }

    public boolean setCharacteristic(String serviceId, String characteristicId) throws InterruptedException, GatewayException {
        if (sensor == null) {
                connected = connect(macAddress);
                if(!connected) {
                        throw new GatewayException("Could not connect to address: " + macAddress);
                }
        }

        BluetoothGattService service = getService(sensor, serviceId);

        if (service == null) {
            logger.log(Level.SEVERE, "This device does not have the service we are looking for.");
            sensor.disconnect();
            return false;
        }
        logger.log(Level.INFO, "Found service " + service.getUUID());

        BluetoothGattCharacteristic characteristic = getCharacteristic(service, characteristicId);

        if (characteristic == null) {
            logger.log(Level.SEVERE, "Could not find the correct characteristics.");
            sensor.disconnect();
            return false;
        }

        logger.log(Level.INFO, "Found the temperature characteristics");
        this.characteristic = characteristic;
        return true;
    }

    public double readDouble() throws GatewayException {
            if(characteristic == null) {
                throw new GatewayException("Please set a characteristic before trying to read.");
            }
            byte[] raw = characteristic.readValue();
            return ByteBuffer.wrap(raw).getDouble();
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
        BluetoothGattService service = null;
        List<BluetoothGattService> bluetoothServices = null;
        do {
            bluetoothServices = device.getServices();
            if (bluetoothServices == null)
                return null;

            for (BluetoothGattService potentialService : bluetoothServices) {
                logger.log(Level.INFO, "UUID: " + potentialService.getUUID());
                if (potentialService.getUUID().equals(UUID))
                    service = potentialService;
            }
            Thread.sleep(4000);
        } while (bluetoothServices.isEmpty());
        return service;
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
}
