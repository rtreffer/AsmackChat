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

    /**
     * Create a new chat service.
     */
    public ChatService() {
        super(R.xml.smackproviders);
    }

    /**
     * Called whenever the transport service is reachable.
     * @param service The remote xmpp transport service.
     */
    @Override
    public void onTrasportServiceConnect(IXmppTransportService service) {
    }

    /**
     * Called whenever the transport service connection dies.
     * @param service The remote xmpp transport service.
     */
    @Override
    public void onTrasportServiceDisconnect(IXmppTransportService service) {
    }

    /**
     * Called whenever another context based android class tries to bind to this
     * service. Currently return null.
     * @param intent The incoming intent, ignored.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called whenever a client becomes ready but is not yet registered. This
     * method wires up listeners to the client.
     */
    @Override
    protected void preClientStart() {
        client.registerListener(
            new ChatNotificationListener(getApplicationContext())
        );
    }

    /**
     * Called when this service starts.
     * @param intent The incoming intent.
     * @param startId The start id.
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("/CHAT//", " START");
    }

}
