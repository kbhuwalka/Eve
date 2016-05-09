package com.dev.kunal.eve;

import android.app.DialogFragment;

/**
 * Created by Kunal on 4/20/2016.
 */
public class Utilities {

    private static RoomModel selectedRoom;

    public static WitDialog witDialog;

    public static String formatQuickTemperature(float temperature) {
        return "" + Math.round(temperature);
    }

    public static String formatSecurityStatus(boolean lockStatus) {
        if(lockStatus)
            return "Locked";
        else
            return "Unlocked";
    }

    public static int getConnectionIconId(int connectionType) {
        switch (connectionType){
            case RoomModel.CONNECTION_BLUETOOTH:
                return R.drawable.ic_bluetooth_connected_black_24dp;
            case RoomModel.CONNECTION_WIFI:
                return R.drawable.ic_network_wifi_black_24dp;
            case RoomModel.CONNECTION_NONE:
            default:
                return R.drawable.ic_signal_wifi_off_black_24dp;
        }
    }

    public static void setSelectedRoom(RoomModel room){
        selectedRoom = room;
    }

    public static RoomModel getSelectedRoom(){
        return selectedRoom;
    }

    public static boolean convertStringToState(String value) {
        return (value.equals("on"));
    }

    public static int[] getPinsForUpdate(String[] pinNames, String deviceName) {
        if(deviceName.equals("all"))
            return new int[] { 0, 1, 2, 3, 4, 5};

        for(int i = 0; i < pinNames.length; i++){
            if(pinNames[i].equals(deviceName))
                return new int[] { i };
        }

        return new int[0];
    }
}
