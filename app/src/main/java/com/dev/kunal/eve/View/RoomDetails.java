package com.dev.kunal.eve.View;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.kunal.eve.R;
import com.dev.kunal.eve.Utilities;
import com.dev.kunal.eve.WitDialog;

import java.io.IOException;

public class RoomDetails extends AppCompatActivity {

    private ExtraFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);


        mFragment = new ExtraFragment();
        getSupportFragmentManager().beginTransaction()
                .add(mFragment, "Extra_Fragment")
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_speech:
                DialogFragment dialogFragment = new WitDialog();
                Utilities.witDialog = (WitDialog) dialogFragment;
                try {
                    mFragment.wit.toggleListening();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialogFragment.show(getFragmentManager(), "tag");
                break;
        }

        return false;
    }

}
