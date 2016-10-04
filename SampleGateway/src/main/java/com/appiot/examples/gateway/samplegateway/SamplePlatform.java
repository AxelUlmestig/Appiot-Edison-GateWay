/**
 * Sample platform implementation 
 */
package com.appiot.examples.gateway.samplegateway;
 
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import se.sigma.sensation.gateway.sdk.client.Platform;
import se.sigma.sensation.gateway.sdk.client.PlatformInitialisationException;
import se.sigma.sensation.gateway.sdk.client.SensationClient;
import se.sigma.sensation.gateway.sdk.client.core.SensationClientProperties;
import se.sigma.sensation.gateway.sdk.client.data.DataCollectorDeleteResponseCode;
import se.sigma.sensation.gateway.sdk.client.data.DataCollectorStatus;
import se.sigma.sensation.gateway.sdk.client.data.DataCollectorStatusCode;
import se.sigma.sensation.gateway.sdk.client.data.ISensorMeasurement;
import se.sigma.sensation.gateway.sdk.client.data.NetworkSetting;
import se.sigma.sensation.gateway.sdk.client.data.NetworkSettingResponseCode;
import se.sigma.sensation.gateway.sdk.client.data.RebootResponseCode;
import se.sigma.sensation.gateway.sdk.client.data.RestartApplicationResponseCode;
import se.sigma.sensation.gateway.sdk.client.data.SensorCollectionRegistrationResponseCode;
import se.sigma.sensation.gateway.sdk.client.data.UpdatePackage;
import se.sigma.sensation.gateway.sdk.client.data.UpdatePackageResponseCode;
import se.sigma.sensation.gateway.sdk.client.registry.SensorCollectionRegistration;
import se.sigma.sensation.gateway.sdk.client.registry.SensorCollectionRegistry;
 
public class SamplePlatform implements Platform {
 
    private final Logger logger = Logger.getLogger(this.getClass().getName()); 
     
    /**
     * Called when measurements are sent to Sensation telling if sensing the
     * measurements was successful or not.
     * 
     * @param measurementsSent
     *            - A list of measurements with acknowledge parameter set.
     */
    public void acknowledgeMeasurementsSent(List<ISensorMeasurement> arg0) {
        logger.log(Level.INFO, "called");
    }
 
    /**
     * Called from Sensation requesting the data collector to add wifi setting.
     * 
     * @param networkSetting
     *            the wifi setting to add.
     * @return NetworkSettingResponseCode
     */
    public NetworkSettingResponseCode addNetworkSetting(NetworkSetting arg0) {
        logger.log(Level.INFO, "called");
        return NetworkSettingResponseCode.OK;
    }
 
    /**
     * Called from Sensation requesting the data collector to be deleted.
     * 
     * @param forceDelete
     *            - If true, ignore errors.
     * @return DataCollectorDeleteResponseCode
     */
    public DataCollectorDeleteResponseCode deleteDataCollector(boolean arg0) {
        logger.log(Level.INFO, "called");
        return DataCollectorDeleteResponseCode.OK;
    }
 
    /**
     * Called from Sensation when using custom commands.
     * 
     * @param correlationId
     *            - Id to identify this request, use id when responding back to
     *            Sensation.
     * @param actorId
     *            - The id of the microservice in Sensation.
     * @param payloadType
     *            - Indicates what kind of payload.
     * @param payload
     *            - The payload handle.
     */
    public void handleCustomCommand(String arg0, String arg1, String arg2, String arg3) {
        logger.log(Level.INFO, "called");
    }
 
    /**
     * Initializes the platform.
     * 
     * @param client
     *            - SensationClient to be used by the platform.
     * @throws PlatformInitialisationException
     *             - Indicates platform initialization failure.
     */
    public void init(SensationClient arg0) throws PlatformInitialisationException {
        logger.log(Level.INFO, "called");
    }
 
    /**
     * Called from Sensation requesting the system to reboot.
     * 
     * @return RebootResponseCode
     */
    public RebootResponseCode reboot() {
        logger.log(Level.INFO, "called");
        return RebootResponseCode.OK;
    }
 
    /**
     * Request from Sensation to report sensor collections in range of the data
     * collector. The platform is expected to report sensor collections
     * available.
     * 
     * @param correlationId
     *            - The id of the request, use when answering.
     * @see DiscoveredSensorCollection.
     * @see SensationClient.reportDicoveredSensorCollection().
     */
    public void reportDiscoveredSensorCollections(String arg0) {
        logger.log(Level.INFO, "called");
    }
 
    /**
     * Called from Sensation requesting the application to restart.
     * 
     * @return RestartApplicationResponseCode
     */
    public RestartApplicationResponseCode restartApplication() {
        logger.log(Level.INFO, "called");
        return RestartApplicationResponseCode.OK;
    }
 
    /**
     * Called when a sensor collection has been registered. The Sensor
     * Collection is already registered in the SensorCollectionRegistry. This
     * call is for the platform to act if necessary.
     * 
     * @see SensorCollectionRegistry
     * @param registration
     *            - A reference to the entry in SensorCollectionRegistry.
     * @return SensorCollectionRegistrationResponseCode
     */
    public SensorCollectionRegistrationResponseCode sensorCollectionRegistrationCreated(
            SensorCollectionRegistration arg0) {
        logger.log(Level.INFO, "called");
        return SensorCollectionRegistrationResponseCode.ADD_OK;
    }
 
    /**
     * Called when a sensor collection that is already registered has been
     * removed. The Sensor Collection is already removed in the
     * SensorCollectionRegistry. This call is for the platform to act if
     * necessary.
     * 
     * @see SensorCollectionRegistry
     * @param registration
     *            - A reference to the entry in SensorCollectionRegistry.
     * @return SensorCollectionRegistrationResponseCode - Indicating
     */
    public SensorCollectionRegistrationResponseCode sensorCollectionRegistrationDeleted(
            SensorCollectionRegistration arg0) {
        logger.log(Level.INFO, "called");
        return SensorCollectionRegistrationResponseCode.DELETE_OK;
    }
 
    /**
     * Called when a sensor collection that is already registered has been
     * updated. The Sensor Collection is already updated in the
     * SensorCollectionRegistry. This call is for the platform to act if
     * necessary.
     * 
     * @see SensorCollectionRegistry
     * @param registration
     *            - A reference to the entry in SensorCollectionRegistry.
     * @return SensorCollectionRegistrationResponseCode - Indicating
     */
    public SensorCollectionRegistrationResponseCode sensorCollectionRegistrationUpdated(
            SensorCollectionRegistration arg0) {
        logger.log(Level.INFO, "called");
        return SensorCollectionRegistrationResponseCode.ADD_OK;
    }
 
 
    /**
     * Called when Sensation distributes a FOTA update package for application
     * update.
     * 
     * @param updatePackage
     *            The update package.
     * @return UpdatePackageResponseCode - Indicating result of update process.
     * @see UpdatePackage
     * @see UpdatePackageResponseCode
     */
    public UpdatePackageResponseCode updateApplication(UpdatePackage arg0) {
        logger.log(Level.INFO, "called");
        return UpdatePackageResponseCode.OK;
    }
 
 
    /**
     * Called from Sensation when settings for a data collector is updated.
     * @param properties @see {@link SensationClientProperties}
     */
    public void updateDataCollectorSettings(SensationClientProperties arg0) {
        logger.log(Level.INFO, "called");
    }
 
    /**
     * Called from Sensation requesting an update of the status of the data
     * collector.
     * 
     * @return DataCollectorStatus populated with the current status of the data
     *         collector.
     */
    public DataCollectorStatus updateDataCollectorStatus() {
        logger.log(Level.INFO, "called");
        DataCollectorStatus dcs = new DataCollectorStatus();
        dcs.setStatus(DataCollectorStatusCode.OK);
        return dcs;
    }
 
    /**
     * Called when Sensation distributes a FOTA update package for sensor
     * collection update.
     * 
     * @param registration
     *            - The registered sensor collection to update.
     * @param updatePackage
     *            The update package.
     * @return UpdatePackageResponseCode - Indicating result of update process.
     * @see UpdatePackage
     * @see UpdatePackageResponseCode
     */
     
    public UpdatePackageResponseCode updateSensorCollection(SensorCollectionRegistration arg0, UpdatePackage arg1) {
        logger.log(Level.INFO, "called");
        return UpdatePackageResponseCode.OK;
    }
 
    /**
     * A request to update the current status of a sensor collection
     * registration.
     * 
     * @param regisration
     *            - The registration to update containing registered values.
     * 
     * @return The updated SensorCollectionRegistration.
     * @see SensorCollectionRegistration
     */
    public SensorCollectionRegistration updateSensorCollectionStatus(SensorCollectionRegistration arg0) {
        logger.log(Level.INFO, "called");
        return null;
    }
 
    /**
     * Called when Sensation distributes a FOTA update package for system
     * update.
     * 
     * @param updatePackage
     *            The update package.
     * @return UpdatePackageResponseCode - Indicating result of update process.
     * @see UpdatePackage
     * @see UpdatePackageResponseCode
     */
    public UpdatePackageResponseCode updateSystem(UpdatePackage arg0) {
        logger.log(Level.INFO, "called");
        return UpdatePackageResponseCode.OK;
    }
 
}