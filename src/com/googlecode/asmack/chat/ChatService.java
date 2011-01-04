package com.googlecode.asmack.chat;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.asmack.client.AsmackClientService;
import com.googlecode.asmack.connection.IXmppTransportService;

/**
 * The basic chat server holding an asmack client.
 */
public class ChatService extends AsmackClientService {

    public ChatService() {
        super(R.xml.smackproviders);
    }

    @Override
    public void onTrasportServiceConnect(IXmppTransportService service) {
    }

    @Override
    public void onTrasportServiceDisconnect(IXmppTransportService service) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void preClientStart() {
        client.registerListener(
            new ChatNotificationListener(getApplicationContext())
        );
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("/CHAT//", " START");
    }

}
