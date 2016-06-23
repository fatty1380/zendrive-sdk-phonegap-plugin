package com.zendrive.phonegap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by chandan on 11/3/14.
 */
public class ZendriveCordovaPlugin extends CordovaPlugin {

    ZendriveCordovaService zendriveService = new ZendriveCordovaService("zdCordovaService");

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (action.equals("setup")) {
            zendriveService.setup(args, callbackContext, this.cordova);
            return true;
        } else if (action.equals("teardown")) {
            zendriveService.teardown(args, callbackContext);
            return true;
        } else if (action.equals("startDrive")) {
            zendriveService.startDrive(args, callbackContext);
            return true;
        } else if (action.equals("stopDrive")) {
            zendriveService.stopDrive(args, callbackContext);
            return true;
        } else if (action.equals("startSession")) {
            zendriveService.startSession(args, callbackContext);
            return true;
        } else if (action.equals("stopSession")) {
            zendriveService.stopSession(args, callbackContext);
            return true;
        } else if (action.equals("setDriveDetectionMode")) {
            zendriveService.setDriveDetectionMode(args, callbackContext);
            return true;
        } else if (action.equals("setProcessStartOfDriveDelegateCallback")) {
            zendriveService.setProcessStartOfDriveDelegateCallback(args, callbackContext);
            return true;
        } else if (action.equals("setProcessEndOfDriveDelegateCallback")) {
            zendriveService.setProcessEndOfDriveDelegateCallback(args, callbackContext);
            return true;
        } else if (action.equals("setProcessResumeDriveDelegateCallback")) {
            zendriveService.setProcessResumeDriveDelegateCallback(args, callbackContext);
            return true;
        }
        return false;
    }

}