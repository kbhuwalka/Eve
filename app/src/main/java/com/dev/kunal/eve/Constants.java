package com.dev.kunal.eve;

import java.util.ArrayList;

/**
 * Created by Kunal on 4/15/2016.
 */
public interface Constants {

    //Message types for Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    public static final byte[] REQUEST_UPDATE = new byte[] { 127 };

    public static ArrayList<RoomModel> roomList = new ArrayList<RoomModel>();

    public static final int OUTPUT_BUFFER_SIZE = 9;
    public static final int INPUT_BUFFER_SIZE = 55;
}
