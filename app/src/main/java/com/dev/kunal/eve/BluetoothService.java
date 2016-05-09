package com.dev.kunal.eve;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


/**
 * Created by Kunal on 4/15/2016.
 */
public class BluetoothService {
    //Debugging Tag
    private static final String TAG = "BluetoothService";

    //UUID for the app
    private static final UUID MY_UUID =
            UUID.fromString("b9065355-bc29-4e2d-aed2-2fd4b04e4d15");

    //Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    //Constants indicating the current state
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    /**
     * Prepares a new Connection
     */
    public BluetoothService(Context context, Handler handler){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the connection
     */
    private synchronized void setState(int state){
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        //Pass the new state to the handler
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current state of the connection
     */
    public synchronized int getState(){
        return mState;
    }

    /**
     * Cancel ConnectThread and set it to null;
     */
    private void resetConnectThread(){
        //Cancel any thread attempting to make a connection
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

    /**
     * Cancel ConnectedThread and set it to null
     */
    private void resetConnectedThread(){
        //Cancel any thread running a connection
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * Start the connection.
     * Sets the connection in listening mode for incoming connections through AcceptThread.
     */
    public synchronized void start(){
        Log.d(TAG, "Start");

        resetConnectThread();
        resetConnectedThread();

        setState(STATE_NONE);
    }

    /**
     * Start the ConnectThread to initiate a connection
     */
    public synchronized void connect(BluetoothDevice device){
        Log.d(TAG, "Connect to:" + device);

        resetConnectThread();
        resetConnectedThread();

        //Connect to the given device on a new thread
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Starts a ConnectThread to manage a Bluetooth connection
     */
    private synchronized void connected(BluetoothDevice device, BluetoothSocket socket){
        Log.d(TAG, "Connected to device: " + device);

        resetConnectThread();
        resetConnectThread();

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized  void stop(){
        Log.d(TAG, "Stop");

        resetConnectThread();
        resetConnectedThread();

        //Cancel any thread that is running a connection
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread
     */
    public void write(byte[] out){
        //Temporary Object
        ConnectedThread ct;
        //Synchronize the ConnectThread object
        synchronized (this){
            if (mState != STATE_CONNECTED) return;
            ct = mConnectedThread;
        }

        //Perform write operation
        ct.write(out);
    }

    /**
     * This thread runs while attempting to make a connection with a device.
     * The connection either succeeds or fails
     */
    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            mmDevice = device;
            BluetoothSocket temp = null;

            //Get a BluetoothSocket with the given device
            try{
                //temp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                temp = (BluetoothSocket) m.invoke(device, 1);
            } catch (Exception e) {
                Log.e(TAG, "Socket creation failed");
            }
            mmSocket = temp;
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectThread for " + mmDevice);
            setName("ConnectThread "+ mmDevice);

            //Cancel discovery as it slows down connection
            mAdapter.cancelDiscovery();

            //Make a connection to the BluetoothSocket
            try{
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "Connection failed. Attempting to close socket...");

                //Close the socket
                try{
                    mmSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "Unable to close socket during connection failure");
                }
                Log.i(TAG, "Successfully closed socket during connection failure.");
            }

            //Reset the ConnectThread because the connection is completed
            synchronized (BluetoothService.this){
                mConnectThread = null;
            }

            //Start the connected thread
            connected(mmDevice, mmSocket);
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close socket");
            }
        }
    }

    /**
     * This thread runs during a connection with a device and handles
     * all incoming/outgoing transmissions.
     */
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "Created a ConnectThread");
            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            //Get the input and output streams
            try{
                tempIn = mmSocket.getInputStream();
                tempOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Failed to get streams from socket");
            }

            mmInStream = tempIn;
            mmOutStream = tempOut;
        }

        public void run(){
            Log.i(TAG, "BEGIN ConnectThread");
            byte[] buffer = new byte[128];
            int bytes;

            //Keep listening to InputStream while connected
            while(true){
                try{
                    //Read from input stream
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Disconnected", e);
                    break;
                }
            }
        }

        /**
         * Write to output stream
         */
        public void write(byte[] buffer){
            try{
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception while writing to output stream");
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connected socket.");
            }
        }

    }

}
