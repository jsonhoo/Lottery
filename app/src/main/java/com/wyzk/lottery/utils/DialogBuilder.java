/*
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 */

package com.wyzk.lottery.utils;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.wyzk.lottery.R;


public final class DialogBuilder {
    private DialogBuilder() {
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);


        alertDialog.setCancelable(true);
        return alertDialog.create();
    }

    public static Dialog createSimpleDialog(final Context context, String title, String message, final boolean enableBt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)

                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        return;
                    }
                })
                .setNegativeButton(R.string.dialog_action_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (enableBt) {
                                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (!mBluetoothAdapter.isEnabled()) {
                                        mBluetoothAdapter.enable();
                                    }
                                } else {
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    context.startActivity(intent);

                                }
                            }
                        });

        alertDialog.setCancelable(true);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        return alertDialog.create();
    }

}
