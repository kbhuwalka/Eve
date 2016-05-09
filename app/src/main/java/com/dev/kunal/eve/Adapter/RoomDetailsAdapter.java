package com.dev.kunal.eve.Adapter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.dev.kunal.eve.BluetoothService;
import com.dev.kunal.eve.Constants;
import com.dev.kunal.eve.R;
import com.dev.kunal.eve.RoomModel;
import com.dev.kunal.eve.Utilities;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Kunal on 5/7/2016.
 */
public class RoomDetailsAdapter extends BaseAdapter {

    private BluetoothService mService;
    private Context mContext;
    public RoomModel mData;

    public RoomDetailsAdapter(Context context, BluetoothService service){
        mService = service;
        mContext = context;
        mData = Utilities.getSelectedRoom();
    }

    @Override
    public int getCount() {
        return mData.pinNumber.length;
    }

    @Override
    public Object getItem(int i) {
        return mData.pinNumber[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = View.inflate(mContext, R.layout.light_item_layout, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder = (ViewHolder) view.getTag();

        String name =mData.pinNames[i];
        holder.name.setText(name);

        final int position = i;

        boolean state = mData.lightStates[i];
        holder.lightSwitch.setChecked(state);

        holder.lightSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(mService.getState() == BluetoothService.STATE_CONNECTED){
                    ByteBuffer buffer = ByteBuffer.allocate(Constants.OUTPUT_BUFFER_SIZE);
                    buffer.put(mData.pinNumber[position]);

                    long currentTime = System.currentTimeMillis();
                    buffer.putLong(currentTime);

                    mService.write(buffer.array());
                    mService.write(Constants.REQUEST_UPDATE);
                }
                else{
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
                    mService.start();
                }

                return false;
            }
        });

//        holder.lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });

        return view;
    }

    private class ViewHolder {
        public TextView name;
        public TextView status;
        public Switch lightSwitch;

        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.lightName);
            status = (TextView) view.findViewById(R.id.lightStatus);
            lightSwitch = (Switch) view.findViewById(R.id.lightSwitch);
        }

    }

}
