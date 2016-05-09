package com.dev.kunal.eve;

/**
 * Created by Kunal on 4/20/2016.
 */
public class RoomModel {
    private final String mName;
    private boolean isLocked;
    private int mConnectionType;
    private float mTemperature;
    private float mAmbientLight;

    public long lastUpdated[];
    public boolean lightStates[];
    public byte pinNumber[] = {
            8, 9, 10, 11, 12, 13
    };
    public String pinNames[] = {
            "Light 1",
            "Main Light",
            "Night Light",
            "Fan",
            "Light 2",
            "Light 3"
    };

    public static final int CONNECTION_NONE = 0;
    public static final int CONNECTION_BLUETOOTH = 1;
    public static final int CONNECTION_WIFI = 2;

    public static  final boolean SECURITY_LOCKED = true;
    public static final boolean SECURITY_UNLOCKED = false;

    public RoomModel(String name){
        mName = name;
        isLocked = false;
        mConnectionType = CONNECTION_NONE;

        lastUpdated = new long[pinNumber.length];

        lightStates = new boolean[pinNumber.length];
        for(int i =0; i < lightStates.length;i++)
            lightStates[i] = false;
    }

    public void setLockStatus(boolean status){
        isLocked = status;
    }

    public void setConnectionType(int type){
        mConnectionType = type;
    }

    public void setTemperature(float temperature){
        mTemperature = temperature;
    }

    public void setAmbientLight(float light){
        mAmbientLight = light;
    }

    public String getName(){
        return  mName;
    }

    public boolean getLockStatus(){
        return  isLocked;
    }

    public int getConnectionType(){
        return mConnectionType;
    }

    public float getTemperature(){
        return mTemperature;
    }

    public float getAmbientLight(){
        return mAmbientLight;
    }

}
