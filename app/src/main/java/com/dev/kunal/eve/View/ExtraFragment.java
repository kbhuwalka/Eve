package com.dev.kunal.eve.View;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.kunal.eve.Adapter.RoomDetailsAdapter;
import com.dev.kunal.eve.BluetoothService;
import com.dev.kunal.eve.Constants;
import com.dev.kunal.eve.R;
import com.dev.kunal.eve.RoomModel;
import com.dev.kunal.eve.Utilities;
import com.dev.kunal.eve.WitDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import ai.wit.sdk.IWitListener;
import ai.wit.sdk.Wit;
import ai.wit.sdk.model.WitOutcome;

/**
 * Created by Kunal on 4/15/2016.
 */
public class ExtraFragment extends Fragment implements IWitListener {

    public Wit wit;

    private static final String TAG = ExtraFragment.class.getSimpleName();

    //Intent Request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mService = null;
    private BluetoothDevice mDevice = null;

    private ListView mListView;
    private RoomDetailsAdapter mAdapter;
    private Button button;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get local BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        String accessToken = "THK3EINKXPGNID5XU5WSVWDFJDLGHE7D";
        wit = new Wit(accessToken, this);
        wit.enableContextLocation(getActivity().getApplicationContext());

        //If Bluetooth is not supported exit the activity
        if(mBluetoothAdapter == null){
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extra, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.listView);

    }

    @Override
    public void onStart() {
        super.onStart();
        //If Bluetooth is not on, request for it to be switched on
        //The connection is setup during onActivityResult
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        } else if(mService == null){
            setupService();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(mService != null){
//            //STATE_NONE means no connection is established or in progress
//            if(mService.getState() == BluetoothService.STATE_NONE)
//                mService.start();
//        }

        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mService != null)
            mService.stop();

        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    private void setupService() {
        mService = new BluetoothService(getActivity(), mHandler);
        mAdapter = new RoomDetailsAdapter(getContext(), mService);
        mListView.setAdapter(mAdapter);
    }

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    //Validate and update
                    byte[] readBytes = Arrays.copyOf(readBuffer, msg.arg1);
                    totalRead += msg.arg1;
                    buffer.put(readBytes);
                    if(totalRead == Constants.INPUT_BUFFER_SIZE){
                        totalRead = 0;
                        updateData(buffer);
                        buffer.clear();
                    }
                    Log.i(TAG, "Input: " + Arrays.toString(readBytes) + " Length: " + msg.arg1);
                    break;
            }
        }

        private int totalRead = 0;
        private ByteBuffer buffer = ByteBuffer.allocate(Constants.INPUT_BUFFER_SIZE);
    };

    private void updateData(ByteBuffer buffer) {
        if(buffer.get(0) != '#') {
            mService.write(Constants.REQUEST_UPDATE);
            return;
        }
        RoomModel room = mAdapter.mData;
        buffer.position(1);
        for(int i =0 ; i < room.lightStates.length; i++)
            room.lightStates[i] = buffer.get() != 0;
        for(int i =0; i < room.lastUpdated.length; i++ )
            room.lastUpdated[i] = buffer.getLong();
        mAdapter.notifyDataSetChanged();

    }


    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
                mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(mDevice != null){
                Log.i(TAG, "Found device: " + mDevice);
                if(mDevice.getAddress().equals("20:15:05:08:70:26"))
                    mService.connect(mDevice);
                else
                    mDevice = null;
            }
        }
    };

    @Override
    public void witDidGraspIntent(ArrayList<WitOutcome> arrayList, String s, Error error) {

        final String KEY_OUTCOMES = "outcomes";
        final String KEY_ENTITIES = "entities";



        Gson gson = new GsonBuilder().create();
        String output = gson.toJson(arrayList);
        JSONObject response = null;
        JSONObject entities = null;

        try {
             response = new JSONArray(output).getJSONObject(0);
            if(response != null)
                entities = response.getJSONObject(KEY_ENTITIES);

            if(entities != null)
                SetDeviceStates(entities);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateDialog(arrayList.get(0).get_text());
    }

    private void SetDeviceStates(JSONObject entities) throws JSONException {

        final String KEY_DEVICE = "device";
        final String KEY_ON_OFF = "on_off";

        JSONObject device= entities.getJSONArray(KEY_DEVICE).getJSONObject(0);
        JSONObject on_off = entities.getJSONArray(KEY_ON_OFF).getJSONObject(0);

        if(device != null){
            RoomModel room = mAdapter.mData;
            String deviceName = device.getString("value");
            boolean state = Utilities.convertStringToState(on_off.getString("value"));
            int[] pinsToUpdate = Utilities.getPinsForUpdate(room.pinNames, deviceName);

            if(mService.getState() == BluetoothService.STATE_CONNECTED){

                long currentTime = System.currentTimeMillis();

                for(int i=0; i < pinsToUpdate.length; i++){
                    int index = pinsToUpdate[i];

                    if(state == room.lightStates[index]) continue;

                    ByteBuffer buffer = ByteBuffer.allocate(Constants.OUTPUT_BUFFER_SIZE);
                    buffer.put(room.pinNumber[index]);
                    buffer.putLong(currentTime);

                    mService.write(buffer.array());
                }

                mService.write(Constants.REQUEST_UPDATE);
            }
        }

    }

    @Override
    public void witDidStartListening() {

        updateDialog("Speak into the mic.");
    }

    private void updateDialog(String text) {
        TextView textView = Utilities.witDialog.statusTextView;
        if(textView != null)
                textView.setText(text);
    }

    @Override
    public void witDidStopListening() {
        updateDialog("Processing...");
    }

    @Override
    public void witActivityDetectorStarted() {
        updateDialog("Listening...");
    }

    @Override
    public String witGenerateMessageId() {
        return null;
    }
}
