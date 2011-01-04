package com.googlecode.asmack.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A boot receiver to ensure that the chat service is running.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    /**
     * Start the chat service on boot.
     * @param context The current context.
     * @param intent The boot completed intent.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent transportService = new Intent();
        transportService.setAction(IChatService.class.getCanonicalName());
        context.startService(transportService);
    }

}
