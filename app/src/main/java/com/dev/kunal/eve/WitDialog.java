package com.dev.kunal.eve;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Kunal on 5/9/2016.
 */
public class WitDialog extends DialogFragment {

    private View rootView;
    public TextView statusTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        rootView = inflater.inflate(R.layout.dialog_wit, null);
        statusTextView = (TextView) rootView.findViewById(R.id.witStatusTextView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);

        return builder.create();
    }
}
