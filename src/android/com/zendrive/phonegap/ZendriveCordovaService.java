package com.zendrive.phonegap;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.LocationPoint;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveDriverAttributes;
import com.zendrive.sdk.ZendriveDriverAttributes.ServiceLevel;
import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.ZendriveOperationResult;

/**
 * New Imports per PDF 2016-06-22
 */
import com.zendrive.sdk.ZendriveOperationCallback;
import com.zendrive.sdk.ZendriveIntentService;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by PDF (2016-06-22)
 */
public class ZendriveCordovaService extends ZendriveIntentService {

    protected ZendriveCordovaService(String name) {
        super(name);
    }

    protected void setup(JSONArray args, final CallbackContext callbackContext, CordovaInterface cordova )
            throws JSONException {
        JSONObject configJsonObj = args.getJSONObject(0);
        if (configJsonObj == null) {
            callbackContext.error("Wrong configuration supplied");
            return;
        }

        String applicationKey = getApplicationKey(configJsonObj);
        String driverId = getDriverId(configJsonObj);

        String driveDetectionMode;
        ZendriveDriveDetectionMode mode;
        if (hasValidValueForKey(configJsonObj, kDriveDetectionModeKey)) {
            driveDetectionMode = configJsonObj.getString(kDriveDetectionModeKey);

            try {
                mode = ZendriveDriveDetectionMode.valueOf(driveDetectionMode);
            }
            catch (IllegalArgumentException err) {
                callbackContext.error("Invalid Drive Detection Mode Supplied");
                return;
            }
        }
        else {
            callbackContext.error("No drive detection mode supplied");
            return;
        }

        ZendriveConfiguration configuration = new ZendriveConfiguration(applicationKey, driverId,
                mode);

        ZendriveDriverAttributes driverAttributes = getDriverAttrsFromJsonObject(configJsonObj);
        if (driverAttributes != null) {
            configuration.setDriverAttributes(driverAttributes);
        }

        // setup Zendrive SDK
        Zendrive.setup(
                cordova.getActivity().getApplicationContext(),
                configuration, ZendriveCordovaService.class,

                new ZendriveOperationCallback() {
                    @Override
                    public void onCompletion(ZendriveOperationResult result) {
                        if (result.isSuccess()) {
                            callbackContext.success();
                        } else {
                            callbackContext.error(result.getErrorMessage());
                        }
                    }
                });
    }

    protected void teardown(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.teardown(
                new ZendriveOperationCallback() {
                    @Override
                    public void onCompletion(ZendriveOperationResult result) {
                        if (result.isSuccess()) {
                            callbackContext.success();
                        } else {
                            callbackContext.error(result.getErrorMessage());
                        }
                    }
                });
    }

    protected void startDrive(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.startDrive(args.getString(0));
        callbackContext.success();
    }

    protected void stopDrive(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.stopDrive(args.getString(0));
        callbackContext.success();
    }

    protected void startSession(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.startSession(args.getString(0));
        callbackContext.success();
    }

    protected void stopSession(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.stopSession();
        callbackContext.success();
    }

    protected void setDriveDetectionMode(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {

        String driveDetectionMode = args.getString(0);
        ZendriveDriveDetectionMode mode;


        if (!isNull(driveDetectionMode)) {
            try {
                mode = ZendriveDriveDetectionMode.valueOf(driveDetectionMode);
            }
            catch (IllegalArgumentException err) {
                callbackContext.error("Invalid Drive Detection Mode Supplied");
                return;
            }
        }
        else {
            callbackContext.error("No drive detection mode supplied");
            return;
        }
        
        Zendrive.setZendriveDriveDetectionMode(mode);
        callbackContext.success();
    }

    protected void setProcessStartOfDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        if (null != this.processStartOfDriveCallback) {
            // Delete old callback
            // Sending NO_RESULT doesn't call any js callback method
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);

            // Setting keepCallback to false would make sure that the callback is deleted from
            // memory after this call
            result.setKeepCallback(false);
            processStartOfDriveCallback.sendPluginResult(result);
        }
        Boolean hasCallback = args.getBoolean(0);
        if (hasCallback) {
            this.processStartOfDriveCallback = callbackContext;
        }
        else {
            this.processStartOfDriveCallback = null;
        }
    }

    protected void setProcessEndOfDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        if (null != this.processEndOfDriveCallback) {
            // Delete old callback
            // Sending NO_RESULT doesn't call any js callback method
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);

            // Setting keepCallback to false would make sure that the callback is deleted from
            // memory after this call
            result.setKeepCallback(false);
            processEndOfDriveCallback.sendPluginResult(result);
        }
        Boolean hasCallback = args.getBoolean(0);
        if (hasCallback) {
            this.processEndOfDriveCallback = callbackContext;
        }
        else {
            this.processEndOfDriveCallback = null;
        }
    }

    protected void setProcessResumeDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        if (null != this.processResumeDriveCallback) {
            // Delete old callback
            // Sending NO_RESULT doesn't call any js callback method
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);

            // Setting keepCallback to false would make sure that the callback is deleted from
            // memory after this call
            result.setKeepCallback(false);
            processResumeDriveCallback.sendPluginResult(result);
        }
        Boolean hasCallback = args.getBoolean(0);
        if (hasCallback) {
            this.processResumeDriveCallback = callbackContext;
        }
        else {
            this.processResumeDriveCallback = null;
        }
    }

    /**
     * Implementation of ZendriveIntentService _____________________________
     * onDriveStart
     * onDriveEnd
     * onAccident (stub only)
     * onLocationPermissionsChange (stub only)
     * onLocationSettingsChange (stub only)
     */

    @Override
    public void onDriveStart(DriveStartInfo driveStartInfo) {
        if (processStartOfDriveCallback == null || processStartOfDriveCallback.isFinished()) {
            return;
        }
        try {
            JSONObject driveStartInfoObject = new JSONObject();
            driveStartInfoObject.put(kStartTimestampKey, driveStartInfo.startTimeMillis);

            if (null != driveStartInfo.startLocation) {
                JSONObject driveStartLocationObject = new JSONObject();
                driveStartLocationObject.put(kLatitudeKey, driveStartInfo.startLocation.latitude);
                driveStartLocationObject.put(kLongitudeKey, driveStartInfo.startLocation.longitude);
                driveStartInfoObject.put(kStartLocationKey, driveStartLocationObject);
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveStartInfoObject);
            result.setKeepCallback(true);
            processStartOfDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * onDriveResume _____________________________
     *
     * DriveResumeInfo: {
     *     long driveGapEndTimestampMillis,
     *     long driveGapStartTimestampMillis,
     *     String driveId,
     *     String sessionId,
     *     long startTimeMillis,
     *     String trackingId
     *  }
     */
    @Override
    public void onDriveResume(DriveResumeInfo driveResumeInfo) {
        if (processResumeDriveCallback == null || processResumeDriveCallback.isFinished()) {
            return;
        }
        try {
            JSONObject driveResumeInfoObject = new JSONObject();
            driveResumeInfoObject.put(kStartTimestampKey, driveResumeInfo.startTimeMillis);
            driveResumeInfoObject.put(kDriveGapEndKey, driveResumeInfo.driveGapEndTimestampMillis);
            driveResumeInfoObject.put(kDriveGapStartKey, driveResumeInfo.driveGapStartTimestampMillis);
            driveResumeInfoObject.put(kDriveId, driveResumeInfo.driveId);
            driveResumeInfoObject.put(kDriveSessionId, driveResumeInfo.sessionId);

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveResumeInfoObject);
            result.setKeepCallback(true);
            processResumeDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {
        if (processEndOfDriveCallback == null || processEndOfDriveCallback.isFinished()) {
            return;
        }
        try {
            JSONObject driveInfoObject = new JSONObject();
            driveInfoObject.put(kStartTimestampKey, driveInfo.startTimeMillis);
            driveInfoObject.put(kEndTimestampKey, driveInfo.endTimeMillis);
            driveInfoObject.put(kIsValidKey, true);
            driveInfoObject.put(kAverageSpeedKey, driveInfo.averageSpeed);
            driveInfoObject.put(kDistanceKey, driveInfo.distanceMeters);

            if (null != driveInfo.waypoints) {
                JSONArray waypointsArray = new JSONArray();

                int waypointsCount = driveInfo.waypoints.size();
                for (int i = 0; i<waypointsCount; i++) {
                    LocationPoint locationPoint = driveInfo.waypoints.get(i);

                    JSONObject driveLocationObject = new JSONObject();
                    driveLocationObject.put(kLatitudeKey, locationPoint.latitude);
                    driveLocationObject.put(kLongitudeKey, locationPoint.longitude);
                    waypointsArray.put(driveLocationObject);
                }
                driveInfoObject.put(kWaypointsKey, waypointsArray);
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveInfoObject);
            result.setKeepCallback(true);
            processEndOfDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccident(AccidentInfo accidentInfo) {

    }

    @Override
    public void onLocationSettingsChange(boolean enabled) {

    }

    @Override
    public void onLocationPermissionsChange(boolean granted) {

    }

    /** END Implementation of ZendriveIntentService --------------------------- */

    /**
     * Implemetation of missing "getDriverAttrsFromJsonObject" method (possibly) removed in recent versions
     */
    private ZendriveDriverAttributes getDriverAttrsFromJsonObject(JSONObject configJsonObj) {
        ZendriveDriverAttributes driverAttributes = new ZendriveDriverAttributes();

        String[] keys = {"firstName", "lastName", "email", "gropuId", "phoneNumber", "startDate", "serviceLevel"};

        for (String key : keys) {
            try {
                String value = configJsonObj.getString(key);

                if (isNull(value)) {
                    continue;
                }

                if (key.equals("firstName")) {
                    driverAttributes.setFirstName(value);
                } else if (key.equals("lastName")) {
                    driverAttributes.setLastName(value);
                } else if (key.equals("email")) {
                    driverAttributes.setEmail(value);
                } else if (key.equals("gropuId")) {
                    driverAttributes.setGroup(value);
                } else if (key.equals("phoneNumber")) {
                    driverAttributes.setPhoneNumber(value);
                } else if (key.equals("startDate")) {
                    driverAttributes.setDriverStartDate(new java.util.Date(value));
                } else if (key.equals("serviceLevel")) {
                    driverAttributes.setServiceLevel(ZendriveDriverAttributes.ServiceLevel.valueOf(value));
                } else {
                    driverAttributes.setCustomAttribute(key, value);
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }

        return driverAttributes;
    }

    // UTILITY METHODS
    private Boolean isNull(Object object) {
        return ((object == null) || JSONObject.NULL.equals(object));
    }

    private Object getObjectFromJSONObject(JSONObject jsonObject, String key) throws JSONException {
        if (hasValidValueForKey(jsonObject, key)) {
            return jsonObject.get(key);
        }
        return null;
    }

    private Boolean hasValidValueForKey(JSONObject jsonObject, String key) {
        return (jsonObject.has(key) && !jsonObject.isNull(key));
    }

    private String getDriverId(JSONObject configJsonObj) throws JSONException {
        Object driverIdObj = getObjectFromJSONObject(configJsonObj, "driverId");
        String driverId = null;
        if (!isNull(driverIdObj)) {
            driverId = driverIdObj.toString();
        }
        return driverId;
    }

    private String getApplicationKey(JSONObject configJsonObj) throws JSONException {
        Object applicationKeyObj = getObjectFromJSONObject(configJsonObj, "applicationKey");
        String applicationKey = null;
        if (!isNull(applicationKeyObj)) {
            applicationKey = applicationKeyObj.toString();
        }
        return applicationKey;
    }

    private CallbackContext processStartOfDriveCallback;
    private CallbackContext processEndOfDriveCallback;
    private CallbackContext processResumeDriveCallback;

    // ZendriveLocationPoint dictionary keys
    private static final String kLatitudeKey = "latitude";
    private static final String kLongitudeKey = "longitude";

    // ZendriveDriveStartInfo dictionary keys
    private static final String kStartTimestampKey = "startTimestamp";
    private static final String kStartLocationKey = "startLocation";

    // DriveResumeInfo dictionary keys
    private static final String kDriveGapEndKey = "driveGapEndTime";
    private static final String kDriveGapStartKey = "driveGapStartTime";
    private static final String kDriveId = "driveId";
    private static final String kDriveSessionId = "driveSessionId";


    // ZendriveDriveInfo dictionary keys
    private static final String kIsValidKey = "isValid";
    private static final String kEndTimestampKey = "endTimestamp";
    private static final String kAverageSpeedKey = "averageSpeed";
    private static final String kDistanceKey = "distance";
    private static final String kWaypointsKey = "waypoints";

    // ZendriveDriverAttributes dictionary keys
    private static final String kCustomAttributesKey = "customAttributes";
    private static final String kDriverAttributesKey = "driverAttributes";

    private static final String kDriveDetectionModeKey = "driveDetectionMode";

}