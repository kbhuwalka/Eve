package com.dev.kunal.eve.View;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dev.kunal.eve.Adapter.RoomListAdapter;
import com.dev.kunal.eve.R;

/**
 * Created by Kunal on 4/19/2016.
 */
public class RoomListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private BluetoothAdapter mBluetoothAdapter;
    private RoomListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get local BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        return inflater.inflate(R.layout.rooms_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new RoomListAdapter(getContext());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.room_list_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

    }
}
