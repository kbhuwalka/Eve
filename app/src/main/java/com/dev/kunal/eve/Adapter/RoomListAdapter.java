package com.dev.kunal.eve.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.kunal.eve.Constants;
import com.dev.kunal.eve.R;
import com.dev.kunal.eve.View.RoomDetails;
import com.dev.kunal.eve.RoomModel;
import com.dev.kunal.eve.Utilities;

import java.util.ArrayList;

/**
 * Created by Kunal on 4/20/2016.
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RoomModel> mData;
    private static LayoutInflater inflater;


    public RoomListAdapter(Context context){
        mContext = context;
        mData = Constants.roomList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mData.clear();

        RoomModel room = new RoomModel("My room");
        room.setConnectionType(RoomModel.CONNECTION_WIFI);
        room.setLockStatus(RoomModel.SECURITY_LOCKED);
        room.setTemperature(72);
        mData.add(room);

        room = new RoomModel("Kitchen");
        room.setConnectionType(RoomModel.CONNECTION_NONE);
        room.setLockStatus(RoomModel.SECURITY_UNLOCKED);
        mData.add(room);

        room = new RoomModel("Garage");
        room.setConnectionType(RoomModel.CONNECTION_BLUETOOTH);
        room.setLockStatus(RoomModel.SECURITY_LOCKED);
        room.setTemperature(80);
        mData.add(room);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rooms_list_item_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RoomModel room = mData.get(position);


        holder.roomNameTextView.setText(room.getName());
        holder.roomNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RoomDetails.class);
                Utilities.setSelectedRoom(mData.get(position));
                mContext.startActivity(intent);
            }
        });

        String quickTemperature = Utilities.formatQuickTemperature(room.getTemperature());
        holder.quickTemperatureTextView.setText(quickTemperature);

        String securityStatus = Utilities.formatSecurityStatus(room.getLockStatus());
        holder.securityStatusTextView.setText(securityStatus);

        int icon = Utilities.getConnectionIconId(room.getConnectionType());
        holder.connectionTypeIcon.setImageResource(icon);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public static TextView roomNameTextView;
        public static TextView quickTemperatureTextView;
        public static TextView securityStatusTextView;
        public static ImageView connectionTypeIcon;

        public ViewHolder(View view){
            super(view);
            roomNameTextView = (TextView) view.findViewById(R.id.roomName);
            quickTemperatureTextView = (TextView) view.findViewById(R.id.quickTemperatureTextView);
            securityStatusTextView = (TextView) view.findViewById(R.id.seurityStatus);
            connectionTypeIcon = (ImageView) view.findViewById(R.id.connectionStatusIcon);
        }
    }
}
