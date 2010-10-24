package com.googlecode.asmack.chat;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.asmack.Attribute;
import com.googlecode.asmack.Stanza;
import com.googlecode.asmack.connection.IXmppTransportService;

/**
 * Main chat activity.
 */
public class Chat extends Activity implements OnClickListener {

    /**
     * Logging tag, ChatActivity.
     */
    private static final String TAG = Chat.class.getSimpleName();

    /**
     * ID string used as message stanza prefix (app + random).
     */
    private static final String ID =
        TAG + "-" +
        Integer.toHexString((int)(Math.random() * 255.9999));

    /**
     * Stanza unique id.
     */
    private static final AtomicInteger atomicInt = new AtomicInteger();

    /**
     * The text input field.
     */
    private EditText input;

    /**
     * The xmpp service.
     */
    private IXmppTransportService service;

    /**
     * XMLPullParser factory to generate a parser for messages.
     */
    private XmlPullParserFactory xmlPullParserFactory;

    /**
     * The remote jid of this chat.
     */
    private String to;

    /**
     * The local account jid of this chat.
     */
    private String from;

    /**
     * Full local user jid, if available.
     */
    public String fullJid;

    /**
     * XmppServiceConnection that will automatically retrieve the full jid on
     * bind.
     */
    private class XmppServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Chat.this.service = IXmppTransportService.Stub.asInterface(service);
            try {
                Chat.this.fullJid = Chat.this.service.getFullJidByBare(from);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            finish();
        }
    }

    /**
     * Initialize the members of this activity and bind to the xmpp transport
     * service.
     * @param savedInstanceState The save state of this activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            // ?!?
            Log.e(TAG, "Can't intatiate xmlPullParser");
            finish();
            return;
        }
        xmlPullParserFactory.setNamespaceAware(true);
        xmlPullParserFactory.setValidating(false);

        Intent serviceIntent =
            new Intent(IXmppTransportService.class.getCanonicalName());
        bindService(
            serviceIntent,
            new XmppServiceConnection(),
            0
        );
        startService(serviceIntent);
        Intent intent = getIntent();
        String toFrom = intent.getData().getPathSegments().get(0);
        Log.d("Chat", toFrom);
        String splitToFrom[] = toFrom.split("/");
        to = splitToFrom[0];
        from = splitToFrom[1];

        setContentView(R.layout.main);

        TextView fromTextView = (TextView) findViewById(R.id.FromTextView);
        fromTextView.setText(from);
        TextView toTextView = (TextView) findViewById(R.id.ToTextView);
        toTextView.setText(to);
        Database.getDatabase(getApplicationContext(), null);
        ListView history = (ListView) findViewById(R.id.ChatHistoryList);
        history.setItemsCanFocus(false);
        history.setAdapter(
            new HistoryCursorAdapter(
                this,
                from,
                HistoryCursorAdapter.query(this, from, to),
                true
            )
        );
        Button send = (Button) findViewById(R.id.ChatButtonSend);
        send.setOnClickListener(this);
        input = (EditText) findViewById(R.id.ChatInput);
        input.setHint("Send " + XMPPUtils.getUser(to) + " a message");
    }

    /**
     * Clicked on button click, creating a new xmpp stanza and sending it
     * to the remote jid.
     * @param v The view catching the click, ignored.
     */
    @Override
    public void onClick(View v) {
        if (input == null) {
            Log.d(TAG, "input is null");
            return;
        }
        if (service == null) {
            Log.d(TAG, "service is null");
            return;
        }
        String msg = input.getEditableText().toString();
        StringWriter xml = new StringWriter();
        try {
            XmlSerializer serializer = xmlPullParserFactory.newSerializer();
            serializer.setOutput(xml);
            serializer.startTag(null, "message");
            serializer.startTag(null, "body");
            serializer.text(msg);
            serializer.endTag(null, "body");
            serializer.endTag(null, "message");
            serializer.flush();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute("type", "", "chat"));
        attributes.add(new Attribute("from", "", fullJid));
        attributes.add(new Attribute("to", "", this.to));
        attributes.add(new Attribute(
                "id",
                "",
                ID + "-" + Integer.toHexString(atomicInt.incrementAndGet()))
        );
        try {
            service.send(
                new Stanza("message", "", from, xml.toString(), attributes)
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put("ts", System.currentTimeMillis());
        values.put("via", from);
        values.put("jid", to);
        values.put("dst", to);
        values.put("src", from);
        values.put("msg", msg);
        Database.getDatabase(null, null).insert("msg", "_id", values);
        Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority("jabber-chat-db");
        builder.appendPath(from);
        builder.appendPath(to);
        getApplicationContext()
            .getContentResolver()
            .notifyChange(builder.build(), null);
        input.setText("");
    }

}
